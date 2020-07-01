package com.lightpro.sales.cmd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TeamStockInterfaceEdited {
	
	private final List<TeamStockEdited> teamsStock;
	
	public TeamStockInterfaceEdited(){
		throw new UnsupportedOperationException("#TeamStockInterfaceEdited()");
	}
	
	@JsonCreator
	public TeamStockInterfaceEdited(@JsonProperty("teamsStock") final List<TeamStockEdited> teamsStock){
		
		this.teamsStock = teamsStock;
	}
	
	@JsonGetter
	public List<TeamStockEdited> teamsStock(){
		return teamsStock;
	}
}
