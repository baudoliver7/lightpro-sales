package com.sales.domains.impl;

import java.io.IOException;
import java.util.List;

import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.SaleAmount;

public final class OrderSaleAmountComputing implements SaleAmount {

	private List<OrderProduct> products;
	private double amountHt = 0;
	private double reduceAmount = 0;
	private double netCommercial = 0;
	private double taxAmount = 0;
	private double amountTtc = 0;
	
	public OrderSaleAmountComputing(List<OrderProduct> products){	
		this.products = products;
		evaluate();
	}
	
	private void evaluate(){
		amountHt = 0;
		reduceAmount = 0;
		netCommercial = 0;
		taxAmount = 0;
		amountTtc = 0;
		
		try {
			for (OrderProduct op : products) {
				amountHt += op.saleAmount().totalAmountHt();
				reduceAmount += op.saleAmount().reduceAmount();
				netCommercial += op.saleAmount().netCommercial();
				taxAmount += op.saleAmount().totalTaxAmount();
				amountTtc += op.saleAmount().totalAmountTtc();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
