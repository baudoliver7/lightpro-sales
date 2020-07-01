package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.UseCode;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.Customer;
import com.sales.domains.api.CustomerMetadata;
import com.sales.domains.api.Customers;
import com.sales.domains.api.Sales;
import com.securities.api.Contact;
import com.securities.api.ContactMetadata;

public final class CustomersDb extends GuidKeyAdvancedQueryableDb<Customer, CustomerMetadata> implements Customers {

	private final transient Sales module;
	private final transient UseCode useCode;
	
	public CustomersDb(final Base base, final Sales module, UseCode useCode){
		super(base, "Client introuvable !");
		this.module = module;
		this.useCode = useCode;
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);

		ContactMetadata persDm = ContactMetadata.create();		
		String statement = String.format("%s cust "
				+ "JOIN view_contacts vctc ON vctc.%s=cust.%s "
				+ "WHERE (vctc.name1 ILIKE ? OR vctc.name2 ILIKE ?) AND cust.%s=?", 
				dm.domainName(), 
				persDm.keyName(), dm.keyName(),
				dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(useCode != UseCode.NONE) {
			statement = String.format("%s AND vctc.%s=?", statement, persDm.useCodeIdKey());
			params.add(useCode.id());
		}
		
		String orderClause;	
		HorodateMetadata hm = new HorodateMetadata();
		
		orderClause = String.format("ORDER BY cust.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("cust.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	public Customer add(Contact contact) throws IOException {
		Customer customer = build(contact.id());
		
		if(contains(customer))
			throw new IllegalArgumentException("Le client existe déjà !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.moduleIdKey(), module.id());
		ds.set(contact.id(), params);		
		
		return build(contact.id());
	}

	@Override
	public void delete(Customer item) throws IOException {
		if(contains(item)){
			ds.delete(item.id());
		}		
	}

	@Override
	public Customer defaultCustomer() throws IOException {
		Contact defaultPerson = module.contacts().defaultPerson();
		Customer defaultCustomer = build(defaultPerson.id());
		
		if(contains(defaultCustomer))
			return defaultCustomer;
		else
			return add(defaultPerson);
	}

	@Override
	protected Customer newOne(UUID id) {
		return new CustomerDb(base, id, module);
	}

	@Override
	public Customer none() {
		return new CustomerNone();
	}

	@Override
	public Customers of(UseCode useCode) throws IOException {
		return new CustomersDb(base, module, useCode);
	}

	@Override
	public boolean contains(Contact contact) {
		Customer customer = build(contact.id());
		return contains(customer);
	}
}
