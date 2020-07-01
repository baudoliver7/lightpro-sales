package com.lightpro.sales.vm;

import com.sales.domains.api.PricingMode;

public final class PricingModeVm {
	public final int id;
	public final String name;
	
	public PricingModeVm(){
		throw new UnsupportedOperationException("#PricingModeVm()");
	}
	
	public PricingModeVm(final PricingMode origin) {
		this.id = origin.id();
        this.name = origin.toString();
    }
}
