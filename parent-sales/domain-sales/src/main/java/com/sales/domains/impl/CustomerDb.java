package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Customer;
import com.sales.domains.api.CustomerMetadata;
import com.sales.domains.api.Provisions;
import com.sales.domains.api.Sales;
import com.securities.api.Company;
import com.securities.api.Contact;
import com.securities.api.ContactNature;

public final class CustomerDb extends GuidKeyEntityDb<Customer, CustomerMetadata> implements Customer {

	private final transient Contact origin;
	private final Sales module;
	
	public CustomerDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Client introuvable !");
		this.module = module;
		
		try {
			this.origin = module.contacts().get(id);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public String photo() throws IOException {
		return origin.photo();
	}

	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public Provisions provisions() throws IOException {
		return module().provisions().of(this);
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public String fax() throws IOException {
		return origin.fax();
	}

	@Override
	public String locationAddress() throws IOException {
		return origin.locationAddress();
	}

	@Override
	public String mail() throws IOException {
		return origin.mail();
	}

	@Override
	public String mobile() throws IOException {
		return origin.mobile();
	}

	@Override
	public ContactNature nature() throws IOException {
		return origin.nature();
	}

	@Override
	public String phone() throws IOException {
		return origin.phone();
	}

	@Override
	public String poBox() throws IOException {
		return origin.poBox();
	}

	@Override
	public void updateAddresses(String locationAddress, String phone, String mobile, String fax, String mail, String poBox, String webSite) throws IOException {
		origin.updateAddresses(locationAddress, phone, mobile, fax, mail, poBox, webSite);
	}

	@Override
	public String webSite() throws IOException {
		return origin.webSite();
	}
}
