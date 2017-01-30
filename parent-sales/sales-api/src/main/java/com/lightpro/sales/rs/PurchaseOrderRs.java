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
import com.lightpro.sales.cmd.DownPaymentCmd;
import com.lightpro.sales.cmd.OrderProductEdited;
import com.lightpro.sales.cmd.QuotationEdited;
import com.lightpro.sales.vm.InvoiceVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.QuotationVm;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Product;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderInvoices;
import com.sales.domains.api.PurchaseOrders;
import com.securities.api.User;

@Path("/purchase-order")
public class PurchaseOrderRs extends SalesBaseRs {
	
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
						
						PurchaseOrders container = sales().purchases();
						
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
						
						QuotationVm item = new QuotationVm(sales().purchases().get(id));

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
						
						OrderProducts products = sales().purchases().get(id).products();
						
						List<OrderProductVm> items = products.all().stream()
															 .map(m -> new OrderProductVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@GET
	@Path("/{id}/invoice")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllInvoices(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PurchaseOrderInvoices invoices = sales().purchases().get(id).invoices();
						
						List<InvoiceVm> items = invoices.all().stream()
															 .map(m -> new InvoiceVm(m))
															 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/invoice/down-payment/amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addDownPaymentAmount(@PathParam("id") final UUID id, final DownPaymentCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						Invoice invoice = order.invoices().generateDownPayment(cmd.amount(), cmd.withTax());
						
						return Response.ok(new InvoiceVm(invoice)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/invoice/down-payment/percent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addDownPaymentPercent(@PathParam("id") final UUID id, final DownPaymentCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						Invoice invoice = order.invoices().generateDownPayment(cmd.percent(), cmd.withTax());
						
						return Response.ok(new InvoiceVm(invoice)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/invoice/final")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addFinalInvoice(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws Exception {
						
						PurchaseOrder order = sales().purchases().get(id);
						Invoice invoice = order.invoices().generateFinalInvoice();
						
						return Response.ok(new InvoiceVm(invoice)).build();
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
						
						PurchaseOrder item = sales().purchases().get(id);
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
						
						PurchaseOrder item = sales().purchases().get(id);
						sales().purchases().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@DELETE
	@Path("/{id}/invoice/{invoiceid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id, @PathParam("invoiceid") final UUID invoiceId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PurchaseOrder order = sales().purchases().get(id);
						Invoice item = order.invoices().get(invoiceId);								
						order.invoices().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
