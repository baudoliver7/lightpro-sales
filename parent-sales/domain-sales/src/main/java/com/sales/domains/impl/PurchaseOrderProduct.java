package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductTaxes;
import com.sales.domains.api.Product;

public class PurchaseOrderProduct implements OrderProduct {

	private transient final OrderProduct origin;

	public PurchaseOrderProduct(final OrderProduct origin){
		this.origin = origin;
	}
	
	@Override
	public Horodate horodate() {
		return origin.horodate();
	}

	@Override
	public UUID id() {
		return origin.id();
	}

	@Override
	public boolean isEqual(OrderProduct item) {
		return origin.isEqual(item);
	}

	@Override
	public boolean isNotEqual(OrderProduct item) {
		return origin.isNotEqual(item);
	}

	@Override
	public boolean isPresent() {
		return origin.isPresent();
	}

	@Override
	public int quantity() throws IOException {
		return origin.quantity();
	}

	@Override
	public double unitPrice() throws IOException {
		return origin.unitPrice();
	}

	@Override
	public double unitPriceApplied() throws IOException {
		return origin.unitPriceApplied();
	}

	@Override
	public double reductionAmount() throws IOException {
		return origin.reductionAmount();
	}

	@Override
	public double totalAmountHt() throws IOException {
		return origin.totalAmountHt();
	}

	@Override
	public double totalTaxAmount() throws IOException {
		return origin.totalTaxAmount();
	}

	@Override
	public double totalAmountTtc() throws IOException {
		return origin.totalAmountTtc();
	}

	@Override
	public Order order() throws IOException {
		return origin.order();
	}

	@Override
	public Product product() throws IOException {
		return origin.product();
	}

	@Override
	public OrderProductTaxes taxes() throws IOException {
		return origin.taxes();
	}

	@Override
	public void update(int quantity, double unitPrice, double reductionAmount, String description, Product product) throws IOException {
		origin.update(quantity, unitPrice, reductionAmount, description, product);		
	}

	@Override
	public String description() throws IOException {
		return origin.description();
	}

	@Override
	public void modifyUnitPrice(double amount) throws IOException {
		origin.modifyUnitPrice(amount);		
	}
}
