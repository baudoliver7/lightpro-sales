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
import com.lightpro.sales.cmd.TeamEdited;
import com.lightpro.sales.vm.TeamVm;
import com.sales.domains.api.Team;
import com.sales.domains.api.Teams;
import com.securities.api.Secured;

@Path("/sales/team")
public class TeamRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<TeamVm> items = sales().teams().all()
													 .stream()
											 		 .map(m -> new TeamVm(m))
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
						
						Teams container = sales().teams();
						
						List<TeamVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new TeamVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<TeamVm> pagedSet = new PaginationSet<TeamVm>(itemsVm, page, count);
						
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
						
						TeamVm item = new TeamVm(sales().teams().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final TeamEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Team team = sales().teams().add(cmd.name(), cmd.shortName());		
						
						log.info(String.format("Création de l'équipe commerciale %s", team.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final TeamEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Team item = sales().teams().get(id);
						item.update(cmd.name(), cmd.shortName());
						
						log.info(String.format("Mise à jour des données de l'équipe commerciale %s", item.name()));
						return Response.status(Response.Status.OK).build();
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
						
						Team item = sales().teams().get(id);
						String name = item.name();
						sales().teams().delete(item);
						
						log.info(String.format("Suppression de l'équipe commerciale %s", name));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
