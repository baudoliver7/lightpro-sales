package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.PriceType;
import com.sales.domains.api.Pricing;

public final class MonthDaysIntervalsPricing implements IntervalsPricing {

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
		return origin.pricing();
	}

	@Override
	public List<IntervalPricing> all() throws IOException {
		return origin.all();
	}

	@Override
	public IntervalPricing add(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException {
		
		MonthDaysIntervalPricing.validate(begin, end, price, priceType);		
		
		return new MonthDaysIntervalPricing(origin.add(begin, end, price, priceType, taxNotApplied));
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
	public IntervalPricing build(UUID id) {
		return new MonthDaysIntervalPricing(origin.build(id));
	}

	@Override
	public IntervalPricing get(UUID id) throws IOException {
		return origin.get(id);
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		return origin.evaluateUnitPrice(quantity, orderDate);
	}

	@Override
	public double evaluateTaxAmount(double quantity, double amountHt) throws IOException {
		return origin.evaluateTaxAmount(quantity, amountHt);
	}

	@Override
	public boolean contains(IntervalPricing item) {
		try {
			return all().stream().anyMatch(m -> m.equals(item));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long count() throws IOException {
		return all().size();
	}

	@Override
	public IntervalPricing first() throws IOException {
		return all().get(0);
	}

	@Override
	public boolean isEmpty() {
		try {
			return all().isEmpty();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IntervalPricing last() throws IOException {
		int size = all().size();
		return all().get(size - 1);
	}
}
