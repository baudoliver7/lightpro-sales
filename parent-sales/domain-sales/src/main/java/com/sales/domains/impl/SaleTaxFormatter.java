package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.Formatter;
import com.sales.domains.api.SaleTax;

public final class SaleTaxFormatter implements Formatter {

	private final transient SaleTax tax;
	
	public SaleTaxFormatter(SaleTax tax){
		this.tax = tax;
	}
	
	@Override
	public String toString() {
		
		try {
			return String.format("%s (%s - %s)", tax.shortName(), tax.valueToString(), tax.company().currency().toString(tax.amount()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
