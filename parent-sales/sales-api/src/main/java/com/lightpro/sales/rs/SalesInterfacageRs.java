package com.lightpro.sales.rs;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lightpro.sales.cmd.TeamStockEdited;
import com.lightpro.sales.cmd.TeamStockInterfaceEdited;
import com.lightpro.sales.vm.ModuleVm;
import com.lightpro.sales.vm.TeamStockInterfaceVm;
import com.sales.domains.api.Team;
import com.sales.interfacage.compta.api.StocksInterface;
import com.securities.api.Secured;

@Path("/sales/interfacage")
public class SalesInterfacageRs extends SalesBaseRs {
	
	
	private StocksInterface stocksInterface() throws IOException {
		return sales().interfacage().stocksInterface();
	}
	
	@GET
	@Secured
	@Path("/module-available")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getModulesAvailable() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ModuleVm> items = sales().interfacage().modulesAvailable()
													 .stream()
											 		 .map(m -> new ModuleVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/team-stock-interface")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllTeamsStock() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<TeamStockInterfaceVm> items = stocksInterface().teamStockInterfaces()
													 .stream()
											 		 .map(m -> new TeamStockInterfaceVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/team-stock-interface")
	@Produces({MediaType.APPLICATION_JSON})
	public Response saveTeamsStock(final TeamStockInterfaceEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						for (TeamStockEdited teamStock : cmd.teamsStock()) {
							
							Team team = sales().teams().build(teamStock.teamId());			
							stocksInterface().linkTeamToStock(team, teamStock.warehouseId(), teamStock.deliveryOperationId());
						}
						
						log.info(String.format("Mise à jour de l'interfacage Equipe commerciale - stocks"));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
}
