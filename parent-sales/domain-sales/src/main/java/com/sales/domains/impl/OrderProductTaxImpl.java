package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductTax;
import com.sales.domains.api.OrderProductTaxMetadata;
import com.securities.api.Tax;
import com.securities.impl.TaxImpl;

public class OrderProductTaxImpl implements OrderProductTax {

	private final transient Base base;
	private final transient UUID id;
	private final transient OrderProductTaxMetadata dm;
	private final transient DomainStore ds;
	private final transient OrderProduct orderProduct;
	
	public OrderProductTaxImpl(final Base base, final UUID id, final OrderProduct orderProduct){
		this.base = base;
		this.id = id;
		this.dm = OrderProductTaxMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
		this.orderProduct = orderProduct;
	}
	
	@Override
	public double amount() throws IOException {
		return ds.get(dm.amountKey());
	}

	@Override
	public Tax tax() throws IOException {
		UUID taxId = ds.get(dm.taxIdKey());
		return new TaxImpl(base, taxId);
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent(){
		try {
			return base.domainsStore(dm).exists(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public OrderProduct orderProduct() throws IOException {
		return orderProduct;
	}
	
	@Override
	public boolean isEqual(OrderProductTax item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(OrderProductTax item) {
		return !isEqual(item);
	}
}
