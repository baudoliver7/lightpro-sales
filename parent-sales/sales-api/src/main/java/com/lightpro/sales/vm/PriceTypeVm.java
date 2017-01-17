package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.PriceType;

public class PriceTypeVm {
	private final transient PriceType origin;
	
	public PriceTypeVm(){
		throw new UnsupportedOperationException("#PriceTypeVm()");
	}
	
	public PriceTypeVm(final PriceType origin) {
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
