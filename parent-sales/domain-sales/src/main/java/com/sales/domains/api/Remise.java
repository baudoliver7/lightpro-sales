package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.NumberValueType;

public interface Remise {
	
	double value() throws IOException;
	NumberValueType valueType() throws IOException;		
	double evaluate(double amount) throws IOException;
	Remise from(double value, NumberValueType valueType) throws IOException;
}
