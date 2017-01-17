package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PriceType;

public class IntervalPricingEdited {
	
	private final UUID id;
	private final int begin;
	private final int end;
	private final double price;
	private final int priceTypeId;
	private final boolean deleted;
	
	public IntervalPricingEdited(){
		throw new UnsupportedOperationException("#IntervalPricingEdited()");
	}
	
	@JsonCreator
	public IntervalPricingEdited( @JsonProperty("id") final UUID id,
								  @JsonProperty("begin") final int begin, 
						    	  @JsonProperty("end") final int end,
						    	  @JsonProperty("price") final double price,
						    	  @JsonProperty("priceTypeId") final int priceTypeId,
						    	  @JsonProperty("deleted") final boolean deleted){
		
		this.id = id;
		this.begin = begin;
		this.end = end;
		this.price = price;
		this.priceTypeId = priceTypeId;
		this.deleted = deleted;
	}
	
	public UUID id(){
		return id;
	}
	
	public int begin(){
		return begin;
	}
	
	public int end(){
		return end;
	}
	
	public double price(){
		return price;
	}
	
	public PriceType priceType(){
		return PriceType.get(priceTypeId);
	}
	
	public boolean deleted(){
		return deleted;
	}
}
