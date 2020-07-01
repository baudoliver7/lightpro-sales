package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Nonable;

public interface ProductCategory extends Nonable {
	UUID id();
	ProductCategoryType type() throws IOException;
	String name() throws IOException;
	String description() throws IOException;
	Sales module() throws IOException;
	Products products() throws IOException;
	
	void update(String name, String description) throws IOException;
}
