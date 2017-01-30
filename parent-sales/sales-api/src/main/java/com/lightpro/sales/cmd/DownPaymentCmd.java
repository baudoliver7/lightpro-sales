package com.lightpro.sales.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DownPaymentCmd {
	
	private final double amount;
	private final int percent;
	private final boolean withTax;
	
	public DownPaymentCmd(){
		throw new UnsupportedOperationException("#InvoiceCmd()");
	}
	
	@JsonCreator
	public DownPaymentCmd( @JsonProperty("amount") final double amount, 
						  @JsonProperty("percent") final int percent,
				    	  @JsonProperty("withTax") final boolean withTax){
		
		this.amount = amount;
		this.percent = percent;
		this.withTax = withTax;			
	}
	
	public double amount(){
		return amount;
	}
	
	public int percent(){
		return percent;
	}
	
	public boolean withTax(){
		return withTax;
	}
}
