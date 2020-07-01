package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sales.domains.api.SaleTax;
import com.sales.domains.api.SaleTaxes;
import com.securities.api.Tax;

public final class SaleTaxesTemplate implements SaleTaxes {

	private final transient List<SaleTax> taxes;
	private final transient double netCommercial;
	
	public SaleTaxesTemplate(double netCommercial){
		this.taxes = new ArrayList<>();
		this.netCommercial = netCommercial;
	}
	
	@Override
	public double amount() throws IOException {
		double amount = 0;
		
		for (SaleTax tax : all()) {
			amount += tax.amount();
		}
		
		return amount;
	}

	@Override
	public List<SaleTax> all() throws IOException {
		return taxes;
	}

	@Override
	public SaleTax get(UUID id) throws IOException {
		throw new UnsupportedOperationException("Operation non supportée !");
	}

	@Override
	public SaleTax add(Tax tax) throws IOException {
		SaleTax stax = new SaleTaxTemplate(tax, netCommercial);
		taxes.add(stax);
		return stax;
	}

	@Override
	public void delete(Tax item) throws IOException {
		taxes.removeIf(m -> m.equals(item));
	}

	@Override
	public void deleteAll() throws IOException {
		taxes.clear();
	}
	
	@Override
	public String toString() {
		try {
			return new SaleTaxListFormatter(all().stream().map(m -> m).collect(Collectors.toList())).toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
