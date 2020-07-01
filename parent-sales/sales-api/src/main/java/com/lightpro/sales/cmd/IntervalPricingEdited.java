package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PriceType;

public class IntervalPricingEdited {
	
	private final UUID id;
	private final double begin;
	private final double end;
	private final double price;
	private final int priceTypeId;
	private final boolean deleted;
	private final boolean taxNotApplied;
	
	public IntervalPricingEdited(){
		throw new UnsupportedOperationException("#IntervalPricingEdited()");
	}
	
	@JsonCreator
	public IntervalPricingEdited( @JsonProperty("id") final UUID id,
								  @JsonProperty("begin") final double begin, 
						    	  @JsonProperty("end") final double end,
						    	  @JsonProperty("price") final double price,
						    	  @JsonProperty("priceTypeId") final int priceTypeId,
						    	  @JsonProperty("deleted") final boolean deleted,
						    	  @JsonProperty("taxNotApplied") final boolean taxNotApplied){
		
		this.id = id;
		this.begin = begin;
		this.end = end;
		this.price = price;
		this.priceTypeId = priceTypeId;
		this.deleted = deleted;
		this.taxNotApplied = taxNotApplied;
	}
	
	public UUID id(){
		return id;
	}
	
	public double begin(){
		return begin;
	}
	
	public double end(){
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
	
	public boolean taxNotApplied(){
		return taxNotApplied;
	}
}
