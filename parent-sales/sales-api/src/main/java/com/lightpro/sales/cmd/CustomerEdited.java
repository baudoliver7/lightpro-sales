package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerEdited {
	
	private final UUID id;
	
	public CustomerEdited(){
		throw new UnsupportedOperationException("#CustomerEdited()");
	}
	
	@JsonCreator
	public CustomerEdited(@JsonProperty("id") final UUID id){
		
		this.id = id;
	}
	
	public UUID id(){
		return id;
	}
}
