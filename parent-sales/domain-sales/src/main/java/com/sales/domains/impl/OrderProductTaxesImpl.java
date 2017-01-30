package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.infrastructure.datasource.Base.OrderDirection;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductTax;
import com.sales.domains.api.OrderProductTaxMetadata;
import com.sales.domains.api.OrderProductTaxes;
import com.securities.api.Tax;

public class OrderProductTaxesImpl implements OrderProductTaxes {

	private transient final Base base;
	private final transient OrderProductTaxMetadata dm;
	private final transient DomainsStore ds;
	private final transient OrderProduct orderProduct;
	
	public OrderProductTaxesImpl(final Base base, OrderProduct orderProduct){
		this.base = base;
		this.orderProduct = orderProduct;
		this.dm = OrderProductTaxMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public double amount() throws IOException {
		return 0;
	}

	@Override
	public List<OrderProductTax> all() throws IOException {
		List<OrderProductTax> values = new ArrayList<OrderProductTax>();
		
		List<DomainStore> results = ds.getAllByKeyOrdered(dm.orderProductIdKey(), orderProduct.id(), HorodateImpl.dm().dateCreatedKey(), OrderDirection.ASC);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}			
		
		return values;
	}

	@Override
	public OrderProductTax get(UUID id) throws IOException {
		OrderProductTax item = build(id);
		
		if(!item.isPresent() || !item.orderProduct().isEqual(orderProduct))
			throw new NotFoundException("La taxe de la ligne n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public OrderProductTax build(UUID id) throws IOException {
		return new OrderProductTaxImpl(base, id, orderProduct);
	}

	@Override
	public OrderProductTax add(Tax tax) throws IOException {
		
		if(!tax.isPresent())
			throw new IllegalArgumentException("Invalid tax : it can't be empty !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.amountKey(), orderProduct.totalAmountHt() * (tax.rate() / 100));	
		params.put(dm.taxIdKey(), tax.id());
		params.put(dm.orderProductIdKey(), orderProduct.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}

	@Override
	public void delete(OrderProductTax item) throws IOException {
		
		if(!item.orderProduct().id().equals(orderProduct.id()))
			return;
		
		ds.delete(item.id());
	}

	@Override
	public void deleteAll() throws IOException {
		for (OrderProductTax item : all()) {
			delete(item);
		}
	}
}
