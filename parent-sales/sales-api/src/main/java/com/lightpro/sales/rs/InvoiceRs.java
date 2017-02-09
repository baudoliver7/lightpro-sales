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
import com.lightpro.sales.cmd.InvoiceEdited;
import com.lightpro.sales.cmd.PaymentEdited;
import com.lightpro.sales.vm.InvoiceVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.PaymentVm;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Payment;
import com.securities.api.Secured;

@Path("/invoice")
public class InvoiceRs extends SalesBaseRs {
	
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
						
						Invoices container = sales().invoices();
						
						List<InvoiceVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new InvoiceVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
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
						
						OrderProducts products = sales().invoices().get(id).products();
						
						List<OrderProductVm> items = products.all().stream()
															 .map(m -> new OrderProductVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
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
	@Path("/{id}/payment")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addPayment(@PathParam("id") final UUID id, final PaymentEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						invoice.payments().add(cmd.paymentDate(), cmd.object(), cmd.paidAmount(), cmd.mode());
						
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
						item.update(cmd.orderDate(), cmd.paymentCondition(), cmd.description(), cmd.notes());
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}	
	
	@DELETE
	@Secured
	@Path("/{id}/payment/{paymentid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id, @PathParam("paymentid") final UUID paymentId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Invoice invoice = sales().invoices().get(id);
						Payment item = invoice.payments().get(paymentId);
						invoice.payments().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
