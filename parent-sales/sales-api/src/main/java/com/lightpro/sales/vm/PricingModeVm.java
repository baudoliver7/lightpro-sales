package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.PricingMode;

public class PricingModeVm {
	private final transient PricingMode origin;
	
	public PricingModeVm(){
		throw new UnsupportedOperationException("#PricingModeVm()");
	}
	
	public PricingModeVm(final PricingMode origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public int getId(){
		return origin.id();
	}
	
	@JsonGetter
	public String getName() throws IOException {
		return origin.toString();
	}
}
