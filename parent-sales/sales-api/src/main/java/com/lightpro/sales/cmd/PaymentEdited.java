package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentEdited {
	
	private final String object;
	private final LocalDate paymentDate;
	private final double paidAmount;
	private final UUID modeId;
	private final UUID provisionId;
	private final String transactionReference;
	private final int typeId;
	
	public PaymentEdited(){
		throw new UnsupportedOperationException("#PaymentEdited()");
	}
	
	@JsonCreator
	public PaymentEdited(@JsonProperty("object") final String object,
							  @JsonProperty("paymentDate") final Date paymentDate, 
					    	  @JsonProperty("paidAmount") final double paidAmount,
					    	  @JsonProperty("modeId") final UUID modeId,
					    	  @JsonProperty("provisionId") final UUID provisionId,
					    	  @JsonProperty("transactionReference") final String transactionReference,
					    	  @JsonProperty("typeId") final int typeId){
		
		this.provisionId = provisionId;
		this.object = object;
		this.paymentDate = TimeConvert.toLocalDate(paymentDate, ZoneId.systemDefault());;
		this.paidAmount = paidAmount;
		this.modeId = modeId;
		this.transactionReference = transactionReference;
		this.typeId = typeId;
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
	
	public UUID modeId(){
		return modeId;
	}
	
	public String transactionReference(){
		return transactionReference;
	}
	
	public int typeId(){
		return typeId;
	}
	
	public UUID provisionId(){
		return provisionId;
	}
}
