package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.Products;
import com.sales.domains.api.Sales;

public final class ProductCategoryNone extends GuidKeyEntityNone<ProductCategory> implements ProductCategory {

	@Override
	public String name() throws IOException {
		return "Aucun produit";
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public void update(String name, String description) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée : catégorie de produit inexistant !");
	}

	@Override
	public Products products() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée : catégorie de produit inexistant !");
	}

	@Override
	public ProductCategoryType type() throws IOException {
		return ProductCategoryType.NONE;
	}
}
