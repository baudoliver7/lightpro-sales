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
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.PurchaseOrderMetadata;

public class InvoicesImpl implements Invoices {

	private transient final Base base;
	private final transient InvoiceMetadata dm;
	private final transient DomainsStore ds;
	
	public InvoicesImpl(final Base base){
		this.base = base;
		this.dm = InvoiceMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public List<Invoice> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Invoice> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public Invoice build(UUID id) {
		return new InvoiceImpl(base, id);		
	}

	@Override
	public boolean contains(Invoice item) {
		return ds.exists(item.id());
	}

	@Override
	public List<Invoice> find(int page, int pageSize, String filter) throws IOException {
		List<Invoice> values = new ArrayList<Invoice>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		PurchaseOrderMetadata poDm = PurchaseOrderMetadata.create();
		String statement = String.format("SELECT %s FROM %s "
				+ "WHERE %s IN (SELECT %s FROM %s WHERE %s ILIKE ?) OR %s ILIKE ? "
				+ "ORDER BY %s DESC LIMIT ? OFFSET ?", 
				dm.keyName(), dm.domainName(), 
				dm.purchaseOrderIdKey(), poDm.keyName(), poDm.domainName(), poDm.referenceKey(), dm.referenceKey(), 
				hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
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
		PurchaseOrderMetadata poDm = PurchaseOrderMetadata.create();
		String statement = String.format("SELECT COUNT(%s) FROM %s "
				+ "WHERE %s IN (SELECT %s FROM %s WHERE %s ILIKE ?) OR %s ILIKE ?", 
				dm.keyName(), dm.domainName(), 
				dm.purchaseOrderIdKey(), poDm.keyName(), poDm.domainName(), poDm.referenceKey(), dm.referenceKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Invoice get(UUID id) throws IOException {
		Invoice item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("La facture n'a pas été trouvée !");
		
		return item;
	}
}
