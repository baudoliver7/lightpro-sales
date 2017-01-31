package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.infrastructure.datasource.Base.OrderDirection;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.Payments;

public class PaymentsImpl implements Payments {

	private transient final Base base;
	private final transient PaymentMetadata dm;
	private final transient DomainsStore ds;
	
	public PaymentsImpl(final Base base){
		this.base = base;
		this.dm = PaymentMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public List<Payment> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Payment> find(int page, int pageSize, String filter) throws IOException {
		List<Payment> values = new ArrayList<Payment>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement = String.format("SELECT %s FROM %s "
				+ "WHERE %s ILIKE ? "
				+ "ORDER BY %s DESC LIMIT ? OFFSET ?", 
				dm.keyName(), dm.domainName(), 
				dm.referenceKey(), 
				hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		return values;
	}

	@Override
	public int totalCount(String filter) throws IOException {
		String statement = String.format("SELECT COUNT(%s) FROM %s "
				+ "WHERE %s ILIKE ?", 
				dm.keyName(), dm.domainName(), 
				dm.referenceKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public List<Payment> all() throws IOException {
		List<Payment> values = new ArrayList<Payment>();
		
		List<DomainStore> results = ds.getAllOrdered(HorodateImpl.dm().dateCreatedKey(), OrderDirection.ASC);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}			
		
		return values;
	}

	@Override
	public Payment build(UUID id) {
		return new PaymentImpl(base, id);
	}

	@Override
	public boolean contains(Payment item) {
		return ds.exists(item.id());
	}

	@Override
	public Payment get(UUID id) throws IOException {
		Payment item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("Le paiement n'a pas été trouvé !");
		
		return item;
	}

}
