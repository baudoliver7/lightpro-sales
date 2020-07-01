package com.lightpro.sales.rs;

import java.io.IOException;
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
import com.lightpro.sales.cmd.MarkSoldCmd;
import com.lightpro.sales.cmd.OrderProductEdited;
import com.lightpro.sales.cmd.PaymentEdited;
import com.lightpro.sales.cmd.PurchaseOrderEdited;
import com.lightpro.sales.cmd.TaxEdited;
import com.lightpro.sales.vm.InvoiceVm;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.PaymentVm;
import com.lightpro.sales.vm.PurchaseOrderVm;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.Product;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Remise;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.api.Secured;
import com.securities.api.Tax;

@Path("/purchase-order")
public class PurchaseOrderRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/status")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllStatus() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(PurchaseOrderStatus.values())
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
						
						PurchaseOrderStatus status = PurchaseOrderStatus.get(statusId);
						PurchaseOrders container = sales().purchases().of(status);
						
						List<PurchaseOrderVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new PurchaseOrderVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<PurchaseOrderVm> pagedSet = new PaginationSet<PurchaseOrderVm>(itemsVm, page, count);
						
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
						
						PurchaseOrderVm item = new PurchaseOrderVm(sales().purchases().get(id));

						return Response.ok(item).build();
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
						
						OrderProducts products = sales().purchases().get(id).products();
						
						List<OrderProductVm> items = products.all().stream()
															 .map(m -> new OrderProductVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{id}/invoice")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllInvoices(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoices invoices = sales().purchases().get(id).invoices();
						
						List<InvoiceVm> items = invoices.all().stream()
															 .map(m -> new InvoiceVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final PurchaseOrderEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PurchaseOrder item = sales().purchases().get(id);
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						item.update(cmd.orderDate(), cmd.expirationDate(), cmd.paymentCondition(), cmd.cgv(), cmd.description(), cmd.notes(), customer, seller, cmd.livraisonDelayInDays());
						
						log.info(String.format("Mise à jour des données du devis N°%s", item.reference()));
						return Response.status(Response.Status.OK).build();
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
						
						PurchaseOrder item = sales().purchases().get(id);						
						OrderProduct op = item.products().get(productId);
						
						List<Tax> taxes = new ArrayList<Tax>();
						for (TaxEdited tax : cmd.taxes()) {
							taxes.add(sales().taxes().get(tax.id()));
						}
						
						Remise remise = op.product().pricing().remise();
						op.update(cmd.name(), cmd.description(), cmd.quantity(), cmd.unitPrice(), remise, taxes);
						
						log.info(String.format("Mise à jour de la ligne produit %s du devis N° %s", op.name(), item.reference()));
						return Response.ok(new OrderProductVm(op)).build();
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
						
						PurchaseOrder item = sales().purchases().get(id);
						String reference = item.reference();
						sales().purchases().delete(item);
						
						log.info(String.format("Suppression du devis N° %s", reference));
						return Response.status(Response.Status.OK).build();
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
						
						PurchaseOrder item = sales().purchases().get(id);
						OrderProduct product = item.products().get(productId);
						String name = product.name();
						item.products().delete(product);
						
						log.info(String.format("Suppression de la ligne produit %s du devis N° %s", name, item.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<PurchaseOrderVm> items = sales().purchases().all()
													 .stream()
											 		 .map(m -> new PurchaseOrderVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/payment-condition")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllPaymentConditions() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(PaymentConditionStatus.values())
														 .stream()
														 .filter(m -> m.id() > 0)
												 		 .map(m -> new ListValueVm(m.id(), m.toString()))
												 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final PurchaseOrderEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrders containers = sales().purchases().of(sales().modulePointDeVenteDirecte());
						Contact customer = sales().contacts().build(cmd.customerId());
						Contact seller = sales().contacts().build(cmd.sellerId());
						PurchaseOrder item = containers.add(cmd.orderDate(), cmd.expirationDate(), cmd.paymentCondition(), cmd.cgv(), cmd.description(), cmd.notes(), customer, seller, cmd.livraisonDelayInDays());
						
						log.info(String.format("Création du devis N° %s", item.reference()));
						return Response.ok(new PurchaseOrderVm(item)).build();
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
						
						PurchaseOrder order = sales().purchases().get(id);
						
						Product product = sales().products().get(cmd.productId());
						
						List<Tax> taxes = new ArrayList<Tax>();
						for (TaxEdited tax : cmd.taxes()) {
							taxes.add(sales().taxes().get(tax.id()));
						}
						
						Remise remise = product.pricing().remise();
						OrderProduct op = order.products().add(product.category(), product, cmd.name(), cmd.description(), cmd.quantity(), cmd.unitPrice(), remise, taxes);	
						
						
						log.info(String.format("Ajout de ligne produit %s au devis N° %s", op.name(), order.reference()));
						return Response.ok(new OrderProductVm(op)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/mark-sold")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markSold(@PathParam("id") final UUID id, MarkSoldCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						order.markSold(cmd.soldDate(), false);
						
						log.info(String.format("Confirmation de vente des articles du devis N° %s", order.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/cancel")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cancel(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						String reference = order.reference();
						order.cancel();
						
						log.info(String.format("Annulation du devis N° %s", reference));
						return Response.ok(new PurchaseOrderVm(order)).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/re-open")
	@Produces({MediaType.APPLICATION_JSON})
	public Response reOpen(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						order.reOpen();
						
						log.info(String.format("Ré-ouverture du dévis N° %s", order.reference()));
						return Response.ok(new PurchaseOrderVm(order)).build();
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
						
						PurchaseOrder purchaseOrder = sales().purchases().get(id);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						
						Payment payment;
						payment = purchaseOrder.cash(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference(), purchaseOrder.seller());
																	
						log.info(String.format("Paiement ticket de caisse N° %s pour le bon de commande N° %s", payment.reference(), purchaseOrder.reference()));
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
																	
						log.info(String.format("Mise à jour d'un paiement sur le bon de commande N° %s", payment.origin()));
						return Response.ok(new PaymentVm(payment)).build();
					}
				});		
	}
}
