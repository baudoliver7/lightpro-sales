package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;
import com.securities.api.Currency;

public final class IntervalPricingDb extends GuidKeyEntityDb<IntervalPricing, IntervalPricingMetadata> implements IntervalPricing {
	
	private final transient Pricing pricing;
	
	public IntervalPricingDb(final Base base, final UUID id, final Pricing pricing){
		super(base, id, "Intervalle de tarification introuvable !");
		this.pricing = pricing;
	}

	@Override
	public double begin() throws IOException {
		return ds.get(dm.beginKey());
	}

	@Override
	public double end() throws IOException {
		return ds.get(dm.endKey());
	}

	@Override
	public double price() throws IOException {
		return ds.get(dm.priceKey());
	}

	@Override
	public void update(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException {
		
		if(begin <= 0)
			throw new IllegalArgumentException("Le début d'un intervalle doit être supérieure à 0 !");
		
		if(begin > end)
			throw new IllegalArgumentException("Le début d'un intervalle doit être inférieur à sa fin !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.beginKey(), begin);	
		params.put(dm.endKey(), end);
		params.put(dm.priceKey(), price);
		params.put(dm.priceTypeIdKey(), priceType.id());
		params.put(dm.pricingIdKey(), pricing.id());
		params.put(dm.taxNotAppliedKey(), taxNotApplied);
		
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
	public double count() throws IOException {
		return end() - begin() + 1;
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		
		if(quantity <= 0)
			return 0;
		
		double price = 0;		
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				if(contains(quantity)) // est compris dans l'intervalle
					price = price();				
				break;
			case UNIT_PRICE:	
				if(begin() <= quantity)				
					price = price();
				break;
			case PRORATA_PRICE:
				price = currency().calculator()
	 					          .withExpression("{price} / {quantity}")
								  .withParam("{price}", price())
								  .withParam("{quantity}", quantity)
								  .result();
				break;
			default:
				break;
		}
		
		return price;
	}

	@Override
	public boolean contains(double quantity) {
		try {
			return begin() <= quantity && quantity <= end();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	private Currency currency() throws IOException{
		return pricing.product().module().company().currency();
	}
	
	@Override
	public double evaluatePrice(double quantity, LocalDate orderDate) throws IOException {
		if(quantity <= 0)
			return 0;
		
		double price = 0;		
		double count = quantity >= end() ? count() : quantity - begin() + 1;
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				if(contains(quantity)) // est compris dans l'intervalle
					price = price();				
				break;
			case UNIT_PRICE:	
				if(begin() <= quantity)				
					price = currency().calculator()
		 					          .withExpression("{price} / {count}")
									  .withParam("{price}", price())
									  .withParam("{count}", count)
									  .result();
				break;
			case PRORATA_PRICE:
				 price = currency().calculator()
			 					   .withExpression("{unitPrice} / {count}")
								   .withParam("{unitPrice}", price() / quantity)
								   .withParam("{count}", count)
								   .result();
				break;
			default:
				break;
		}
		
		return price;
	}

	@Override
	public boolean taxNotApplied() throws IOException {
		return ds.get(dm.taxNotAppliedKey());
	}
}
