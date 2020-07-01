package com.sales.domains.impl;

import java.util.List;

import com.infrastructure.core.Formatter;
import com.sales.domains.api.SaleTax;

public final class SaleTaxListFormatter implements Formatter {
	
	private final transient List<SaleTax> taxes;
	
	public SaleTaxListFormatter(List<SaleTax> taxes){
		this.taxes = taxes;
	}
	
	public String toString(){
		String description = "";
		
		for (SaleTax tax : taxes) {
			if(!description.isEmpty())
				description += ", ";
			
			description += String.format("%s", new SaleTaxFormatter(tax).toString());
		}
		
		return description;
	}
}
