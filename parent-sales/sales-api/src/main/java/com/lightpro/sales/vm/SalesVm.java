package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.sales.domains.api.Sales;

public final class SalesVm {
	
	public final UUID id;
	public final String name;
	public final String shortName;
	
	public SalesVm(){
		throw new UnsupportedOperationException("#SalesVm()");
	}
	
	public SalesVm(final Sales origin) {
		try {
			this.id = origin.id();
	        this.name = origin.name();
	        this.shortName = origin.shortName();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
