package com.lightpro.sales.vm;

import java.io.IOException;
import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonGetter;

public class ResumeSalesVm {
	
	private final transient double invoiced;
	private final transient double turnover;
	private final transient double inCirculation;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public ResumeSalesVm(){
		throw new UnsupportedOperationException("#ResumeSalesVm()");
	}
	
	public ResumeSalesVm(final double invoiced, final double turnover, final double inCirculation) {
		
        this.invoiced = invoiced;
        this.turnover = turnover;
        this.inCirculation = inCirculation;
    }
	
	@JsonGetter
	public String invoiced(){
		return df.format(invoiced);
	}
	
	@JsonGetter
	public String turnover() throws IOException {
		return df.format(turnover);
	}
	
	@JsonGetter
	public String inCirculation() throws IOException {
		return df.format(inCirculation);
	}
}
