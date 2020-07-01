package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkSoldCmd {
	
	private final LocalDate soldDate;
	
	public MarkSoldCmd(){
		throw new UnsupportedOperationException("#MarkSoldCmd()");
	}
	
	@JsonCreator
	public MarkSoldCmd(@JsonProperty("soldDate") final Date date){
		
		this.soldDate = TimeConvert.toLocalDate(date, ZoneId.systemDefault());
	}
	
	public LocalDate soldDate(){
		return soldDate;
	}
}
