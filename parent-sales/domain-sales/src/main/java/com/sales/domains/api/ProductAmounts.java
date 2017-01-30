package com.sales.domains.api;

public class ProductAmounts {
	
	private transient final double unitPrice;
	private transient final double unitPriceApplied;
	private transient final double totalAmountHt;
	private transient final double totalTaxAmount;
	private transient final double totalAmountTtc;
	
	public ProductAmounts(final double unitPrice, final double unitPriceApplied, final double totalAmountHt, final double totalTaxAmount, final double totalAmountTtc){
		this.unitPrice = unitPrice;
		this.unitPriceApplied = unitPriceApplied;
		this.totalAmountHt = totalAmountHt;
		this.totalTaxAmount = totalTaxAmount;
		this.totalAmountTtc = totalAmountTtc;
	}
	
	public double unitPrice(){
		return unitPrice;
	}
	
	public double unitPriceApplied(){
		return unitPriceApplied;
	}
	
	public double totalAmountHt(){
		return totalAmountHt;
	}
	
	public double totalTaxAmount(){
		return totalTaxAmount;
	}
	
	public double totalAmountTtc(){
		return totalAmountTtc;
	}
}
