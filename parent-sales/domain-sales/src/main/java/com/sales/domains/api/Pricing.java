package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.sales.domains.impl.ProductSaleAmount;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;

public interface Pricing {
	UUID id();
	double fixPrice() throws IOException;
	PricingMode mode() throws IOException;
	IntervalsPricing intervals() throws IOException;	
	Product product();
	Remise remise() throws IOException;
	
	void update(double fixPrice, PricingMode mode, double reduceValue, NumberValueType reduceValueType) throws IOException;
	ProductSaleAmount evaluateAmounts(double quantity, LocalDate orderDate, List<Tax> taxes) throws IOException;
	double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException;
}
