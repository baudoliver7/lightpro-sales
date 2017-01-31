package com.lightpro.sales.rs;

import java.io.IOException;
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
import com.lightpro.sales.cmd.OrderProductEdited;
import com.lightpro.sales.cmd.QuotationEdited;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.QuotationVm;
import com.sales.domains.api.Customer;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.Product;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrders;
import com.securities.api.User;

@Path("/quotation")
public class QuotationRs extends SalesBaseRs {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<QuotationVm> items = sales().quotations().all()
													 .stream()
											 		 .map(m -> new QuotationVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Path("/payment-condition")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllPaymentConditions() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(PaymentConditionStatus.values())
														 .stream()
												 		 .map(m -> new ListValueVm(m.id(), m.toString()))
												 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
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
						
						PurchaseOrders container = sales().quotations();
						
						List<QuotationVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new QuotationVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<QuotationVm> pagedSet = new PaginationSet<QuotationVm>(itemsVm, page, count);
						
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
						
						QuotationVm item = new QuotationVm(sales().quotations().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Path("/{id}/product")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllProducts(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						OrderProducts products = sales().quotations().get(id).products();
						
						List<OrderProductVm> items = products.all().stream()
															 .map(m -> new OrderProductVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final QuotationEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrders containers = sales().quotations();
						Customer customer = sales().customers().build(cmd.customerId()); // peut ne pas être spécifié (prendre le Client inconnu par défaut)
						User seller = sales().membership().get(cmd.sellerId());
						PurchaseOrder item = containers.add(cmd.orderDate(), cmd.expirationDate(), cmd.paymentCondition(), cmd.cgv(), cmd.notes(), customer, seller);
						
						for (OrderProductEdited ope : cmd.products()) {
							OrderProduct op = item.products().build(ope.id());
							
							if(ope.deleted())
								item.products().delete(op);
							else
							{
								Product product = sales().products().get(ope.productId());
								
								if(op.isPresent())
									op.update(ope.quantity(), 0, ope.reductionAmount(), null, product);
								else
									item.products().add(ope.quantity(), 0, ope.reductionAmount(), null, product, true);
							}								
						}
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/mark-send")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markSend(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().quotations().get(id);
						order.markSend();
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/mark-sold")
	@Produces({MediaType.APPLICATION_JSON})
	public Response markSold(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().quotations().get(id);
						order.markSold();
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/cancel")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cancel(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().quotations().get(id);
						order.cancel();
						
						return Response.ok(new QuotationVm(order)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/re-open")
	@Produces({MediaType.APPLICATION_JSON})
	public Response reOpen(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().quotations().get(id);
						order.reOpen();
						
						return Response.ok(new QuotationVm(order)).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final QuotationEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PurchaseOrder item = sales().quotations().get(id);
						Customer customer = sales().customers().get(cmd.customerId());
						User seller = sales().membership().get(cmd.sellerId());
						item.update(cmd.orderDate(), cmd.expirationDate(), cmd.paymentCondition(), cmd.cgv(), cmd.notes(), customer, seller);
						
						for (OrderProductEdited ope : cmd.products()) {
							OrderProduct op = item.products().build(ope.id());
							
							if(ope.deleted())
								item.products().delete(op);
							else
							{
								Product product = sales().products().get(ope.productId());
								
								if(op.isPresent())
									op.update(ope.quantity(), 0, ope.reductionAmount(), null, product);
								else
									item.products().add(ope.quantity(), 0, ope.reductionAmount(), null, product, true);
							}								
						}
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@DELETE
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PurchaseOrder item = sales().quotations().get(id);
						sales().quotations().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
