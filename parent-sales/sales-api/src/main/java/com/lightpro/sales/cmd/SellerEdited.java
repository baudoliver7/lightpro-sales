package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SellerEdited {
	
	private final UUID id;
	private final UUID teamId;
	
	public SellerEdited(){
		throw new UnsupportedOperationException("#SellerEdited()");
	}
	
	@JsonCreator
	public SellerEdited(  @JsonProperty("id") final UUID id,
				    	  @JsonProperty("teamId") final UUID teamId){
		
		this.id = id;
		this.teamId = teamId;
	}
	
	public UUID id(){
		return id;
	}
	
	public UUID teamId(){
		return teamId;
	}
}
