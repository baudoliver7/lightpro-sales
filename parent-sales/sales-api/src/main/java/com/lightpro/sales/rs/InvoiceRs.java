package com.lightpro.sales.rs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.sales.cmd.CancelInvoiceEdited;
import com.lightpro.sales.cmd.DownPaymentAmountEdited;
import com.lightpro.sales.cmd.DownPaymentPercentEdited;
import com.lightpro.sales.cmd.EscompteEdited;
import com.lightpro.sales.cmd.InvoiceEdited;
import com.lightpro.sales.cmd.OrderProductEdited;
import com.lightpro.sales.cmd.PaymentEdited;
import com.lightpro.sales.cmd.RabaisEdited;
import com.lightpro.sales.cmd.RemiseAmountEdited;
import com.lightpro.sales.cmd.RemisePercentEdited;
import com.lightpro.sales.cmd.RistourneAmountEdited;
import com.lightpro.sales.cmd.RistournePercentEdited;
import com.lightpro.sales.cmd.TaxEdited;
import com.lightpro.sales.cmd.ValidatePayment;
import com.lightpro.sales.vm.InvoiceVm;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.PaymentVm;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceProducts;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.InvoiceRefundReceipt;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Payment;
import com.sales.domains.api.Product;
import com.sales.domains.api.Provision;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.Remise;
import com.sales.domains.impl.ProductNone;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.api.Secured;
import com.securities.api.Tax;

@Path("/invoice")
public class InvoiceRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/status")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllStatus() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(InvoiceStatus.values())
														 .stream()
														 .filter(m -> m.id() > 0)
												 		 .map(m -> new ListValueVm(m.id(), m.toString()))
												 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter,
							@QueryParam("statusId") int statusId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						InvoiceStatus status = InvoiceStatus.get(statusId);
						Invoices container = sales().invoices().of(status);
						
						List<InvoiceVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new InvoiceVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<InvoiceVm> pagedSet = new PaginationSet<InvoiceVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	@GET
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getSingle(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice item = sales().invoices().get(id);

						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{id}/product")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllProducts(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						InvoiceProducts products = sales().invoices().get(id).products();
						
						List<OrderProductVm> items = products.all().stream()
															 .map(m -> new OrderProductVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/product")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addProduct(@PathParam("id") final UUID id, final OrderProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice order = sales().invoices().get(id);
						
						Product product = sales().products().get(cmd.productId());
						
						List<Tax> taxes = new ArrayList<Tax>();
						for (TaxEdited tax : cmd.taxes()) {
							taxes.add(sales().taxes().get(tax.id()));
						}
						
						Remise remise = product.pricing().remise();
						OrderProduct op = order.products().add(product.category(), product, cmd.name(), cmd.description(), cmd.quantity(), cmd.unitPrice(), remise, taxes, new ProductNone());	
						
						log.info(String.format("Ajout de la ligne produit %s à la facture N° %s", op.name(), order.reference()));
						return Response.ok(new OrderProductVm(op)).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}/product/{productid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateProduct(@PathParam("id") final UUID id, @PathParam("productid") final UUID productId, final OrderProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice item = sales().invoices().get(id);						
						OrderProduct op = item.products().get(productId);
						
						List<Tax> taxes = new ArrayList<Tax>();
						for (TaxEdited tax : cmd.taxes()) {
							taxes.add(sales().taxes().get(tax.id()));
						}
						
						Remise remise = op.product().pricing().remise();
						op.update(cmd.name(), cmd.description(), cmd.quantity(), cmd.unitPrice(), remise, taxes);
						
						log.info(String.format("Retrait de la ligne produit %s à la facture N° %s", op.name(), item.reference()));
						return Response.ok(new OrderProductVm(op)).build();
					}
				});		
	}
	
	@DELETE
	@Secured
	@Path("/{id}/product/{productid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteProduct(@PathParam("id") final UUID id, @PathParam("productid") final UUID productId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice item = sales().invoices().get(id);
						OrderProduct product = item.products().get(productId);
						item.products().delete(product);
						
						log.info(String.format("Suppression de la ligne produit %s de la facture N° %s", product.name(), item.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@GET
	@Secured
	@Path("/{id}/payment")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPayments(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						
						List<PaymentVm> items = invoice.payments().all().stream()
															 .map(m -> new PaymentVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/payment/cash")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cash(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						
						Payment payment;
						if(cmd.provisionId() != null)
						{
							Provision provision = sales().provisions().build(cmd.provisionId());
							payment = provision.cash(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), invoice, currentUser);													
						}else
							payment = invoice.cash(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference(), currentUser);
																	
						log.info(String.format("Création d'un paiement de la facture N° %s", invoice.reference()));
						return Response.ok(new PaymentVm(payment)).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/payment/{paymentid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response modifyInvoiceCashReceipt(@PathParam("paymentid") final UUID paymentId, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Payment payment = sales().payments().get(paymentId);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						payment.update(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference());
															
						log.info(String.format("Mise à jour d'un paiement de la facture N° %s", payment.origin()));
						return Response.ok(new PaymentVm(payment)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/payment/refund")
	@Produces({MediaType.APPLICATION_JSON})
	public Response refund(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						
						Payment payment = invoice.refund(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference(), currentUser);
						
						log.info(String.format("Création d'un remboursement sur la facture N° %s", invoice.reference()));
						return Response.ok(new PaymentVm(payment)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/cash-receipt/{id}/validate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response validateCashReceipt(@PathParam("id") final UUID id, ValidatePayment cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						InvoiceReceipt item = sales().payments().getInvoiceReceipt(id);
						item.validate(cmd.forcePayment());
						
						log.info(String.format("Validation du paiement N° %s de la %s", item.reference(), item.origin()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/refund-receipt/{id}/validate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response validateRefundReceipt(@PathParam("id") final UUID id, ValidatePayment cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						InvoiceRefundReceipt item = sales().payments().getInvoiceRefundReceipt(id);
						item.validate();
						
						log.info(String.format("Validation du paiement N° %s de la facture N° %s", item.reference(), item.origin()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/validate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response validate(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						invoice.validate();
						
						log.info(String.format("Validation de la facture N° %s", invoice.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-paid")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markPaid(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						invoice.markPaid();
						
						log.info(String.format("Confirmation de paiement totale de la facture N° %s", invoice.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/send-to-compta")
	@Produces({MediaType.APPLICATION_JSON})
	public Response sendToCompta(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						sales().interfacage().comptaInterface().send(invoice, false);
						
						log.info(String.format("Envoi de la facture N° %s à la comptabilité", invoice.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/get-rid")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRid(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						invoice.getRid();
						
						log.info(String.format("Annulation de la facture N° %s", invoice.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final InvoiceEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice item = sales().invoices().get(id);
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						
						item.update(cmd.orderDate(), cmd.paymentCondition(), cmd.origin(), cmd.description(), cmd.notes(), customer, seller);
						
						log.info(String.format("Mise à jour des données d'une facture en mode brouillon"));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}	
	
	@POST
	@Secured
	@Path("/down-payment/amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addDownPaymentAmount(final DownPaymentAmountEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder purchaseOrder = sales().purchases().build(cmd.purchaseOrderId());						
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						
						Invoices invoices = sales().invoices().of(purchaseOrder);
						Invoice invoice = invoices.addDownPayment(LocalDate.now(), cmd.amount(), cmd.origin(), cmd.description(), cmd.notes(), cmd.paymentCondition(), customer, seller);
						
						if(purchaseOrder.isNone())
							log.info(String.format("Création d'une facture d'acompte"));
						else
							log.info(String.format("Création d'une facture d'acompte sur le bon de commande %s", purchaseOrder.reference()));
						
						return Response.ok(new InvoiceVm(invoice)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/down-payment/percent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addDownPaymentPercent(final DownPaymentPercentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder purchaseOrder = sales().purchases().build(cmd.purchaseOrderId());						
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						
						Invoices invoices = sales().invoices().of(purchaseOrder);					
						Invoice invoice = invoices.addDownPayment(LocalDate.now(), cmd.percent(), cmd.base(), cmd.origin(), cmd.description(), cmd.notes(), cmd.paymentCondition(), customer, seller);
						
						if(purchaseOrder.isNone())
							log.info(String.format("Création d'une facture d'acompte"));
						else
							log.info(String.format("Création d'une facture d'acompte sur le bon de commande %s", purchaseOrder.reference()));
						
						return Response.ok(new InvoiceVm(invoice)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/final")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addFinalInvoice(final InvoiceEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder purchaseOrder = sales().purchases().build(cmd.purchaseOrderId());						
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						
						Invoices invoices = sales().invoices().of(purchaseOrder);
						
						Invoice invoice = invoices.addFinalInvoice(cmd.orderDate(), cmd.origin(), cmd.description(), cmd.notes(), cmd.paymentCondition(), customer, seller);
						
						if(purchaseOrder.isNone())
							log.info(String.format("Création d'une facture définitive"));
						else
							log.info(String.format("Création d'une facture définitive sur le bon de commande %s", purchaseOrder.reference()));
						
						return Response.ok(new InvoiceVm(invoice)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/remise/percent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addRemisePercent(@PathParam("id") final UUID id, final RemisePercentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addRemise(cmd.orderDate(), cmd.percent());
						
						log.info(String.format("Création d'une facture d'avoir de type remise sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/remise/amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addRemiseAmount(@PathParam("id") final UUID id, final RemiseAmountEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addRemise(cmd.orderDate(), cmd.amount());
						
						log.info(String.format("Création d'une facture d'avoir de type remise sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/ristourne/percent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addRistournePercent(@PathParam("id") final UUID id, final RistournePercentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addRistourne(cmd.orderDate(), cmd.percent());
						
						log.info(String.format("Création d'une facture d'avoir de type ristourne sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/ristourne/amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addRistourneAmount(@PathParam("id") final UUID id, final RistourneAmountEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addRistourne(cmd.orderDate(), cmd.amount());
						
						log.info(String.format("Création d'une facture d'avoir de type ristourne sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/rabais")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addRabais(@PathParam("id") final UUID id, final RabaisEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addRabais(cmd.orderDate());
						
						log.info(String.format("Création d'une facture d'avoir de type rabais sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/escompte")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addEscompte(@PathParam("id") final UUID id, final EscompteEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addEscompte(cmd.orderDate(), cmd.percent());
						
						log.info(String.format("Création d'une facture d'avoir de type escompte sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/avoir-annulation")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addAvoirAnnulation(@PathParam("id") final UUID id, final CancelInvoiceEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						Invoice originInvoice = sales().invoices().get(id);						
						Invoice item = originInvoice.avoirs().addCancelInvoice(cmd.orderDate());
						
						log.info(String.format("Annulation d'une facture d'avoir sur la facture N° %s", originInvoice.reference()));
						return Response.ok(new InvoiceVm(item)).build();
					}
				});		
	}
	
	@DELETE
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice item = sales().invoices().get(id);
						sales().invoices().delete(item);
						
						log.info(String.format("Suppression d'une facture en mode brouillon"));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
