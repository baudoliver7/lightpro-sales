package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;

public final class DetailSalesVm {
	
	private final transient double turnover;
	private final transient String pdv;
	
	public DetailSalesVm(){
		throw new UnsupportedOperationException("#DetailSalesVm()");
	}
	
	public DetailSalesVm(final String pdv, final double turnover) {
		
        this.turnover = turnover;
        this.pdv = pdv;
    }
	
	@JsonGetter
	public String pdv(){
		return pdv;
	}
	
	@JsonGetter
	public double turnover() throws IOException {
		return turnover;
	}
}
