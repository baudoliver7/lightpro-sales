package com.sales.domains.impl;

import java.io.IOException;

import com.common.utilities.formular.Formular;
import com.sales.domains.api.Remise;
import com.securities.api.Currency;
import com.securities.api.NumberValueType;

public final class RemiseImpl implements Remise {

	private final transient double value;
	private final transient NumberValueType valueType;
	private final transient Currency currency;
	
	public RemiseImpl(double value, NumberValueType valueType, Currency currency){
		this.value = value;
		this.valueType = valueType;
		this.currency = currency;
	}
	
	@Override
	public double value() throws IOException {
		return value;
	}

	@Override
	public NumberValueType valueType() throws IOException {
		return valueType;
	}

	@Override
	public String toString() {
		
		String summary = "";
		
		switch (valueType) {
		case PERCENT:
			summary = String.format("Remise (%.2f", value) + " %)";
			break;
		case AMOUNT:
			summary = currency.toString(value);
		default:
			break;
		}
		
		return summary;
	}

	@Override
	public Remise from(double value, NumberValueType valueType) throws IOException {
		return new RemiseImpl(value, valueType, currency);
	}
	
	@Override
	public double evaluate(double amount) throws IOException {
		double result = 0;
		
		switch (valueType()) {
			case PERCENT:
				Formular formular = currency.calculator().withExpression("{base} * {remise}")
												  .withParam("{base}", amount)
												  .withParam("{remise}", value() / 100.0);
				
				result = formular.result();
				break;
			case AMOUNT:
				result = value();
				break;
			default:
				break;
		}
		
		return result;
	}
}
