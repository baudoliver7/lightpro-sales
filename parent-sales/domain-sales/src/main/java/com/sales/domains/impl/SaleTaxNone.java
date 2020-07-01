package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.SaleTax;
import com.securities.api.Company;
import com.securities.api.NumberValueType;
import com.securities.api.TaxType;
import com.securities.impl.CompanyNone;

public final class SaleTaxNone extends GuidKeyEntityNone<SaleTax> implements SaleTax {

	@Override
	public Company company() throws IOException {
		return new CompanyNone();
	}

	@Override
	public double decimalValue() throws IOException {
		return 0;
	}

	@Override
	public double evaluateAmount(double amount) throws IOException {
		return 0;
	}

	@Override
	public String name() throws IOException {
		return "Aucune taxe";
	}

	@Override
	public String shortName() throws IOException {
		return null;
	}

	@Override
	public TaxType type() throws IOException {
		return TaxType.NONE;
	}

	@Override
	public void update(TaxType arg0, String arg1, String arg2, double arg3, NumberValueType arg4) throws IOException {
		
	}

	@Override
	public double value() throws IOException {
		return 0;
	}

	@Override
	public String valueToString() {
		return null;
	}

	@Override
	public NumberValueType valueType() throws IOException {
		return NumberValueType.AMOUNT;
	}

	@Override
	public double amount() throws IOException {
		return 0;
	}
}
