package com.sales.domains.impl;

import java.io.IOException;

import com.sales.domains.api.Remise;
import com.securities.api.NumberValueType;

public final class RemiseNone implements Remise {

	public RemiseNone(){
		
	}
	
	@Override
	public double value() throws IOException {
		return 0;
	}

	@Override
	public NumberValueType valueType() throws IOException {
		return NumberValueType.AMOUNT;
	}

	@Override
	public Remise from(double value, NumberValueType valueType) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public double evaluate(double amount) throws IOException {
		return 0;
	}
}
