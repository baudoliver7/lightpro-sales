package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.sales.domains.api.IntervalPricing;

public final class IntervalPricingVm {
	
	public final UUID id;
	public final double begin;
	public final double end;
	public final double price;
	public final int priceTypeId;
	public final String priceType;
	public final boolean taxNotApplied;
	
	public IntervalPricingVm(){
		throw new UnsupportedOperationException("#IntervalPricingVm()");
	}
	
	public IntervalPricingVm(final IntervalPricing origin) {
		try {
			this.id = origin.id();
	        this.begin = origin.begin();
	        this.end = origin.end();
	        this.price = origin.price();
	        this.priceTypeId = origin.priceType().id();
	        this.priceType = origin.priceType().toString();
	        this.taxNotApplied = origin.taxNotApplied();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
