package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductEdited {
	
	private final String name;
	private final String barCode;
	private final String description;
	private final UUID mesureUnitId;
	
	public ProductEdited(){
		throw new UnsupportedOperationException("#ProductEdited()");
	}
	
	@JsonCreator
	public ProductEdited( @JsonProperty("name") final String name, 
				    	  @JsonProperty("barCode") final String barCode,
				    	  @JsonProperty("mesureUnitId") final UUID mesureUnitId,
				    	  @JsonProperty("description") final String description){
		
		this.name = name;
		this.barCode = barCode;
		this.mesureUnitId = mesureUnitId;
		this.description = description;
	}
	
	public String name(){
		return name;
	}
	
	public String barCode(){
		return barCode;
	}
	
	public UUID mesureUnitId(){
		return mesureUnitId;
	}
	
	public String description(){
		return description;
	}
}
