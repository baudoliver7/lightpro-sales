package com.lightpro.sales.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.sales.vm.SellerVm;
import com.sales.domains.api.Seller;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;
import com.securities.api.Secured;

@Path("/sales/seller")
public class SellerRs extends SalesBaseRs {
	
	private Sellers sellers() throws IOException{
		return sales().sellers();
	}
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<SellerVm> items = sellers().all()
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
						
						Sellers container = sellers();
						
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
						
						SellerVm item = new SellerVm(sellers().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{id}/is-seller")
	@Produces({MediaType.APPLICATION_JSON})
	public Response isSeller(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {

						Seller item = sellers().build(id);
						return Response.ok(sellers().contains(item)).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/change-team/{newteamid}")
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(@PathParam("id") final UUID id, @PathParam("newteamid") final UUID newTeamId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Seller seller = sellers().get(id);
						String oldTeam = seller.team().name();
						Team team = sales().teams().get(newTeamId);
								
						seller.changeTeam(team);
						
						log.info(String.format("Transfert du vendeur %s de l'équipe commerciale %s à l'équipe commerciale %s", seller.name(), oldTeam, team.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
}
