package com.lightpro.sales.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProvisionEdited {

	private final double amount;
	
	public ProvisionEdited(){
		throw new UnsupportedOperationException("#ProvisionEdited()");
	}
	
	@JsonCreator
	public ProvisionEdited(@JsonProperty("amount") final double amount){
		this.amount = amount;
	}
	
	public double amount(){
		return amount;
	}
}
