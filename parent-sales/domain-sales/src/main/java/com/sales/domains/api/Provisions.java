package com.sales.domains.api;

import java.io.IOException;

import com.infrastructure.core.GuidKeyAdvancedQueryable;
import com.securities.api.Contact;

public interface Provisions extends GuidKeyAdvancedQueryable<Provision> {
	
	Provision add(double amount, InvoiceReceipt payment) throws IOException;
	double totalAvailableAmount() throws IOException;
	
	Provisions of(Contact customer);
}
