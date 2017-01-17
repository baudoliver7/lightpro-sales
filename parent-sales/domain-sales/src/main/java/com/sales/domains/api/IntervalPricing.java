package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;
 
public interface IntervalPricing extends Recordable<UUID> {		
	int begin() throws IOException;
	int end() throws IOException;
	int count() throws IOException;
	double price() throws IOException;
	PriceType priceType() throws IOException;
	
	double evaluatePrice(EvaluatingPriceParams params) throws IOException;	
	void update(int begin, int end, double price, PriceType priceType) throws IOException;
}
