package com.lightpro.sales.rs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.PaginationSet;
import com.infrastructure.core.UseCode;
import com.lightpro.sales.cmd.IntervalPricingEdited;
import com.lightpro.sales.cmd.PricingEdited;
import com.lightpro.sales.cmd.ProductAmountsCmd;
import com.lightpro.sales.cmd.ProductEdited;
import com.lightpro.sales.cmd.TaxEdited;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.OrderProductVm;
import com.lightpro.sales.vm.PriceTypeVm;
import com.lightpro.sales.vm.PricingModeVm;
import com.lightpro.sales.vm.PricingVm;
import com.lightpro.sales.vm.ProductVm;
import com.lightpro.sales.vm.ResumeSalesVm;
import com.lightpro.sales.vm.TaxVm;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Products;
import com.securities.api.MesureUnit;
import com.securities.api.NumberValueType;
import com.securities.api.Secured;
import com.securities.api.Tax;

@Path("/product")
public class ProductRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/reduce-amount/value-type")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTaxValueTypes() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(NumberValueType.values())
													 .stream()
											 		 .map(m -> new ListValueVm(m.id(), m.toString()))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ProductVm> items = sales().products().of(UseCode.USER).all()
													 .stream()
											 		 .map(m -> new ProductVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
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
	@Secured
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
													products.returnAmount(start, end));

						return Response.ok(item).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter,
							@QueryParam("categoryId") UUID categoryId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ProductCategory category = sales().productCategories().build(categoryId);
						Products container = sales().products().of(UseCode.USER).of(category);
						
						List<ProductVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new ProductVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<ProductVm> pagedSet = new PaginationSet<ProductVm>(itemsVm, page, count);
						
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
						
						ProductVm item = new ProductVm(sales().products().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Secured
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
	@Secured
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
	@Secured
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
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final ProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ProductCategory category = sales().productCategories().build(cmd.categoryId());
						MesureUnit unit = sales().mesureUnits().get(cmd.mesureUnitId());
						Product item = sales().products().add(cmd.name(), cmd.internalReference(), cmd.barCode(), category, cmd.description(), unit, cmd.emballage(), cmd.quantity());
						
						log.info(String.format("Création du produit %s", item.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
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
						
						log.info(String.format("Ajout de la taxe %s sur le produit %s", tax.name(), product.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/pricing")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final PricingEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Pricing pricing = sales().products().get(id).pricing();
						pricing.update(cmd.fixPrice(), cmd.modeId(), cmd.reduceValue(), cmd.reduceValueType());
						
						for (IntervalPricingEdited ipe : cmd.intervals()) {
							
							IntervalPricing ip = pricing.intervals().build(ipe.id());
							
							if(ipe.deleted())
								pricing.intervals().delete(ip);
							else
							{
								if(!ip.isNone())
									ip.update(ipe.begin(), ipe.end(), ipe.price(), ipe.priceType(), ipe.taxNotApplied());
								else
									pricing.intervals().add(ipe.begin(), ipe.end(), ipe.price(), ipe.priceType(), ipe.taxNotApplied());
							}								
						}
						
						log.info(String.format("Mise à jour de la tarification du produit %s", pricing.product().name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/calculate-amount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response calculateAmount(@PathParam("id") final UUID id, final ProductAmountsCmd cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Product product = sales().products().get(id);
						List<Tax> taxes = new ArrayList<Tax>();
						for (TaxEdited tax : cmd.taxes()) {
							taxes.add(sales().taxes().get(tax.id()));
						}
						
						OrderProduct orderProduct = product.generate(cmd.quantity(), cmd.orderDate(), cmd.unitPrice(), taxes);
						
						return Response.ok(new OrderProductVm(orderProduct)).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final ProductEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ProductCategory category = sales().productCategories().build(cmd.categoryId());
						Product item = sales().products().get(id);
						MesureUnit unit = sales().mesureUnits().get(cmd.mesureUnitId());
						
						item.update(cmd.name(), cmd.internalReference(), cmd.barCode(), category, cmd.description(), unit, cmd.emballage(), cmd.quantity());
						
						log.info(String.format("Mise à jour des données du produit %s", item.name()));
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
						
						Product item = sales().products().get(id);
						String name = item.name();
						sales().products().delete(item);
						
						log.info(String.format("Suppression du produit %s", name));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
	
	@DELETE
	@Secured
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
						
						log.info(String.format("Retrait de la taxe %s sur le produit %s", tax.name(), product.name()));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
