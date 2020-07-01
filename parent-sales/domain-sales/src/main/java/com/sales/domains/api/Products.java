package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.UseCode;
import com.securities.api.MesureUnit;

public interface Products extends AdvancedQueryable<Product, UUID> {
	
	Product add(UUID id, String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException;
	Product add(String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException;
	Product getDownPaymentProduct() throws IOException;
	
	Product getDefaultRemiseProduct() throws IOException;
	Product getDefaultRabaisProduct() throws IOException;
	Product getDefaultRistourneProduct() throws IOException;
	
	Product getEscompteProduct() throws IOException;
	
	double turnover(LocalDate start, LocalDate end) throws IOException;
	double invoicedAmount(LocalDate start, LocalDate end) throws IOException;
	double returnAmount(LocalDate start, LocalDate end) throws IOException;
	
	Products of(ProductCategory category) throws IOException;
	Products of(UseCode useCode) throws IOException;
	Products withInternalReference(String internalReference) throws IOException;
}
