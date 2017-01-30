package com.sales.domains.impl;

import java.util.UUID;

import com.infrastructure.datasource.Base;
import com.sales.domains.api.Customers;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Products;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sales;
import com.securities.api.Company;
import com.securities.api.Membership;
import com.securities.api.MesureUnits;
import com.securities.api.Taxes;
import com.securities.impl.CompanyImpl;
import com.securities.impl.MembershipImpl;
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

	@Override
	public PurchaseOrders quotations() {
		return new QuotationsImpl(base);
	}

	@Override
	public PurchaseOrders purchases() {
		return new PurchaseOrdersImpl(base, quotations());
	}

	@Override
	public Invoices invoices() {
		return new InvoicesImpl(base);
	}

	@Override
	public Membership membership() {
		return new MembershipImpl(base);
	}

	@Override
	public Customers customers() {
		return new CustomersImpl(base);
	}

	@Override
	public Payments payments() {
		return new PaymentsImpl(base);
	}
}
