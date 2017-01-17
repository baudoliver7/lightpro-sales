package com.sales.domains.api;

import java.io.IOException;

import com.infrastructure.datasource.Base;
import com.sales.domains.impl.SalesImpl;

public class Currency {
	private transient final Base base;
	
	public Currency(final Base base){
		this.base = base;
	}
	
	public String currencyShortName() throws IOException{
		return new SalesImpl(base).company().currencyShortName();
	}
}
