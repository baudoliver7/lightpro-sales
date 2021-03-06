package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RemiseAmountEdited {
	
	private final LocalDate orderDate;
	private final double amount;
	
	public RemiseAmountEdited(){
		throw new UnsupportedOperationException("#RemiseAmountEdited()");
	}
	
	@JsonCreator
	public RemiseAmountEdited(@JsonProperty("orderDate") final Date orderDate,
				    	  @JsonProperty("amount") final double amount){
		
		this.orderDate = TimeConvert.toLocalDate(orderDate, ZoneId.systemDefault());
		this.amount = amount;
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public double amount(){
		return amount;
	}
}
