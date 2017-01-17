package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.IntervalPricing;

public class IntervalPricingVm {
	
	private final transient IntervalPricing origin;
	
	public IntervalPricingVm(){
		throw new UnsupportedOperationException("#IntervalPricingVm()");
	}
	
	public IntervalPricingVm(final IntervalPricing origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public UUID getId(){
		return origin.id();
	}
	
	@JsonGetter
	public int getBegin() throws IOException {
		return origin.begin();
	}
	
	@JsonGetter
	public int getEnd() throws IOException {
		return origin.end();
	}
	
	@JsonGetter
	public double getPrice() throws IOException {
		return origin.price();
	}
	
	@JsonGetter
	public int getPriceTypeId() throws IOException {
		return origin.priceType().id();
	}
	
	@JsonGetter
	public String getPriceType() throws IOException {
		return origin.priceType().toString();
	}
}
