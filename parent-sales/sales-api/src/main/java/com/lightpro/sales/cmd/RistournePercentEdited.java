package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RistournePercentEdited {
	
	private final LocalDate orderDate;
	private final int percent;
	
	public RistournePercentEdited(){
		throw new UnsupportedOperationException("#RistournePercentEdited()");
	}
	
	@JsonCreator
	public RistournePercentEdited(
						  @JsonProperty("orderDate") final Date orderDate, 						  
						  @JsonProperty("percent") final int percent){
		
		this.orderDate = TimeConvert.toLocalDate(orderDate, ZoneId.systemDefault());
		this.percent = percent;
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public int percent(){
		return percent;
	}
}
