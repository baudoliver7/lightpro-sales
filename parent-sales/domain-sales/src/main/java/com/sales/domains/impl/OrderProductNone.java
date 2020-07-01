package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Remise;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.securities.api.Tax;

public final class OrderProductNone extends GuidKeyEntityNone<OrderProduct> implements OrderProduct {

	@Override
	public String name() throws IOException {
		return "Aucun produit";
	}

	@Override
	public ProductCategory category() throws IOException {
		return new ProductCategoryNone();
	}

	@Override
	public Product product() throws IOException {
		return new ProductNone();
	}

	@Override
	public boolean deductible() throws IOException {
		return false;
	}

	@Override
	public double quantity() throws IOException {
		return 0;
	}

	@Override
	public double unitPrice() throws IOException {
		return 0;
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Order order() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public Product originProduct() throws IOException {
		return new ProductNone();
	}

	@Override
	public Remise remise() throws IOException {
		return new RemiseNone();
	}

	@Override
	public void update(String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes)
			throws IOException {
		
	}
}
