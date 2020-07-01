package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.Remise;
import com.securities.api.Tax;

public final class PurchaseOrderProducts implements OrderProducts {

	private transient final OrderProducts origin;
	private transient final PurchaseOrder purchaseOrder;
	
	public PurchaseOrderProducts(final OrderProducts origin, final PurchaseOrder purchaseOrder){
		this.origin = origin;
		this.purchaseOrder = purchaseOrder;
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
		return origin.build(id);
	}

	@Override
	public OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException {		
		return add(UUID.randomUUID(), category, originProduct, name, description, quantity, unitPrice, remise, taxes, originProduct);
	}

	@Override
	public void delete(OrderProduct item) throws IOException {
		origin.delete(item);	
		order().updateAmounts();
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

	@Override
	public OrderProduct add(OrderProduct product) throws IOException {
		return origin.add(product);
	}

	@Override
	public OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException {
		return add(UUID.randomUUID(), category, product, name, description, quantity, unitPrice, remise, taxes, new ProductNone());
	}

	@Override
	public long count() throws IOException {
		return all().size();
	}

	@Override
	public OrderProduct first() throws IOException {
		return all().get(0);
	}

	@Override
	public boolean isEmpty() {
		try {
			return count() == 0;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OrderProduct last() throws IOException {
		int size = all().size();
		return all().get(size - 1);
	}

	@Override
	public OrderProduct add(UUID id, ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException {
		
		if(purchaseOrder.status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez modifier que les lignes d'un devis en mode Brouillon !");
		
		return origin.add(id, category, product, name, description, quantity, unitPrice, remise, taxes, originProduct);
	}
}
