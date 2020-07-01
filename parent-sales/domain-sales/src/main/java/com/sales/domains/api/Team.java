package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Nonable;

public interface Team extends Nonable {
	UUID id();
	String name() throws IOException;
	String shortName() throws IOException;
	Sales module() throws IOException;
	Sellers members() throws IOException;
	
	void update(String name, String shortName) throws IOException;
}
