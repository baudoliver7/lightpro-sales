package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Recordable;
import com.securities.api.MesureUnit;

public interface Product extends Recordable<UUID, Product> {
	String name() throws IOException;
	String barCode() throws IOException;
	String description() throws IOException;
	MesureUnit unit() throws IOException;
	ProductTaxes taxes() throws IOException;
	Pricing pricing() throws IOException;	
	Sales module() throws IOException;
	
	void update(String name, String barCode, String description, MesureUnit unit) throws IOException;
	ProductAmounts evaluatePrice(int quantity, double unitPrice, double reductionAmount, LocalDate orderDate, boolean withTax) throws IOException;
}
