package com.sales.domains.api;

import java.io.IOException;
import java.util.List;

import com.infrastructure.core.GuidKeyQueryable;
import com.securities.api.Tax;

public interface InvoiceProducts extends GuidKeyQueryable<OrderProduct> {
	Order order() throws IOException;
	void deleteAll() throws IOException;
	OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException;
	OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException;
	OrderProduct add(OrderProduct product) throws IOException;
}
