package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IntervalsPricing {	
	String priceSummary() throws IOException;
	double evaluatePrice(int quantity, double reductionAmount, LocalDate orderDate) throws IOException;
	Pricing pricing() throws IOException;
	IntervalPricing get(UUID id) throws IOException;
	List<IntervalPricing> all() throws IOException;
	
	IntervalPricing add(int begin, int end, double price, PriceType priceType) throws IOException;
	void deleteAll() throws IOException;
	void delete(IntervalPricing item) throws IOException;
	IntervalPricing build(UUID id) throws IOException;
}
