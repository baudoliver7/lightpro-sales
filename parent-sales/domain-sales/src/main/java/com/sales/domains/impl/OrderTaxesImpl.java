package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.SaleTax;
import com.sales.domains.api.SaleTaxes;
import com.securities.api.Tax;

public final class OrderTaxesImpl implements SaleTaxes {

	private final transient List<OrderProduct> products;
	
	public OrderTaxesImpl(List<OrderProduct> products){
		this.products = products;
	}
	
	@Override 
	public double amount() throws IOException {
		double amount = 0;
		
		for (OrderProduct op : products) {
			amount += op.saleAmount().totalTaxAmount();
		}
		
		return amount;
	}

	@Override
	public List<SaleTax> all() throws IOException {
		List<SaleTax> taxes = new ArrayList<SaleTax>();
		
		for (OrderProduct op : products) {
			for (SaleTax tax : op.taxes().all()) {
				if(!taxes.stream().anyMatch(m -> m.equals(tax)))
					taxes.add(new OrderTaxImpl(tax, products)); 
			}
		}
		
		return taxes;
	}

	@Override
	public SaleTax get(UUID id) throws IOException {
		Optional<SaleTax> tax = all().stream().filter(m -> m.id().equals(id)).findFirst();
		if(!tax.isPresent())
			throw new IllegalArgumentException("Taxe du produit introuvable !");
		
		return tax.get();
	}

	@Override
	public SaleTax add(Tax tax) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void delete(Tax item) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void deleteAll() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}
	
	@Override
	public String toString() {
		try {
			return new SaleTaxListFormatter(all().stream().map(m -> m).collect(Collectors.toList())).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
