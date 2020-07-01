package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;

public final class ResumeSalesVm {
	
	private final transient double invoiced;
	private final transient double turnover;
	private final transient double returnAmount;
	
	public ResumeSalesVm(){
		throw new UnsupportedOperationException("#ResumeSalesVm()");
	}
	
	public ResumeSalesVm(final double invoiced, final double turnover, final double returnAmount) {		
        this.invoiced = invoiced;
        this.turnover = turnover;
        this.returnAmount = returnAmount;
    }
	
	@JsonGetter
	public double invoiced(){
		return invoiced;
	}
	
	@JsonGetter
	public double turnover() throws IOException {
		return turnover;
	}
	
	@JsonGetter
	public double returnAmount() throws IOException {
		return returnAmount;
	}
}
