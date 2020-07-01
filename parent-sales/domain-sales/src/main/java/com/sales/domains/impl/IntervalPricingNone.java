package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.PriceType;

public final class IntervalPricingNone extends GuidKeyEntityNone<IntervalPricing> implements IntervalPricing {

	@Override
	public boolean contains(double quantity) throws IOException {
		return false;
	}

	@Override
	public boolean taxNotApplied() throws IOException {
		return false;
	}

	@Override
	public double begin() throws IOException {
		return 0;
	}

	@Override
	public double end() throws IOException {
		return 0;
	}

	@Override
	public double count() throws IOException {
		return 0;
	}

	@Override
	public double price() throws IOException {
		return 0;
	}

	@Override
	public PriceType priceType() throws IOException {
		return PriceType.NONE;
	}

	@Override
	public double evaluatePrice(double quantity, LocalDate orderDate) throws IOException {
		return 0;
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		return 0;
	}

	@Override
	public void update(double begin, double end, double price, PriceType priceType, boolean taxNotApplied)
			throws IOException {
		
	}
}
