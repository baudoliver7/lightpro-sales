package com.lightpro.sales.rs;

import java.io.IOException;
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

import com.infrastructure.core.PaginationSet;
import com.infrastructure.core.UseCode;
import com.lightpro.sales.cmd.ProductCategoryEdited;
import com.lightpro.sales.cmd.ProductEdited;
import com.lightpro.sales.vm.ListValueVm;
import com.lightpro.sales.vm.ProductCategoryVm;
import com.lightpro.sales.vm.ProductVm;
import com.sales.domains.api.ProductCategories;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryType;
import com.securities.api.Secured;

@Path("/product-category")
public class ProductCategoryRs extends SalesBaseRs {
	
	@GET
	@Secured
	@Path("/type")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllStatus() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ListValueVm> items = Arrays.asList(ProductCategoryType.values())
														 .stream()
														 .filter(m -> m.id() > 0)
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
						
						List<ProductCategoryVm> items = sales().productCategories().withUseCode(UseCode.USER).all()
													 .stream()
											 		 .map(m -> new ProductCategoryVm(m))
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
						
						ProductCategories container = sales().productCategories().withUseCode(UseCode.USER);
						
						List<ProductCategoryVm> itemsVm = container.find(page, pageSize, filter).stream()
															 .map(m -> new ProductCategoryVm(m))
															 .collect(Collectors.toList());
													
						long count = container.count(filter);
						PaginationSet<ProductCategoryVm> pagedSet = new PaginationSet<ProductCategoryVm>(itemsVm, page, count);
						
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
						
						ProductCategoryVm item = new ProductCategoryVm(sales().productCategories().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@GET
	@Secured
	@Path("/{id}/product")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getProductsOfCategory(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ProductCategory category = sales().productCategories().get(id);
						
						List<ProductVm> items = category.products().of(UseCode.USER).all()
								 .stream()
						 		 .map(m -> new ProductVm(m))
						 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final ProductCategoryEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ProductCategory item = sales().productCategories().add(cmd.type(), cmd.name(), cmd.description());	
						
						log.info(String.format("Création de la catégorie de produit %s", item.name()));
						return Response.status(Response.Status.OK).build();
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
						
						ProductCategory item = sales().productCategories().get(id);
						item.update(cmd.name(), cmd.description());
						
						log.info(String.format("Mise à jour des données de la catégorie de produit %s", item.name()));
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
						
						ProductCategory item = sales().productCategories().get(id);
						String name = item.name();
						sales().productCategories().delete(item);
						
						log.info(String.format("Suppression de la catégorie de produit %s", name));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
