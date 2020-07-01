package com.sales.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Tax;

public interface OrderProduct extends Nonable {
	UUID id();
	String name() throws IOException;
	ProductCategory category() throws IOException;
	Product product() throws IOException;
	boolean deductible() throws IOException;
	double quantity() throws IOException;
	double unitPrice() throws IOException;
	SaleAmount saleAmount() throws IOException;
	Order order() throws IOException;	
	SaleTaxes taxes() throws IOException;
	String description() throws IOException;
	Product originProduct() throws IOException;
	Remise remise() throws IOException;
	
	void update(String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException;
}
