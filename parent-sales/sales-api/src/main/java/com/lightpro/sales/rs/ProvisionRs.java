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
import com.lightpro.sales.vm.PaymentVm;
import com.lightpro.sales.vm.ProvisionVm;
import com.sales.domains.api.Payment;
import com.sales.domains.api.Provision;
import com.sales.domains.api.Provisions;
import com.securities.api.PaymentMode;
import com.securities.api.Secured;

@Path("/sales/provision")
public class ProvisionRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Provisions container = sales().provisions();
						
						List<ProvisionVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new ProvisionVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<ProvisionVm> pagedSet = new PaginationSet<ProvisionVm>(itemsVm, page, count);
						
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
						
						Provision item = sales().provisions().get(id);

						return Response.ok(new ProvisionVm(item)).build();
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
						
						Provision item = sales().provisions().get(id);
						item.validate();
						
						log.info(String.format("Validation de la provision N° %s", item.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final ProvisionEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Provision item = sales().provisions().get(id);
						item.update(cmd.amount());
						
						log.info(String.format("Mise à jour des données de la provision du paiement N° %s", item.originPayment().reference()));
						return Response.ok(new ProvisionVm(item)).build();
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
						
						Provision item = sales().provisions().get(id);
						sales().provisions().delete(item);
						
						log.info(String.format("Suppression de la provision du paiement N° %s", item.originPayment().reference()));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@POST
	@Secured
	@Path("/{id}/refund")
	@Produces({MediaType.APPLICATION_JSON})
	public Response refund(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Provision provision = sales().provisions().build(id);
						PaymentMode mode = sales().paymentModes().build(cmd.modeId());
						Payment payment = provision.refund(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), mode, cmd.transactionReference(), currentUser);

						
						log.info(String.format("Remboursement sur la provision N° %s", provision.reference()));
						return Response.ok(new PaymentVm(payment)).build();
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
						
						Provision provision = sales().provisions().get(id);
						sales().interfacage().comptaInterface().send(provision, false);
						
						log.info(String.format("Envoi de la provision N° %s à la comptabilité", provision.reference()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
}
