package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxEdited {
	private final UUID id;
	
	public TaxEdited(){
		throw new UnsupportedOperationException("#TaxEdited()");
	}
	
	@JsonCreator
	public TaxEdited(@JsonProperty("id") final UUID id){				
		this.id = id;
	}
	
	public UUID id(){
		return id;
	}
}
