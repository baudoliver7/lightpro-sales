package com.lightpro.sales.rs;

import java.io.IOException;
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
import com.lightpro.sales.cmd.PaymentEdited;
import com.lightpro.sales.cmd.ProvisionEdited;
import com.lightpro.sales.vm.PaymentModeVm;
import com.lightpro.sales.vm.PaymentVm;
import com.lightpro.sales.vm.ProvisionVm;
import com.sales.domains.api.Customer;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.Payment;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.securities.api.PaymentMode;
import com.securities.api.Secured;

@Path("/sales/payment")
public class PaymentRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter,
							@QueryParam("customerId") UUID customerId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customer customer = sales().customers().build(customerId);
						Payments container = sales().payments().of(customer);
						
						List<PaymentVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new PaymentVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<PaymentVm> pagedSet = new PaginationSet<PaymentVm>(itemsVm, page, count);
						
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
						
						Payment item = sales().payments().get(id);

						return Response.ok(new PaymentVm(item)).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/mode")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPaymentModes() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<PaymentModeVm> items = sales().paymentModes().all()
													   .stream()
													   .map(m -> new PaymentModeVm(m))
													   .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/provision")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addProvision(@PathParam("id") final UUID id, final ProvisionEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						InvoiceReceipt payment = sales().payments().getInvoiceReceipt(id);
						Provision provision = payment.makeProvision(cmd.amount());
						
						log.info(String.format("Création d'une provisioin sur le paiement N° %s", payment.reference()));
						return Response.ok(new ProvisionVm(provision)).build();
					}
				});		
	}	
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Payment item = sales().payments().get(id);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						item.update(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference());
						
						log.info(String.format("Mise à jour d'un paiement en mode brouillon"));
						return Response.ok(new PaymentVm(item)).build();
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

						Payment item = sales().payments().get(id);
						sales().payments().delete(item);
						
						log.info(String.format("Suppression d'un paiement en mode brouillon"));
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
						
						Payment payment = sales().payments().get(id);
						sales().interfacage().comptaInterface().send(payment, false);
						
						log.info(String.format("Envoi du paiement N° %s à la comptabilité", payment.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
}
