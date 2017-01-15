package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.securities.api.MesureUnit;

public interface Products extends Queryable<Product> {
	Product get(UUID id) throws IOException;
	Product add(String name, String barCode, String description, MesureUnit unit) throws IOException;
	void delete(Product item) throws IOException;
}
