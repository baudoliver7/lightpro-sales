package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyQueryableDb;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.Base.OrderDirection;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;
import com.securities.api.Currency;

public final class IntervalsPricingDb extends GuidKeyQueryableDb<IntervalPricing, IntervalPricingMetadata> implements IntervalsPricing {

	private final transient Pricing pricing;
	
	private Currency currency() throws IOException {
		return pricing.product().module().company().currency();
	}
	
	public IntervalsPricingDb(final Base base, Pricing pricing){
		super(base, "Intervalle de tarification introuvable !");
		this.pricing = pricing;
	}

	@Override
	public String priceSummary() throws IOException {
		String unit = pricing.product().unit().shortName();
		
		String summary = "";				
		for (IntervalPricing ip : all()) {
			summary += String.format("De %s à %s %s : %s de %s / ", ip.begin(), ip.end(), unit, ip.priceType().toString(), currency().toString(ip.price()));
		}
		
		return summary;
	}

	@Override
	public double evaluatePrice(double quantity, LocalDate orderDate) throws IOException {		
		double price = 0;
		
		for (IntervalPricing ip : all()) {
			price += ip.evaluatePrice(quantity, orderDate);
		}
		
		return price;
	}

	@Override
	public Pricing pricing() throws IOException {
		return pricing;
	}

	@Override
	public List<IntervalPricing> all() throws IOException {
		List<IntervalPricing> values = new ArrayList<IntervalPricing>();
		
		List<DomainStore> results = ds.getAllByKeyOrdered(dm.pricingIdKey(), pricing.id(), HorodateImpl.dm().dateCreatedKey(), OrderDirection.ASC);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		// ranger par ordre croissant des intervalles
		Comparator<IntervalPricing> byAscInterval = (e1, e2) -> {
			try {
				return Double.compare(e1.end(), e2.begin());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		};		
		
		return values.stream()
					 .sorted(byAscInterval)
					 .collect(Collectors.toList());
	}

	@Override
	public IntervalPricing add(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException {
		
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
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}

	@Override
	public void deleteAll() throws IOException {
		for (IntervalPricing ip : all()) {
			ds.delete(ip.id());
		}
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		double amount = 0;
		
		for (IntervalPricing ip : all()) {
			if(ip.contains(quantity)) {
				amount = ip.evaluateUnitPrice(quantity, orderDate);
				break;
			}			
		}
		
		return amount;
	}

	@Override
	public double evaluateTaxAmount(double quantity, double amountHt) throws IOException {
		
		double price = 0;
		
		for (IntervalPricing ip : all()) {
			if(ip.contains(quantity) && !ip.taxNotApplied()) {
				price = pricing.product().taxes().evaluateAmount(amountHt);				
				break;
			}
		}
		
		return price;
	}

	@Override
	protected IntervalPricing newOne(UUID id) {
		return new IntervalPricingDb(base, id, pricing);
	}

	@Override
	public IntervalPricing none() {
		return new IntervalPricingNone();
	}
}
