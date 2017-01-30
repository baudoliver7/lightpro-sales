package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Product;

public class InvoiceProducts implements OrderProducts {

	private transient final OrderProducts origin;
	
	public InvoiceProducts(final OrderProducts origin){
		this.origin = origin;	
	}
	
	@Override
	public List<OrderProduct> all() throws IOException {
		return origin.all();
	}

	@Override
	public OrderProduct get(UUID id) throws IOException {
		return origin.get(id);
	}

	@Override
	public OrderProduct build(UUID id) {
		return new InvoiceProduct(origin.build(id));
	}

	@Override
	public OrderProduct add(int quantity, double unitPrice, double reductionAmount, String description, Product product, boolean withTax) throws IOException {
		return origin.add(quantity, unitPrice, reductionAmount, description, product, withTax);
	}

	@Override
	public void delete(OrderProduct item) throws IOException {
		origin.delete(item);		
	}

	@Override
	public void deleteAll() throws IOException {
		origin.deleteAll();
	}

	@Override
	public Order order() throws IOException {
		return origin.order();
	}

	@Override
	public boolean contains(OrderProduct item) {
		return origin.contains(item);	
	}
}
