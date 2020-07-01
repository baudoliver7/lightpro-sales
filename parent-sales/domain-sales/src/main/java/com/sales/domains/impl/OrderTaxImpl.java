package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityBase;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.SaleTax;
import com.securities.api.Company;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;
import com.securities.api.TaxType;
import com.securities.impl.TaxFormatter;
import com.securities.impl.TaxValueFormatter;

public final class OrderTaxImpl extends GuidKeyEntityBase<SaleTax> implements SaleTax {

	private final transient Tax origin;
	private final transient List<OrderProduct> products;
	
	public OrderTaxImpl(final Tax origin, final List<OrderProduct> products){
		super(origin.id());
		this.origin = origin;
		this.products = products;
	}
	
	@Override
	public double amount() throws IOException {
		double amount = 0.0;
		
		for (OrderProduct op : products) {
			
			for (SaleTax tax : op.taxes().all()) {
				if(tax.equals(origin))
					amount += tax.amount();
			}			
		}
		
		return amount;
	}

	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public double decimalValue() throws IOException {
		return origin.decimalValue();
	}

	@Override
	public double evaluateAmount(double amountHt) throws IOException {
		return origin.evaluateAmount(amountHt);
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public String shortName() throws IOException {
		return origin.shortName();
	}

	@Override
	public TaxType type() throws IOException {
		return origin.type();
	}

	@Override
	public void update(TaxType arg0, String arg1, String arg2, double arg3, NumberValueType arg4) throws IOException {
		throw new IllegalArgumentException("Opération non supportée !");	
	}

	@Override
	public double value() throws IOException {
		return origin.value();
	}

	@Override
	public String valueToString() {
		return new TaxValueFormatter(this).toString();
	}
	
	@Override
	public String toString() {
		return new TaxFormatter(this).toString();
	}

	@Override
	public NumberValueType valueType() throws IOException {
		return origin.valueType();
	}

	@Override
	public UUID id() {
		return origin.id();
	}

	@Override
	public boolean isNone() {
		return origin.isNone();
	}
}
