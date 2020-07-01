package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Nonable;

public interface RRRSetting  extends Nonable {
	UUID id();
	RRRType reductionType() throws IOException;
	ProductCategoryType exploitationProductCategory() throws IOException;
	Product reductionProduct() throws IOException;
	Sales module() throws IOException;
	
	void update(Product reductionProduct) throws IOException;
}
