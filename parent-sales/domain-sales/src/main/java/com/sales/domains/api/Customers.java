package com.sales.domains.api;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;
import com.securities.api.Sex;

public interface Customers extends AdvancedQueryable<Customer, UUID>, Updatable<Customer> {
	Customer add(String firstName, String lastName, Sex sex, String address, Date birthDate, String tel1, String tel2, String email, String photo) throws IOException;
	void delete(Customer item) throws IOException;
}
