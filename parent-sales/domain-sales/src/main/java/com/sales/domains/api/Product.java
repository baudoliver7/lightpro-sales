package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.MesureUnit;
import com.securities.api.Tax;

public interface Product extends Nonable {
	UUID id();
	String name() throws IOException;
	String internalReference() throws IOException;
	String barCode() throws IOException;
	String description() throws IOException;
	MesureUnit unit() throws IOException;
	ProductTaxes taxes() throws IOException;
	Pricing pricing() throws IOException;
	Sales module() throws IOException;
	String emballage() throws IOException;
	double quantity() throws IOException;
	ProductCategory category() throws IOException;
	
	void update(String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException;
	OrderProduct generate(double quantity, LocalDate orderDate, double unitPrice, List<Tax> taxes) throws IOException;
}
