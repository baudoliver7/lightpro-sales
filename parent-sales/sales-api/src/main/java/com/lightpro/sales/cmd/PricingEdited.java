package com.lightpro.sales.cmd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PricingMode;

public class PricingEdited {
	
	private final double fixPrice;
	private final int modeId;
	private final List<IntervalPricingEdited> intervals;
	
	public PricingEdited(){
		throw new UnsupportedOperationException("#IntervalPricingEdited()");
	}
	
	@JsonCreator
	public PricingEdited( @JsonProperty("fixPrice") final double fixPrice, 
				    	  @JsonProperty("modeId") final int modeId,
				    	  @JsonProperty("intervals") final List<IntervalPricingEdited> intervals){
		
		this.fixPrice = fixPrice;
		this.modeId = modeId;
		this.intervals = intervals;
	}
	
	public double fixPrice(){
		return fixPrice;
	}
	
	public PricingMode modeId(){
		return PricingMode.get(modeId);
	}
	
	public List<IntervalPricingEdited> intervals(){
		return intervals;
	}
}
