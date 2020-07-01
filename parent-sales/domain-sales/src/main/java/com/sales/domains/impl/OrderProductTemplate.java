package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.infrastructure.core.GuidKeyEntityBase;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Remise;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.securities.api.Tax;

public final class OrderProductTemplate extends GuidKeyEntityBase<OrderProduct> implements OrderProduct {

	private transient final double quantity;
	private transient final LocalDate orderDate;
	private transient final double unitPrice;
	private transient final Product product;
	private transient final List<Tax> taxes;
	
	public OrderProductTemplate(final Product product, final double quantity, final double unitPrice, final LocalDate orderDate, List<Tax> taxes){
		super(null);
		this.product = product;
		this.taxes = taxes;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.orderDate = orderDate;
	}

	@Override
	public String name() throws IOException {
		return product.name();
	}

	@Override
	public ProductCategory category() throws IOException {
		return product.category();
	}

	@Override
	public Product product() throws IOException {
		return product;
	}

	@Override
	public boolean deductible() throws IOException {
		return quantity < 0;
	}

	@Override
	public double quantity() throws IOException {
		return quantity;
	}

	@Override
	public double unitPrice() throws IOException {
		Pricing pricing = product.pricing();
		return pricing.mode() == PricingMode.KNOWN_IN_SALING ? unitPrice : pricing.evaluateUnitPrice(quantity, orderDate);
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		return new ProductSaleAmount(quantity, unitPrice(), remise(), taxes);
	}

	@Override
	public Order order() throws IOException {
		return new PurchaseOrderNone();
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		SaleTaxes taxes = new SaleTaxesTemplate(saleAmount().netCommercial());
		for (Tax tax : this.taxes) {
			taxes.add(tax);
		}
		
		return taxes;
	}

	@Override
	public String description() throws IOException {
		return product.description();
	}

	@Override
	public void update(String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public Product originProduct() throws IOException {
		return new ProductNone();
	}

	@Override
	public Remise remise() throws IOException {
		return product.pricing().remise();
	}
}
