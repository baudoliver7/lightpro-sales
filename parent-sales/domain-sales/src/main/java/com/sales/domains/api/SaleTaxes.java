package com.sales.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.securities.api.Tax;

public interface SaleTaxes {
	double amount() throws IOException;
	List<SaleTax> all() throws IOException;
	SaleTax get(UUID id) throws IOException;
	
	SaleTax add(Tax tax) throws IOException;
	void delete(Tax item) throws IOException;
	void deleteAll() throws IOException;
}
