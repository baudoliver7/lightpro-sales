package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductTaxes;
import com.sales.domains.api.Sales;
import com.securities.api.MesureUnit;
import com.securities.api.Tax;
import com.securities.impl.MesureUnitNone;

public final class ProductNone extends GuidKeyEntityNone<Product> implements Product {

	@Override
	public String name() throws IOException {
		return null;
	}

	@Override
	public String barCode() throws IOException {
		return null;
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public MesureUnit unit() throws IOException {
		return new MesureUnitNone();
	}

	@Override
	public ProductTaxes taxes() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Pricing pricing() throws IOException {
		return new PricingNone();
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public String emballage() throws IOException {
		return null;
	}

	@Override
	public double quantity() throws IOException {
		return 0;
	}

	@Override
	public ProductCategory category() throws IOException {
		return new ProductCategoryNone();
	}

	@Override
	public void update(String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit,
			String emballage, double quantity) throws IOException {
	}

	@Override
	public OrderProduct generate(double quantity, LocalDate orderDate, double unitPrice, List<Tax> taxes)
			throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public boolean isNone() {
		return true;
	}

	@Override
	public String internalReference() throws IOException {
		return null;
	}
}
