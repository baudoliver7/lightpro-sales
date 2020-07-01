package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyQueryable;

public interface IntervalsPricing extends GuidKeyQueryable<IntervalPricing> {	
	String priceSummary() throws IOException;
	double evaluatePrice(double quantity, LocalDate orderDate) throws IOException;
	double evaluateTaxAmount(double quantity, double amountHt) throws IOException;
	double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException;
	Pricing pricing() throws IOException;
	
	IntervalPricing add(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException;
	void deleteAll() throws IOException;
}
