package com.lightpro.sales.vm;

import java.io.IOException;

import com.sales.domains.impl.ProductSaleAmount;

public final class ProductAmountsVm {
	
	public final double totalAmountHt;
	public final double netCommercial;
	public final double totalTaxAmount;
	public final double totalAmountTtc;
	
	public ProductAmountsVm(){
		throw new UnsupportedOperationException("#ProductAmountsVm()");
	}
	
	public ProductAmountsVm(final ProductSaleAmount origin) {
		try {
			this.totalAmountHt = origin.totalAmountHt();
	        this.netCommercial = origin.netCommercial();
	        this.totalTaxAmount = origin.totalTaxAmount();
	        this.totalAmountTtc = origin.totalAmountTtc();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
