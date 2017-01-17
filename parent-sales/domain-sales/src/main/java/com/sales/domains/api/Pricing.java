package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface Pricing extends Recordable<UUID> {
	double fixPrice() throws IOException;
	PricingMode mode() throws IOException;
	String priceSummary() throws IOException;
	IntervalsPricing intervals() throws IOException;
	Product product();
	
	void update(double fixPrice, PricingMode mode) throws IOException;
}
