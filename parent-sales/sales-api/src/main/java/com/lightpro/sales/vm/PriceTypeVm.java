package com.lightpro.sales.vm;

import com.sales.domains.api.PriceType;

public final class PriceTypeVm {
	
	public final int id;
	public final String name;
	
	public PriceTypeVm(){
		throw new UnsupportedOperationException("#PriceTypeVm()");
	}
	
	public PriceTypeVm(final PriceType origin) {
        this.id = origin.id();
        this.name = origin.toString();
    }
	
	
}
