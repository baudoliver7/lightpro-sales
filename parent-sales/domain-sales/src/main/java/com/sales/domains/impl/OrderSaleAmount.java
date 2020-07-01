package com.sales.domains.impl;

import java.io.IOException;

import com.sales.domains.api.SaleAmount;

public final class OrderSaleAmount implements SaleAmount {

	private double amountHt = 0;
	private double reduceAmount = 0;
	private double netCommercial = 0;
	private double taxAmount = 0;
	private double amountTtc = 0;
	
	public OrderSaleAmount(double amountHt, double reduceAmount, double netCommercial, double taxAmount, double amountTtc){
		this.amountHt = amountHt;
		this.reduceAmount = reduceAmount;
		this.netCommercial = netCommercial;
		this.taxAmount = taxAmount;
		this.amountTtc = amountTtc;
	}

	@Override
	public double reduceAmount() throws IOException {
		return reduceAmount;
	}

	@Override
	public double totalAmountHt() {
		return amountHt;
	}

	@Override
	public double totalTaxAmount() throws IOException {
		return taxAmount;
	}

	@Override
	public double totalAmountTtc() throws IOException {
		return amountTtc;
	}

	@Override
	public double netCommercial() throws IOException {
		return netCommercial;
	}
}
