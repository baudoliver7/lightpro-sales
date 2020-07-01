package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;

import com.sales.domains.api.Remise;
import com.sales.domains.api.SaleAmount;
import com.securities.api.Tax;

public final class ProductSaleAmount implements SaleAmount {

	private transient final double quantity;
	private transient final double unitPrice;
	private transient final Remise remise;	
	private transient final List<Tax> taxes;
	
	public ProductSaleAmount(final double quantity, final double unitPrice, final Remise remise, final List<Tax> taxes){
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.remise = remise;
		this.taxes = taxes;
	}
	
	@Override
	public double reduceAmount() throws IOException {
		return remise.evaluate(totalAmountHt());
	}
	
	@Override
	public double totalAmountHt(){
		return unitPrice * quantity;
	}
	
	@Override
	public double totalTaxAmount() throws IOException{
		double totalTaxAmount = 0;
		
		for (Tax tax : taxes) {
			totalTaxAmount += tax.evaluateAmount(netCommercial());
		}
		
		return totalTaxAmount;		
	}
	
	@Override
	public double totalAmountTtc() throws IOException{
		return netCommercial() + totalTaxAmount();
	}
	
	@Override
	public double netCommercial() throws IOException {
		return totalAmountHt() - reduceAmount();
	}
}
