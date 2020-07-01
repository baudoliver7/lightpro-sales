package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.Tax;

public interface SaleTax extends Tax {
	double amount() throws IOException;	
}
