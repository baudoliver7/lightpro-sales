package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.Pricing;

public class PricingVm {
	
	private final transient Pricing origin;
	
	public PricingVm(){
		throw new UnsupportedOperationException("#PricingVm()");
	}
	
	public PricingVm(final Pricing origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public UUID getId(){
		return origin.id();
	}
	
	@JsonGetter
	public double getFixPrice() throws IOException {
		return origin.fixPrice();
	}
	
	@JsonGetter
	public int getModeId() throws IOException {
		return origin.mode().id();
	}
	
	@JsonGetter
	public String getMode() throws IOException {
		return origin.mode().toString();
	}	
	
	@JsonGetter
	public List<IntervalPricingVm> getIntervals() throws IOException {
		
		return origin.intervals().all()
					 .stream()
			 		 .map(m -> new IntervalPricingVm(m))
			 		 .collect(Collectors.toList());
	}
}
