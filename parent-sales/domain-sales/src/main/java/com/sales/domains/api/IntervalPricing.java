package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;
 
public interface IntervalPricing extends Nonable {	
	UUID id();
	boolean contains(double quantity) throws IOException;
	boolean taxNotApplied() throws IOException;
	double begin() throws IOException;
	double end() throws IOException;
	double count() throws IOException;
	double price() throws IOException;
	PriceType priceType() throws IOException;
	
	double evaluatePrice(double quantity, LocalDate orderDate) throws IOException;	
	double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException;
	void update(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException;
}
