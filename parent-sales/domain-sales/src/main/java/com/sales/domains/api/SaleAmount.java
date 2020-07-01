package com.sales.domains.api;

import java.io.IOException;

public interface SaleAmount {	
	double reduceAmount() throws IOException;
	double totalAmountHt();	
	double totalTaxAmount() throws IOException;	
	double totalAmountTtc() throws IOException;	
	double netCommercial() throws IOException;	
}
