package com.sales.domains.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.securities.api.Tax;

public interface OrderProductTaxes {
	double amount() throws IOException;
	List<OrderProductTax> all() throws IOException;
	OrderProductTax get(UUID id) throws IOException;
	OrderProductTax build(UUID id) throws IOException;
	
	OrderProductTax add(Tax tax) throws IOException;
	void delete(OrderProductTax item) throws IOException;
	void deleteAll() throws IOException;
}
