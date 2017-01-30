package com.lightpro.sales.rs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.PaginationSet;
import com.lightpro.sales.cmd.IntervalPricingEdited;
import com.lightpro.sales.cmd.PricingEdited;
import com.lightpro.sales.cmd.ProductAmountsCmd;
import com.lightpro.sales.cmd.ProductEdited;
import com.lightpro.sales.vm.PriceTypeVm;
import com.lightpro.sales.vm.PricingModeVm;
import com.lightpro.sales.vm.PricingVm;
import com.lightpro.sales.vm.ProductAmountsVm;
import com.lightpro.sales.vm.ProductVm;
import com.lightpro.sales.vm.ResumeSalesVm;
import com.lightpro.sales.vm.TaxVm;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductAmounts;
import com.sales.domains.api.Products;
import com.securities.api.MesureUnit;
import com.securities.api.Tax;

@Path("/product")
public class ProductRs extends SalesBaseRs {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ProductVm> items = sales().products().all()
													 .stream()
											 		 .map(m -> new ProductVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Path("/{id}/tax")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllTaxes(@PathParam("id") final UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<TaxVm> items = sales().products().get(id).taxes().all()
													 .stream()
											 		 .map(m -> new TaxVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Path("/resume-sales")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResumeSales(@QueryParam("start") String startStr, @QueryParam("end") String endStr) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						if(StringUtils.isBlank(startStr) || StringUtils.isBlank(endStr) )
							throw new IllegalArgumentException("Vous devez renseigner une période !");
						
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						
						LocalDate start = LocalDate.parse(startStr, formatter);
						LocalDate end = LocalDate.parse(endStr, formatter);
						
						Products products = sales().products();
						
						ResumeSalesVm item = new ResumeSalesVm(
													products.invoicedAmount(start, end),
													products.turnover(start, end),
													products.amountInCirculation(start, end));

						return Response.ok(item).build();
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
						
						Products container = sales().products();
						
						List<ProductVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new ProductVm(m))
															 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<ProductVm> pagedSet = new PaginationSet<ProductVm>(itemsVm, page, count);
						
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
						
						ProductVm item = new ProductVm(sales().products().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Path("/{id}/pricing")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPricing(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						PricingVm item = new PricingVm(sales().products().get(id).pricing());

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Path("/pricing-mode/tranche")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllPriceModeTranche() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<PricingModeVm> items = new ArrayList<PricingModeVm>();
								
						for (PricingMode m : PricingMode.values()) {
							if(m != PricingMode.NONE && m != PricingMode.FIX)
								items.add(new PricingModeVm(m));
						}
					
						return Response.ok(items).build();
					}
				});		
	}
	
	@GET
	@Path("/price-type")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllPriceType() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<PriceTypeVm> items = new ArrayList<PriceTypeVm>();
								
						for (PriceType m : PriceType.values()) {
							if(m != PriceType.NONE)
								items.add(new PriceTypeVm(m));
						}
					
						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final ProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						MesureUnit unit = sales().mesureUnits().get(cmd.mesureUnitId());
						sales().products().add(cmd.name(), cmd.barCode(), cmd.description(), unit);
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/tax/{taxid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(@PathParam("id") final UUID id, @PathParam("taxid") final UUID taxId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Product product = sales().products().get(id);
						Tax tax = sales().taxes().get(taxId);
						
						product.taxes().add(tax);
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/pricing")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final PricingEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Pricing pricing = sales().products().get(id).pricing();
						pricing.update(cmd.fixPrice(), cmd.modeId());
						
						for (IntervalPricingEdited ipe : cmd.intervals()) {
							
							IntervalPricing ip = pricing.intervals().build(ipe.id());
							
							if(ipe.deleted())
								pricing.intervals().delete(ip);
							else
							{
								if(ip.isPresent())
									ip.update(ipe.begin(), ipe.end(), ipe.price(), ipe.priceType());
								else
									pricing.intervals().add(ipe.begin(), ipe.end(), ipe.price(), ipe.priceType());
							}								
						}
						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Path("/{id}/calculate-amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(@PathParam("id") final UUID id, final ProductAmountsCmd cmd) throws IOException {
		
		return createNonTransactionalHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Product product = sales().products().get(id);
						ProductAmounts amounts = product.evaluatePrice(cmd.quantity(), 0, cmd.reductionAmount(), cmd.orderDate(), true);
						
						return Response.ok(new ProductAmountsVm(amounts)).build();
					}
				});		
	}
	
	@PUT
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final ProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Product item = sales().products().get(id);
						MesureUnit unit = sales().mesureUnits().get(cmd.mesureUnitId());
						item.update(cmd.name(), cmd.barCode(), cmd.description(), unit);
						
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
						
						Product item = sales().products().get(id);
						sales().products().delete(item);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@DELETE
	@Path("/{id}/tax/{taxid}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id, @PathParam("taxid") final UUID taxId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Product product = sales().products().get(id);
						Tax tax = sales().taxes().get(taxId);
						
						product.taxes().delete(tax);
						
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
