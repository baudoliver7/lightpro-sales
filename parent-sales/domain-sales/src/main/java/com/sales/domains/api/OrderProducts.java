package com.sales.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.infrastructure.core.Updatable;

public interface OrderProducts extends Queryable<OrderProduct, UUID>, Updatable<OrderProduct> {
	List<OrderProduct> all() throws IOException;
	Order order() throws IOException;
	
	OrderProduct add(int quantity, double unitPrice, double reductionAmount, String description, Product product, boolean withTax) throws IOException;
	void deleteAll() throws IOException;
}
