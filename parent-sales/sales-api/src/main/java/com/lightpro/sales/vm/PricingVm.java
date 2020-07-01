package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sales.domains.api.Pricing;

public final class PricingVm {
	
	public final UUID id;
	public final double fixPrice;
	public final int modeId;
	public final String mode;
	public final double reduceValue;
	public final int reduceValueTypeId;
	public final String reduceValueType;
	public final List<IntervalPricingVm> intervals;
	
	public PricingVm(){
		throw new UnsupportedOperationException("#PricingVm()");
	}
	
	public PricingVm(final Pricing origin) {
		try {
			this.id = origin.id();
	        this.fixPrice = origin.fixPrice();
	        this.modeId = origin.mode().id();
	        this.mode = origin.mode().toString();
	        this.reduceValue = origin.remise().value();
	        this.reduceValueTypeId = origin.remise().valueType().id();
	        this.reduceValueType = origin.remise().valueType().toString();
	        this.intervals = origin.intervals().all()
					 .stream()
			 		 .map(m -> new IntervalPricingVm(m))
			 		 .collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
