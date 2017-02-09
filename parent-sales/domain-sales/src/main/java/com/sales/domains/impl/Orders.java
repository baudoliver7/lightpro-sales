package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Customer;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sales;
import com.securities.api.Sequence;
import com.securities.api.User;
import com.securities.api.Sequence.SequenceReserved;

public class Orders implements PurchaseOrders {

	private transient final Base base;
	private final transient PurchaseOrderMetadata dm;
	private final transient DomainsStore ds;
	private final transient Sales module;
	
	public Orders(final Base base, final Sales module){
		this.base = base;
		this.dm = PurchaseOrderMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.module = module;
	}
	
	@Override
	public List<PurchaseOrder> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public PurchaseOrder build(UUID id) {
		return new PurchaseOrderImpl(base, id);
	}

	@Override
	public boolean contains(PurchaseOrder item) {
		try {
			return item.isPresent() && item.module().isEqual(module);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<PurchaseOrder> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<PurchaseOrder> find(int page, int pageSize, String filter) throws IOException {
		List<PurchaseOrder> values = new ArrayList<PurchaseOrder>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement = String.format("SELECT %s FROM %s WHERE %s ILIKE ? AND %s=? ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.referenceKey(), dm.moduleIdKey(), hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add(module.id());
		
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
		String statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s ILIKE ? AND %s=?", dm.keyName(), dm.domainName(), dm.referenceKey(), dm.moduleIdKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add(module.id());
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public PurchaseOrder get(UUID id) throws IOException {
		PurchaseOrder item = build(id);
		
		if(!contains(item))
			throw new NotFoundException("L'ordre n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public void delete(PurchaseOrder item) throws IOException {
		if(contains(item))
		{
			item.products().deleteAll();
			ds.delete(item.id());
		}
	}

	@Override
	public PurchaseOrder add(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String notes, Customer customer, User seller) throws IOException {
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		if (expirationDate == null)
            throw new IllegalArgumentException("Invalid expiration date : it can't be empty!");
				
		if (!customer.isPresent()){
			customer = module.customers().defaultCustomer(); // mettre un client par défaut		
		}
		
		if (!seller.isPresent())
            throw new IllegalArgumentException("Invalid seller : it can't be empty!");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));	
		params.put(dm.expirationDateKey(), java.sql.Date.valueOf(expirationDate));	
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.cgvKey(), cgv);
		params.put(dm.notesKey(), notes);
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.sellerIdKey(), seller.id());
		params.put(dm.statusIdKey(), PurchaseOrderStatus.DRAFT.id());	
		params.put(dm.referenceKey(), sequence().generate());
		params.put(dm.moduleIdKey(), module.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);	
		
		return build(id);
	}

	private Sequence sequence() throws IOException{
		return module.company().sequences().reserved(SequenceReserved.PURCHASE_ORDER);
	}
}
