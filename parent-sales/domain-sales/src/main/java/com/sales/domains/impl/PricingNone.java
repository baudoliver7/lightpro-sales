package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.IntervalsPricing;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.Remise;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;

public final class PricingNone extends GuidKeyEntityNone<Pricing> implements Pricing {

	@Override
	public double fixPrice() throws IOException {
		return 0;
	}

	@Override
	public PricingMode mode() throws IOException {
		return PricingMode.NONE;
	}

	@Override
	public IntervalsPricing intervals() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Product product() {
		return new ProductNone();
	}

	@Override
	public Remise remise() throws IOException {
		return new RemiseNone();
	}

	@Override
	public void update(double fixPrice, PricingMode mode, double reduceValue, NumberValueType reduceValueType)
			throws IOException {

	}

	@Override
	public ProductSaleAmount evaluateAmounts(double quantity, LocalDate orderDate, List<Tax> taxes) throws IOException {
		return new ProductSaleAmount(0, 0, new RemiseNone(), new ArrayList<Tax>());
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		return 0;
	}
}
