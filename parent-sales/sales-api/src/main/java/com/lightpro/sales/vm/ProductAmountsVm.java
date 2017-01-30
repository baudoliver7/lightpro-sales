package com.lightpro.sales.vm;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.ProductAmounts;

public class ProductAmountsVm {
	private final transient ProductAmounts origin;
	
	public ProductAmountsVm(){
		throw new UnsupportedOperationException("#ProductAmountsVm()");
	}
	
	public ProductAmountsVm(final ProductAmounts origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public double getUnitPrice(){
		return origin.unitPrice();
	}
	
	@JsonGetter
	public double getUnitPriceApplied(){
		return origin.unitPriceApplied();
	}
	
	@JsonGetter
	public double getTotalAmountHt() throws IOException {
		return origin.totalAmountHt();
	}
	
	@JsonGetter
	public double getTotalTaxAmount() throws IOException {
		return origin.totalTaxAmount();
	}
	
	@JsonGetter
	public double getTotalAmountTtc() throws IOException {
		return origin.totalAmountTtc();
	}
}
