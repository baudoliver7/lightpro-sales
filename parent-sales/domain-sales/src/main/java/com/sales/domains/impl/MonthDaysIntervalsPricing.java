package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;

public class MonthDaysIntervalsPricing implements IntervalsPricing {

	private final transient IntervalsPricing origin;
	
	public MonthDaysIntervalsPricing(final IntervalsPricing origin){
		this.origin = origin;
	}

	@Override
	public String priceSummary() throws IOException {
		String summary = "";
		
		for (IntervalPricing ip : all()) {
			summary += String.format("Jour %s à %s -> %s de %.0f %s / ", ip.begin(), ip.end(), ip.priceType().toString(), ip.price(), "FCFA");
		}
		
		return summary;
	}
	
	/**
	 * param1 = Jour; param2 = Mois; param3 = Année
	 */
	@Override
	public double evaluatePrice(int quantity, double reductionAmount, LocalDate orderDate) throws IOException {
		
		double price = 0;
		
		for (IntervalPricing ip : all()) {
			price += ip.evaluatePrice(quantity, reductionAmount, orderDate);
		}
		
		return price;
	}

	@Override
	public Pricing pricing() throws IOException {
		return origin.pricing();
	}

	@Override
	public List<IntervalPricing> all() throws IOException {
		return origin.all();
	}

	@Override
	public IntervalPricing add(int begin, int end, double price, PriceType priceType) throws IOException {
		
		MonthDaysIntervalPricing.validate(begin, end, price, priceType);		
		
		return new MonthDaysIntervalPricing(origin.add(begin, end, price, priceType));
	}

	@Override
	public void delete(IntervalPricing item) throws IOException {
		origin.delete(item);		
	}

	@Override
	public void deleteAll() throws IOException {
		origin.deleteAll();
	}

	@Override
	public IntervalPricing build(UUID id) throws IOException {
		return new MonthDaysIntervalPricing(origin.build(id));
	}

	@Override
	public IntervalPricing get(UUID id) throws IOException {
		return origin.get(id);
	}
}
