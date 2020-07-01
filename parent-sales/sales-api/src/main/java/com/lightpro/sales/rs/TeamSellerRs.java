package com.lightpro.sales.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.sales.cmd.SellerEdited;
import com.lightpro.sales.vm.SellerVm;
import com.sales.domains.api.Seller;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.Secured;

@Path("/sales/team/{teamid}/member")
public class TeamSellerRs extends SalesBaseRs {

	private transient UUID teamId;
	
	@PathParam("teamid")
	void setTeam(UUID id){
		teamId = id;
	}
	
	private Team team() throws IOException{
		return sales().teams().get(teamId);
	}
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<SellerVm> items = team().members().all()
													 .stream()
											 		 .map(m -> new SellerVm(m))
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
						
						Sellers container = team().members();
						
						List<SellerVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new SellerVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<SellerVm> pagedSet = new PaginationSet<SellerVm>(itemsVm, page, count);
						
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
						
						SellerVm item = new SellerVm(team().members().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final SellerEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Sellers sellers = team().members();
						Contact contact = sales().contacts().build(cmd.id());
						
						Seller seller = sellers.add(contact);	
						
						log.info(String.format("Ajout du vendeur %s à l'équipe commerciale %s", seller.name(), team().name()));
						return Response.ok(new SellerVm(seller)).build();
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
						
						Seller item = team().members().get(id);
						String name = item.name();
						team().members().delete(item);
						
						log.info(String.format("Suppression du vendeur %s de l'équipe commerciale %s", name, team().name()));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
