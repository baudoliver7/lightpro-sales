package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;

public class ResumeSalesVm {
	
	private final transient double invoiced;
	private final transient double turnover;
	private final transient double inCirculation;
	
	public ResumeSalesVm(){
		throw new UnsupportedOperationException("#ResumeSalesVm()");
	}
	
	public ResumeSalesVm(final double invoiced, final double turnover, final double inCirculation) {
		
        this.invoiced = invoiced;
        this.turnover = turnover;
        this.inCirculation = inCirculation;
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
	public double inCirculation() throws IOException {
		return inCirculation;
	}
}
