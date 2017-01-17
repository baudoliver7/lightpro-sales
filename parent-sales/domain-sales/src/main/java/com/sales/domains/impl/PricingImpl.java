package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Currency;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMetadata;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;

public class PricingImpl implements Pricing {

	private final transient Base base;
	private final transient Object id;
	private final transient PricingMetadata dm;
	private final transient DomainStore ds;
	
	public PricingImpl(final Base base, final Object id){
		this.base = base;
		this.id = id;
		this.dm = dm();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return UUIDConvert.fromObject(this.id);
	}

	@Override
	public boolean isPresent() throws IOException {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public double fixPrice() throws IOException {
		return ds.get(dm.fixPriceKey());
	}

	@Override
	public PricingMode mode() throws IOException {
		int id = ds.get(dm.modeIdKey());		
		return PricingMode.get(id);
	}

	@Override
	public String priceSummary() throws IOException {
		
		if(mode() == PricingMode.FIX)
			return String.format("%.0f %s", fixPrice(), new Currency(base).currencyShortName());
		else
			return intervals().priceSummary();
	}
	
	public static PricingMetadata dm(){
		return new PricingMetadata();
	}

	@Override
	public Product product() {
		return new ProductImpl(base, id);
	}

	@Override
	public IntervalsPricing intervals() throws IOException{
		IntervalsPricing intervals = new IntervalsPricingImpl(this.base, this);
		
		switch (mode()) {
			case TRANCHE_MONTH_DAYS:
				intervals = new MonthDaysIntervalsPricing(intervals);
				break;
			default:
				break;
		}
		
		return intervals;
	}

	@Override
	public void update(double fixPrice, PricingMode mode) throws IOException {
		
		if(mode == PricingMode.FIX)
		{
			this.ds.set(dm.fixPriceKey(), fixPrice);			
			intervals().deleteAll();
		} else {
			this.ds.set(dm.fixPriceKey(), 0);			
		}
		
		this.ds.set(dm.modeIdKey(), mode.id());
	}
}
