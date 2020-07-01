package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Provisions;
import com.sales.domains.api.Sales;
import com.securities.api.Company;
import com.securities.api.ContactNature;
import com.securities.impl.CompanyNone;

public final class CustomerNone extends GuidKeyEntityNone<Customer> implements Customer {

	@Override
	public Company company() throws IOException {
		return new CompanyNone();
	}

	@Override
	public String photo() throws IOException {
		return null;
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public Provisions provisions() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public String fax() throws IOException {
		return null;
	}

	@Override
	public String locationAddress() throws IOException {
		return null;
	}

	@Override
	public String mail() throws IOException {
		return null;
	}

	@Override
	public String mobile() throws IOException {
		return null;
	}

	@Override
	public String name() throws IOException {
		return null;
	}

	@Override
	public ContactNature nature() throws IOException {
		return ContactNature.NONE;
	}

	@Override
	public String phone() throws IOException {
		return null;
	}

	@Override
	public String poBox() throws IOException {
		return null;
	}

	@Override
	public void updateAddresses(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5,
			String arg6) throws IOException {
		
	}

	@Override
	public String webSite() throws IOException {
		return null;
	}
}
