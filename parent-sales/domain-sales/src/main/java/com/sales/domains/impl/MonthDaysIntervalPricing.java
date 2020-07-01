package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

import com.infrastructure.core.GuidKeyEntityBase;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.PriceType;

public final class MonthDaysIntervalPricing extends GuidKeyEntityBase<IntervalPricing> implements IntervalPricing {

	private final transient IntervalPricing origin;
	private static final transient double FIRST_DAY = 1;
	private static final transient double LAST_DAY = 30;	
	
	public MonthDaysIntervalPricing(final IntervalPricing origin){
		super(origin.id());
		this.origin = origin;
	}

	@Override
	public double begin() throws IOException {
		return origin.begin();
	}

	@Override
	public double end() throws IOException {
		return origin.end();
	}

	@Override
	public double price() throws IOException {
		return origin.price();
	}

	@Override
	public void update(double begin, double end, double price, PriceType priceType, boolean taxNotApplied) throws IOException {
		
		validate(begin, end, price, priceType);		
		
		origin.update(begin, end, price, priceType, taxNotApplied);		
	}
	
	public static IntervalPricingMetadata dm(){
		return new IntervalPricingMetadata();
	}

	@Override
	public PriceType priceType() throws IOException {
		return origin.priceType();
	}
	
	public static void validate(double begin, double end, double price, PriceType priceType) {
		
		if (!(begin >= FIRST_DAY && end <= LAST_DAY)) {
            throw new IllegalArgumentException("Intervalle invalide : les valeurs doivent être comprises entre 1 et 30");
        }
	}

	@Override
	public double count() throws IOException {
		return origin.count();
	}

	@Override
	public double evaluatePrice(double quantity, LocalDate orderDate) throws IOException {		
		
		if(!(begin() <= orderDate.getDayOfMonth() && orderDate.getDayOfMonth() <= end())) // n'est pas compris dans l'intervalle
			return 0;
		
		double price = 0;
		
		double end = end();
		double begin = begin();
		
		if(end == 30) // fin du mois 
			end = numberOfDayInMonths(orderDate.getMonthValue(), orderDate.getYear());
		
		double count = end - begin + 1;
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				price = price();
				break;
			case UNIT_PRICE:			
				price = price() * (double)count;
				break;
			case PRORATA_PRICE:
				price = (price() / (double)numberOfDayInMonths(orderDate.getMonthValue(), orderDate.getYear())) * (end - orderDate.getDayOfMonth() + 1);
				break;
			default:
				break;
		}
		
		return price;
	}
	
	private static int numberOfDayInMonths(int month, int year){
		YearMonth yearMonthObject = YearMonth.of(year, month);
		return yearMonthObject.lengthOfMonth();
	}

	@Override
	public boolean contains(double quantity) throws IOException {
		try {
			return begin() <= quantity && quantity <= end();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public double evaluateUnitPrice(double quantity, LocalDate orderDate) throws IOException {
		
		if(!(begin() <= orderDate.getDayOfMonth() && orderDate.getDayOfMonth() <= end())) // n'est pas compris dans l'intervalle
			return 0;
		
		double price = 0;
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				price = price();
				break;
			case UNIT_PRICE:			
				price = price();
				break;
			case PRORATA_PRICE:
				price = price() / (double)numberOfDayInMonths(orderDate.getMonthValue(), orderDate.getYear());
				break;
			default:
				break;
		}
		
		return price;
	}
	
	@Override
	public boolean taxNotApplied() throws IOException {
		return origin.taxNotApplied();
	}
}
