package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.securities.api.Contact;

public interface Sellers extends AdvancedQueryable<Seller, UUID> {
	boolean contains(Contact contact);
	Seller add(Contact contact) throws IOException;
	Sellers of(Team team) throws IOException;
}
