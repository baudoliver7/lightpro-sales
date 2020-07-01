package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.GuidKeyAdvancedQueryable;
import com.infrastructure.core.UseCode;

public interface ProductCategories extends GuidKeyAdvancedQueryable<ProductCategory> {

	ProductCategory add(ProductCategoryType type, String name, String description) throws IOException;
	ProductCategory add(ProductCategoryType type, String name, String description, UseCode useCode) throws IOException;
	ProductCategory add(UUID id, ProductCategoryType type, String name, String description, UseCode useCode) throws IOException;
	
	ProductCategory getAcompteCategory() throws IOException;
	
	ProductCategory getDefaultRemiseCategory() throws IOException;
	ProductCategory getDefaultRabaisCategory() throws IOException;
	ProductCategory getDefaultRistourneCategory() throws IOException;
	
	ProductCategory getEscompteCategory() throws IOException;
	
	ProductCategories withUseCode(UseCode code) throws IOException;
	ProductCategories withShortName(String shortName) throws IOException;
	
}
