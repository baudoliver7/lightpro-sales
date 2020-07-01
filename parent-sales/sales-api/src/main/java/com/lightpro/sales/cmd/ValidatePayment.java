package com.lightpro.sales.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidatePayment {

	private final boolean forcePayment;
	
	public ValidatePayment(){
		throw new UnsupportedOperationException("#ValidatePayment()");
	}
	
	@JsonCreator
	public ValidatePayment(@JsonProperty("forcePayment") final boolean forcePayment){
		this.forcePayment = forcePayment;
	}
	
	public boolean forcePayment(){
		return forcePayment;
	}
}
