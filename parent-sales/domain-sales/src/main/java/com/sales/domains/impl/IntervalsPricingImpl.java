package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.infrastructure.datasource.Base.OrderDirection;
import com.sales.domains.api.Currency;
import com.sales.domains.api.EvaluatingPriceParams;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;

public class IntervalsPricingImpl implements IntervalsPricing {

	private transient final Base base;
	private final transient IntervalPricingMetadata dm;
	private final transient DomainsStore ds;
	private final transient Pricing pricing;
	
	public IntervalsPricingImpl(final Base base, Pricing pricing){
		this.base = base;
		this.pricing = pricing;
		this.dm = IntervalPricingMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}

	@Override
	public String priceSummary() throws IOException {
		String unit = pricing.product().unit().shortName();
		
		String summary = "";				
		for (IntervalPricing ip : all()) {
			summary += String.format("De %s � %s %s : %s de %.0f %s / ", ip.begin(), ip.end(), unit, ip.priceType().toString(), ip.price(), new Currency(base).currencyShortName());
		}
		
		return summary;
	}

	@Override
	public double evaluatePrice(EvaluatingPriceParams params) throws IOException {
		double price = 0;
		
		for (IntervalPricing ip : all()) {
			price += ip.evaluatePrice(params);
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
			values.add(build(domainStore.key())); 
		}		
		
		// ranger par ordre croissant des intervalles
		Comparator<IntervalPricing> byAscInterval = (e1, e2) -> {
			try {
				return Integer.compare(e1.end(), e2.begin());
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
	public IntervalPricing add(int begin, int end, double price, PriceType priceType) throws IOException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.beginKey(), begin);	
		params.put(dm.endKey(), end);
		params.put(dm.priceKey(), price);
		params.put(dm.priceTypeIdKey(), priceType.id());
		params.put(dm.pricingIdKey(), pricing.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}

	@Override
	public void delete(IntervalPricing item) throws IOException {
		ds.delete(item.id());
	}

	@Override
	public void deleteAll() throws IOException {
		for (IntervalPricing ip : all()) {
			ds.delete(ip.id());
		}
	}

	@Override
	public IntervalPricing build(Object id) throws IOException {
		return new IntervalPricingImpl(base, id, pricing);
	}

	@Override
	public IntervalPricing get(UUID id) throws IOException {
		if(!ds.exists(id))
			throw new NotFoundException("L'interval n'a pas �t� trouv�e !");
		
		return build(id);
	}
}