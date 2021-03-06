package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RabaisEdited {
	
	private final LocalDate orderDate;
	
	public RabaisEdited(){
		throw new UnsupportedOperationException("#RabaisEdited()");
	}
	
	@JsonCreator
	public RabaisEdited(@JsonProperty("orderDate") final Date orderDate){
		
		this.orderDate = TimeConvert.toLocalDate(orderDate, ZoneId.systemDefault());
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
}
