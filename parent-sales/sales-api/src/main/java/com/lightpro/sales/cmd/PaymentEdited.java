package com.lightpro.sales.cmd;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentMode;

public class PaymentEdited {
	
	private final String object;
	private final LocalDate paymentDate;
	private final double paidAmount;
	private final PaymentMode mode;
	
	public PaymentEdited(){
		throw new UnsupportedOperationException("#PaymentEdited()");
	}
	
	@JsonCreator
	public PaymentEdited(@JsonProperty("object") final String object,
							  @JsonProperty("paymentDate") final LocalDate paymentDate, 
					    	  @JsonProperty("paidAmount") final double paidAmount,
					    	  @JsonProperty("modeId") final int modeId){
		
		this.object = object;
		this.paymentDate = paymentDate;
		this.paidAmount = paidAmount;
		this.mode = PaymentMode.get(modeId);
	}
	
	public String object(){
		return object;
	}
	
	public LocalDate paymentDate(){
		return paymentDate;
	}
	
	public double paidAmount(){
		return paidAmount;
	}
	
	public PaymentMode mode(){
		return mode;
	}
}
