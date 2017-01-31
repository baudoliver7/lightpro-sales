package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;

public class IntervalPricingImpl implements IntervalPricing {
	
	private final transient Base base;
	private final transient UUID id;
	private final transient Pricing pricing;
	private final transient IntervalPricingMetadata dm;
	private final transient DomainStore ds;	
	
	public IntervalPricingImpl(final Base base, final UUID id, final Pricing pricing){
		this.base = base;
		this.id = id;
		this.dm = dm();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
		this.pricing = pricing;
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public int begin() throws IOException {
		return ds.get(dm.beginKey());
	}

	@Override
	public int end() throws IOException {
		return ds.get(dm.endKey());
	}

	@Override
	public double price() throws IOException {
		return ds.get(dm.priceKey());
	}

	@Override
	public void update(int begin, int end, double price, PriceType priceType) throws IOException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.beginKey(), begin);	
		params.put(dm.endKey(), end);
		params.put(dm.priceKey(), price);
		params.put(dm.priceTypeIdKey(), priceType.id());
		params.put(dm.pricingIdKey(), pricing.id());
		
		ds.set(params);	
	}
	
	public static IntervalPricingMetadata dm(){
		return new IntervalPricingMetadata();
	}

	@Override
	public PriceType priceType() throws IOException {
		int id = ds.get(dm.priceTypeIdKey());
		return PriceType.get(id);
	}

	@Override
	public int count() throws IOException {
		return end() - begin() + 1;
	}

	@Override
	public double evaluatePrice(int quantity, double reductionAmount, LocalDate orderDate) throws IOException {
		
		if(quantity == 0)
			return 0;
		
		double price = 0;
		int count = quantity <= end() ? count() : quantity - begin() + 1;
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				if(begin() >= quantity && end() <= quantity) // est compris dans l'intervalle
					price = price();
				break;
			case UNIT_PRICE:	
				if(begin() >= quantity)
				{				
					price = count * price();
				}
				break;
			case PRORATA_PRICE:
				price = (price() / quantity) * count;
				break;
			default:
				break;
		}
		
		return price;
	}

	@Override
	public boolean isEqual(IntervalPricing item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(IntervalPricing item) {
		return !isEqual(item);
	}
}
