package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.securities.api.Tax;

public class TaxVm {
	private final transient Tax origin;
	
	public TaxVm(){
		throw new UnsupportedOperationException("#TaxVm()");
	}
		
	public TaxVm(Tax origin){
		this.origin = origin;
	}
	
	public UUID getId(){
		return this.origin.id();
	}
	
	public String getName() throws IOException {
		return this.origin.name();
	}
	
	public String getShortName() throws IOException {
		return this.origin.shortName();
	}
	
	public int getRate() throws IOException {
		return this.origin.rate();
	}	
}
