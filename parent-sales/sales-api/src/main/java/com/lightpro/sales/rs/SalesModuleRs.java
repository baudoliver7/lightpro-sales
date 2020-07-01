package com.lightpro.sales.rs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.datasource.Base;
import com.infrastructure.pgsql.PgBase;
import com.lightpro.sales.vm.DetailSalesVm;
import com.lightpro.sales.vm.SalesVm;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.Sales;
import com.sales.domains.impl.SalesDb;
import com.securities.api.Company;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.api.Secured;
import com.securities.impl.CompanyDb;

@Path("/sales/module")
public class SalesModuleRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/current")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCurrent(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						SalesVm item = new SalesVm(sales());

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/install")
	@Produces({MediaType.APPLICATION_JSON})
	public Response installModule() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
											
						Module sales = sales(currentCompany.modulesSubscribed().get(ModuleType.SALES));					
						sales.install();
												
						return Response.status(Response.Status.OK).build();	
					}
				});					
	}
	
	@POST
	@Secured
	@Path("/uninstall")
	@Produces({MediaType.APPLICATION_JSON})
	public Response uninstallModule() throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
											
						sales().uninstall();
												
						return Response.status(Response.Status.OK).build();
					}
				});			
	}
	
	@POST
	@Secured
	@Path("/{companyId}/uninstall")
	@Produces({MediaType.APPLICATION_JSON})
	public Response uninstallModule(@PathParam("companyId") final UUID companyId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
							
						Base base = PgBase.getInstance(currentUser.id(), companyId);
						
						try {
											
							Company company = new CompanyDb(base, companyId);
							Module module = company.modulesInstalled().get(ModuleType.SALES);
							Sales sales = new SalesDb(base, module);
							
							sales.uninstall();
							
							base.commit();
						} catch (IllegalArgumentException e) {
							base.rollback();
							throw e;
						} 
						catch (Exception e) {
							base.rollback();
							throw e;
						} finally {
							base.terminate();
						}						
												
						return Response.status(Response.Status.OK).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/detail-sales")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDetailsSales(@QueryParam("start") String startStr, @QueryParam("end") String endStr) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						if(StringUtils.isBlank(startStr) || StringUtils.isBlank(endStr) )
							throw new IllegalArgumentException("Vous devez renseigner une période !");
						
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						
						LocalDate start = LocalDate.parse(startStr, formatter);
						LocalDate end = LocalDate.parse(endStr, formatter);

						List<DetailSalesVm> items = new ArrayList<DetailSalesVm>();
						
						for (ModulePdv pdv : sales().modulePdvs().all()) {
							double turnover = pdv.turnover(start, end);							
							items.add(new DetailSalesVm(pdv.name(), turnover));							
						}

						return Response.ok(items).build();
					}
				});			
	}
}
