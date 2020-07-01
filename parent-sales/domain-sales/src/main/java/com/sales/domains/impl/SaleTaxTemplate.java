package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityBase;
import com.sales.domains.api.SaleTax;
import com.securities.api.Company;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;
import com.securities.api.TaxType;

public final class SaleTaxTemplate extends GuidKeyEntityBase<SaleTax> implements SaleTax {
	
	private final transient Tax tax;
	private final transient double netCommercial;
	
	public SaleTaxTemplate(Tax tax, double netCommercial){
		super(tax.id());
		this.tax = tax;
		this.netCommercial = netCommercial;
	}
	
	@Override
	public Company company() throws IOException {
		return tax.company();
	}

	@Override
	public double decimalValue() throws IOException {
		return tax.decimalValue();
	}

	@Override
	public double evaluateAmount(double amount) throws IOException {
		return tax.evaluateAmount(amount);
	}

	@Override
	public String name() throws IOException {
		return tax.name();
	}

	@Override
	public String shortName() throws IOException {
		return tax.shortName();
	}

	@Override
	public TaxType type() throws IOException {
		return tax.type();
	}

	@Override
	public void update(TaxType arg0, String arg1, String arg2, double arg3, NumberValueType arg4) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public double value() throws IOException {
		return tax.value();
	}

	@Override
	public String valueToString() {
		return tax.valueToString();
	}

	@Override
	public NumberValueType valueType() throws IOException {
		return tax.valueType();
	}

	@Override
	public UUID id() {
		return tax.id();
	}

	@Override
	public double amount() throws IOException {
		return evaluateAmount(netCommercial);
	}
}
