package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.UseCode;
import com.securities.api.Contact;

public interface Customers extends AdvancedQueryable<Customer, UUID> {
	boolean contains(Contact contact);
	Customer add(Contact contact) throws IOException;
	void delete(Customer item) throws IOException;
	Customer defaultCustomer() throws IOException;
	
	Customers of(UseCode useCode) throws IOException;
}
