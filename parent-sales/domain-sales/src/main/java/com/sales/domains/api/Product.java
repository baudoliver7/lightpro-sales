package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;
import com.securities.api.MesureUnit;

public interface Product extends Recordable<UUID> {
	String name() throws IOException;
	String barCode() throws IOException;
	String description() throws IOException;
	MesureUnit unit() throws IOException;
	ProductTaxes taxes();
	Pricing pricing() throws IOException;
	
	void update(String name, String barCode, String description, MesureUnit unit) throws IOException;
}
