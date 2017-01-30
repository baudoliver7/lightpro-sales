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
import com.lightpro.sales.cmd.CustomerEdited;
import com.lightpro.sales.vm.CustomerVm;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Customers;

@Path("/customer")
public class CustomerRs extends SalesBaseRs {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<CustomerVm> items = sales().customers().all()
													 .stream()
											 		 .map(m -> new CustomerVm(m))
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
						
						Customers container = sales().customers();
						
						List<CustomerVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new CustomerVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<CustomerVm> pagedSet = new PaginationSet<CustomerVm>(itemsVm, page, count);
						
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
						
						CustomerVm item = new CustomerVm(sales().customers().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final CustomerEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customers containers = sales().customers();
						Customer item = containers.add(cmd.firstName(), cmd.lastName(), cmd.sex(), cmd.address(), cmd.birthDate(), cmd.tel1(), cmd.tel2(), cmd.email(), cmd.photo());
						
						return Response.ok(new CustomerVm(item)).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final CustomerEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Customer item = sales().customers().get(id);
						item.update(cmd.firstName(), cmd.lastName(), cmd.sex(), cmd.address(), cmd.birthDate(), cmd.tel1(), cmd.tel2(), cmd.email(), cmd.photo());
						
						return Response.ok(new CustomerVm(item)).build();
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
						
						Customer item = sales().customers().get(id);
						sales().customers().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
