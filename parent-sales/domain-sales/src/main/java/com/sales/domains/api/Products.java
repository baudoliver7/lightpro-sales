package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;
import com.securities.api.MesureUnit;

public interface Products extends AdvancedQueryable<Product, UUID>, Updatable<Product> {
	Product add(String name, String barCode, String description, MesureUnit unit) throws IOException;
	Product getDownPaymentProduct() throws IOException;
	
	double turnover(LocalDate start, LocalDate end) throws IOException;
	double invoicedAmount(LocalDate start, LocalDate end) throws IOException;
	double amountInCirculation(LocalDate start, LocalDate end) throws IOException;
}
