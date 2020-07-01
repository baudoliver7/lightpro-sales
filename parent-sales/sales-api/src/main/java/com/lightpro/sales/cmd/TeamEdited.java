package com.lightpro.sales.cmd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamEdited {
	
	private final String name;
	private final String shortName;
	
	public TeamEdited(){
		throw new UnsupportedOperationException("#TeamEdited()");
	}
	
	@JsonCreator
	public TeamEdited( @JsonProperty("name") final String name, 
				    	  @JsonProperty("shortName") final String shortName){
		
		this.name = name;
		this.shortName = shortName;
	}
	
	public String name(){
		return name;
	}
	
	public String shortName(){
		return shortName;
	}
}
