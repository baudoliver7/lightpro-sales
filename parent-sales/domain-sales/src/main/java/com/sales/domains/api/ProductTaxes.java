package com.sales.domains.api;

import java.io.IOException;
import java.util.List;

import com.securities.api.Tax;

public interface ProductTaxes {
	List<Tax> all() throws IOException;
	void add(Tax item) throws IOException;
	void delete(Tax item) throws IOException;
	void deleteAll() throws IOException;
	double evaluateAmount(double amountHt) throws IOException;
}
