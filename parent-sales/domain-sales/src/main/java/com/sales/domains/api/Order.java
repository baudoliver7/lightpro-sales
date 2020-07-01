package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Contact;

public interface Order extends Nonable {
	UUID id();
	String title() throws IOException;
	LocalDate orderDate() throws IOException;
	String reference() throws IOException;
	String description() throws IOException;
	SaleAmount saleAmount() throws IOException;
	String notes() throws IOException;
	Contact customer() throws IOException;	
    SaleTaxes taxes() throws IOException;
    Sales module() throws IOException;
	void updateAmounts() throws IOException;	
}
