package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface Order extends Recordable<UUID, Order> {
	LocalDate orderDate() throws IOException;
	String reference() throws IOException;
	double totalAmountHt() throws IOException;
	double totalTaxAmount() throws IOException;
	double totalAmountTtc() throws IOException;	
	String notes() throws IOException;
	Customer customer() throws IOException;	
	OrderProducts products() throws IOException;
	
	void updateAmounts() throws IOException;
}
