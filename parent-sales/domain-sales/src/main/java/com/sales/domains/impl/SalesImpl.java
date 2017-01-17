package com.sales.domains.impl;

import java.util.UUID;

import com.infrastructure.datasource.Base;
import com.sales.domains.api.Products;
import com.sales.domains.api.Sales;
import com.securities.api.Company;
import com.securities.api.MesureUnits;
import com.securities.api.Taxes;
import com.securities.impl.CompanyImpl;
import com.securities.impl.MesureUnitsImpl;
import com.securities.impl.TaxesImpl;

public class SalesImpl implements Sales {

	private final transient Base base;
	
	public SalesImpl(Base base){
		this.base = base;
	}
	
	@Override
	public Products products() {
		return new ProductsImpl(this.base);
	}

	@Override
	public MesureUnits mesureUnits() {
		return new MesureUnitsImpl(base);
	}

	@Override
	public Taxes taxes() {
		return new TaxesImpl(base);
	}

	@Override
	public Company company() {
		UUID companyOwnerId = UUID.fromString("c155b7df-f18b-47bd-ba49-cb525f7efaa2");
		return new CompanyImpl(base, companyOwnerId);
	}
}
