package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMetadata;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.Remise;
import com.sales.domains.api.Sales;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;

public final class PricingDb extends GuidKeyEntityDb<Pricing, PricingMetadata> implements Pricing {
	
	private final Sales module;
	
	public PricingDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Tarification introuvable !");
		this.module = module;
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
	
	public static PricingMetadata dm(){
		return new PricingMetadata();
	}

	@Override
	public Product product() {
		return new ProductDb(base, id, module);
	}

	@Override
	public IntervalsPricing intervals() throws IOException{
		IntervalsPricing intervals = new IntervalsPricingDb(this.base, this);
		
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
	public void update(double fixPrice, PricingMode mode, double reduceValue, NumberValueType reduceValueType) throws IOException {
		
		if(mode == PricingMode.FIX)
		{
			this.ds.set(dm.fixPriceKey(), fixPrice);			
			intervals().deleteAll();
		} else {
			this.ds.set(dm.fixPriceKey(), 0);			
		}
		
		this.ds.set(dm.modeIdKey(), mode.id());
		this.ds.set(dm.reduceValueKey(), reduceValue);
		this.ds.set(dm.reduceValueTypeIdKey(), reduceValueType.id());
	}
	
	@Override
	public ProductSaleAmount evaluateAmounts(double quantity, LocalDate orderDate, List<Tax> taxes) throws IOException {
		
		double unitPrice;
		
		switch (mode()) {
			case FIX:      
				unitPrice = fixPrice();				
				break;
			case TRANCHE_MONTH_DAYS:
			case TRANCHE_SIMPLE:
				unitPrice = intervals().evaluateUnitPrice(quantity, orderDate);
				break;
			case KNOWN_IN_SALING:
				unitPrice = 0;
				break;
			default:
				throw new IllegalArgumentException("Mode de tarification non pris en charge !");
		}
		
		ProductSaleAmount amounts = new ProductSaleAmount(quantity, unitPrice, remise(), taxes);
		
		if(quantity > 0 && amounts.totalAmountHt() < 0)
			throw new IllegalArgumentException(String.format("Le montant de réduction (%.2f) doit être inférieur au prix total ht (%.2f) !", amounts.reduceAmount(), amounts.totalAmountHt()));
		
		return amounts;
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		
		double amount = 0;
		
		switch (mode()) {
			case FIX:
				amount = fixPrice();
				break;
			case TRANCHE_MONTH_DAYS:
			case TRANCHE_SIMPLE:
				amount = intervals().evaluatePrice(quantity, orderDate);
			default:
				throw new IllegalArgumentException("Mode de tarification non pris en charge !");
		}
		
		return amount;
	}

	@Override
	public Remise remise() throws IOException {
		
		int valueTypeId = ds.get(dm.reduceValueTypeIdKey());
		NumberValueType valueType = NumberValueType.get(valueTypeId);
		
		return new RemiseImpl(ds.get(dm.reduceValueKey()), valueType, product().module().company().currency());
	}
	
	@Override
	public String toString(){
		String summary;
		
		try {
			switch (mode()) {
			case FIX:
				summary = product().module().company().currency().toString(fixPrice());
				break;
			case TRANCHE_MONTH_DAYS:
			case TRANCHE_SIMPLE:
				summary = intervals().priceSummary();				
			default:
				summary = product().module().company().currency().toString(0);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return summary;
	}
}
