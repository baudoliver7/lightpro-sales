package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Customer;
import com.sales.domains.api.CustomerMetadata;
import com.sales.domains.api.Customers;
import com.securities.api.Company;
import com.securities.api.Person;
import com.securities.api.PersonMetadata;
import com.securities.api.Persons;
import com.securities.api.Sex;
import com.securities.impl.PersonImpl;
import com.securities.impl.PersonsImpl;

public class CustomersImpl implements Customers {

	private final transient Base base;
	private final transient CustomerMetadata dm;
	private final transient DomainsStore ds;
	private final transient Persons persons;
	private final transient Company company;
	
	public CustomersImpl(final Base base, final Company company){
		this.base = base;		
		this.dm = CustomerMetadata.create();
		this.ds = base.domainsStore(dm);
		this.company = company;
		this.persons = new PersonsImpl(base, company);
	}
	
	@Override
	public List<Customer> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public Customer build(UUID id) {
		return new CustomerImpl(base, id);
	}

	@Override
	public boolean contains(Customer item) {
		return item.isPresent() && persons.contains(item);
	}

	@Override
	public List<Customer> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Customer> find(int page, int pageSize, String filter) throws IOException {
		List<Customer> values = new ArrayList<Customer>();
		
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT %s FROM %s "
				+ "WHERE %s IN (SELECT %s FROM %s WHERE (concat(%s,' ', %s) ILIKE ?  OR concat(%s, ' ', %s) ILIKE ?) AND %s=?) "
				+ "ORDER BY %s DESC LIMIT ? OFFSET ?", 
				dm.keyName(), dm.domainName(), 
				dm.keyName(), personDm.keyName(), personDm.domainName(), personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey(), personDm.companyIdKey(), 
				HorodateImpl.dm().dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(company.id());
		
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
		
		PersonMetadata personDm = PersonImpl.dm();
		String statement = String.format("SELECT COUNT(%s) FROM %s "
				+ "WHERE %s IN (SELECT %s FROM %s WHERE (concat(%s,' ', %s) ILIKE ?  OR concat(%s, ' ', %s) ILIKE ?) AND %s=?) ", 
				dm.keyName(), dm.domainName(), 
				dm.keyName(), personDm.keyName(), personDm.domainName(), personDm.firstNameKey(), personDm.lastNameKey(), personDm.lastNameKey(), personDm.firstNameKey(), personDm.companyIdKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(company.id());
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Customer add(String firstName, String lastName, Sex sex, String address, LocalDate birthDate, String tel1, String tel2, String email, String photo) throws IOException {
		Person person = persons.add(firstName, lastName, sex, address, birthDate, tel1, tel2, email, photo);		
		return addPerson(person);
	}

	@Override
	public void delete(Customer item) throws IOException {
		if(contains(item)){
			ds.delete(item.id());
			persons.delete(item);
		}		
	}

	@Override
	public Customer get(UUID id) throws IOException {
		Customer item = build(id);
		
		if(!contains(item))
			throw new NotFoundException("Le client n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public Customer addPerson(Person person) throws IOException {
		Customer customer = build(person.id());
		
		if(contains(customer))
			throw new IllegalArgumentException("Le client existe déjà !");
		
		ds.set(person.id(), new HashMap<String, Object>());		
		return build(person.id());
	}

	@Override
	public Customer defaultCustomer() throws IOException {
		Person defaultPerson = persons.defaultPerson();
		Customer defaultCustomer = build(defaultPerson.id());
		
		if(!contains(defaultCustomer))
			return addPerson(defaultPerson);
		else
			return defaultCustomer;
	}
}
