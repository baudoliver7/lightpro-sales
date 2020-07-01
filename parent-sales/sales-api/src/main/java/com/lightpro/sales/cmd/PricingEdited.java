package com.lightpro.sales.cmd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PricingMode;
import com.securities.api.NumberValueType;

public class PricingEdited {
	
	private final double fixPrice;
	private final int modeId;
	private final List<IntervalPricingEdited> intervals;
	private final double reduceValue;
	private final int reduceValueTypeId;
	
	public PricingEdited(){
		throw new UnsupportedOperationException("#IntervalPricingEdited()");
	}
	
	@JsonCreator
	public PricingEdited( @JsonProperty("fixPrice") final double fixPrice, 
				    	  @JsonProperty("modeId") final int modeId,
				    	  @JsonProperty("intervals") final List<IntervalPricingEdited> intervals,
				    	  @JsonProperty("reduceValue") final double reduceValue, 
				    	  @JsonProperty("reduceValueTypeId") final int reduceValueTypeId){
		
		this.fixPrice = fixPrice;
		this.modeId = modeId;
		this.intervals = intervals;
		this.reduceValue = reduceValue;
		this.reduceValueTypeId = reduceValueTypeId;
	}
	
	public double fixPrice(){
		return fixPrice;
	}
	
	public double reduceValue(){
		return reduceValue;
	}
	
	public PricingMode modeId(){
		return PricingMode.get(modeId);
	}
	
	public NumberValueType reduceValueType(){
		return NumberValueType.get(reduceValueTypeId);
	}
	
	public List<IntervalPricingEdited> intervals(){
		return intervals;
	}
}
