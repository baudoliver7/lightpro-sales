package com.lightpro.sales.rs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.sales.cmd.PaymentEdited;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.PaymentVm;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMode;
import com.sales.domains.api.Payments;

@Path("/sales/payment")
public class PaymentRs extends SalesBaseRs {
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Payments container = sales().payments();
						
						List<PaymentVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new PaymentVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<PaymentVm> pagedSet = new PaginationSet<PaymentVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	
	@GET
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
	@Path("/mode")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPaymentModes() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(PaymentMode.values())
													   .stream()
													   .filter(m -> m.id() > 0)
													   .map(m -> new ListValueVm(m.id(), m.toString()))
													   .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Payment item = sales().payments().get(id);
						item.update(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), cmd.mode());
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}	
}
