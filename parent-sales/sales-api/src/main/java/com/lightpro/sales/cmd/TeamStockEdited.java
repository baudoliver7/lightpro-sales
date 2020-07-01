package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TeamStockEdited {
	
	private final UUID teamId;
	private final UUID warehouseId;
	private final UUID deliveryOperationId;
	
	public TeamStockEdited(){
		throw new UnsupportedOperationException("#TeamStockEdited()");
	}
	
	@JsonCreator
	public TeamStockEdited( 
						  @JsonProperty("teamId") final UUID teamId, 
				    	  @JsonProperty("warehouseId") final UUID warehouseId,
				    	  @JsonProperty("deliveryOperationId") final UUID deliveryOperationId){
		
		this.teamId = teamId;
		this.warehouseId = warehouseId;
		this.deliveryOperationId = deliveryOperationId;
	}
	
	public UUID teamId(){
		return teamId;
	}
	
	public UUID warehouseId(){
		return warehouseId;
	}
	
	public UUID deliveryOperationId(){
		return deliveryOperationId;
	}
}
