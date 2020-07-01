package com.lightpro.sales.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.infrastructure.core.UseCode;
import com.lightpro.sales.vm.CustomerVm;
import com.lightpro.sales.vm.ProvisionVm;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Customers;
import com.sales.domains.api.Provisions;
import com.securities.api.Secured;

@Path("/customer")
public class CustomerRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<CustomerVm> items = sales().customers().of(UseCode.USER).all()
													 .stream()
											 		 .map(m -> new CustomerVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/{id}/provision/available")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllAvailableProvisions(@PathParam("id") final UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customer customer = sales().customers().get(id);
						List<ProvisionVm> items = customer.provisions().all()
													 .stream()
											 		 .map(m -> new ProvisionVm(m))
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
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customers container = sales().customers().of(UseCode.USER);
						
						List<CustomerVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new CustomerVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<CustomerVm> pagedSet = new PaginationSet<CustomerVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	
	@GET
	@Secured
	@Path("/{id}/provision/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response searchProvision( @PathParam("id") final UUID id,
						    @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customer customer = sales().customers().of(UseCode.USER).get(id);
						Provisions container = customer.provisions();
						
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
						
						CustomerVm item = new CustomerVm(sales().customers().get(id));

						return Response.ok(item).build();
					}
				});		
	}
}
