package com.sales.domains.impl;

import java.io.IOException;
import java.time.YearMonth;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.sales.domains.api.EvaluatingPriceParams;
import com.sales.domains.api.IntervalPricing;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.PriceType;

public class MonthDaysIntervalPricing implements IntervalPricing {

	private final transient IntervalPricing origin;
	private static final transient int FIRST_DAY = 1;
	private static final transient int LAST_DAY = 30;	
	
	public MonthDaysIntervalPricing(final IntervalPricing origin){
		this.origin = origin;
	}
	
	@Override
	public Horodate horodate() {
		return origin.horodate();
	}

	@Override
	public UUID id() {
		return origin.id();
	}

	@Override
	public boolean isPresent() throws IOException {
		return origin.isPresent();
	}

	@Override
	public int begin() throws IOException {
		return origin.begin();
	}

	@Override
	public int end() throws IOException {
		return origin.end();
	}

	@Override
	public double price() throws IOException {
		return origin.price();
	}

	@Override
	public void update(int begin, int end, double price, PriceType priceType) throws IOException {
		
		validate(begin, end, price, priceType);		
		
		origin.update(begin, end, price, priceType);		
	}
	
	public static IntervalPricingMetadata dm(){
		return new IntervalPricingMetadata();
	}

	@Override
	public PriceType priceType() throws IOException {
		return origin.priceType();
	}
	
	public static void validate(int begin, int end, double price, PriceType priceType) {
		
		if (!(begin >= FIRST_DAY && end <= LAST_DAY)) {
            throw new IllegalArgumentException("Intervalle invalide : les valeurs doivent être comprises entre 1 et 30");
        }
	}

	@Override
	public int count() throws IOException {
		return origin.count();
	}

	@Override
	public double evaluatePrice(EvaluatingPriceParams params) throws IOException {		
		
		if(!(begin() >= params.param1() && end() <= params.param1())) // n'est pas compris dans l'intervalle
			return 0;
		
		double price = 0;
		
		int end = end();
		int begin = begin();
		
		if(end == 30) // fin du mois 
			end = numberOfDayInMonths(params.param3(), params.param2());
		
		int count = end - begin + 1;
		
		switch (priceType()) {
			case TRANCHE_PRICE:
				price = price();
				break;
			case UNIT_PRICE:			
				price = price() * count;
				break;
			case PRORATA_PRICE:
				price = (price() / numberOfDayInMonths(params.param3(), params.param2())) * (end - params.param1() + 1);
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
}
