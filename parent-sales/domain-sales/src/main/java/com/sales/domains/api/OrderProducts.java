package com.sales.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.securities.api.Tax;

public interface OrderProducts extends Queryable<OrderProduct, UUID> {
	Order order() throws IOException;
	
	OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException;	
	OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException;
	OrderProduct add(UUID id, ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException;
	
	OrderProduct add(OrderProduct product) throws IOException;
	void deleteAll() throws IOException;
}
