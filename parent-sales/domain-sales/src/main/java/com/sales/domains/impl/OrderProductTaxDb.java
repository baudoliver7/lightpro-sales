package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.common.utilities.formular.Formular;
import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.SaleTax;
import com.securities.api.Company;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;
import com.securities.api.TaxType;
import com.securities.impl.TaxDb;
import com.securities.impl.TaxFormatter;
import com.securities.impl.TaxValueFormatter;

public final class OrderProductTaxDb extends GuidKeyEntityDb<SaleTax, OrderProductTaxMetadata> implements SaleTax {

	private final transient Tax origin;
	
	public OrderProductTaxDb(final Base base, final UUID id, final OrderProduct orderProduct){
		super(base, id, "Taxe du produit introuvable !");	
		
		try {
			UUID taxId = ds.get(dm.taxIdKey());
			origin = new TaxDb(base, taxId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public UUID id(){
		return origin.id();
	}
	
	@Override
	public double amount() throws IOException {
		return ds.get(dm.amountKey());
	}

	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public double decimalValue() throws IOException {
		return valueType() == NumberValueType.AMOUNT ? value() : value() / 100.0;
	}

	@Override
	public double evaluateAmount(double amountHt) throws IOException {
		double amount = 0;
		
		switch (valueType()) {
			case PERCENT:
				Formular formular = company().currency().calculator()
									.withExpression("tax_from_ht({base}, {ttax})")
									.withParam("{base}", amountHt)
									.withParam("{ttax}", decimalValue());
				
				amount = formular.result();
				break;
			case AMOUNT:
				amount = value();
				break;
			default:
				break;
		}
		
		return amount;
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public String shortName() throws IOException {
		return ds.get(dm.shortNameKey());
	}

	@Override
	public TaxType type() throws IOException {
		int typeId = ds.get(dm.typeIdKey());
		return TaxType.get(typeId);
	}

	@Override
	public void update(TaxType arg0, String arg1, String arg2, double arg3, NumberValueType arg4) throws IOException {
		throw new IllegalArgumentException("Opération non supportée !");	
	}

	@Override
	public double value() throws IOException {
		return ds.get(dm.valueKey());
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
		int typeId = ds.get(dm.valueTypeKey());
		return NumberValueType.get(typeId);
	}
}
