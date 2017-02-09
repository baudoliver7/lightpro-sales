package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Horodate;
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
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.api.Taxes;
import com.securities.impl.BasisModule;
import com.securities.impl.CompanyImpl;
import com.securities.impl.MembershipImpl;
import com.securities.impl.MesureUnitsImpl;
import com.securities.impl.TaxesImpl;

public class SalesImpl implements Sales {

	private final transient Base base;
	private final transient Module origin;
	
	public SalesImpl(Base base, final UUID id){
		this.base = base;
		this.origin = new BasisModule(base, id);
	}
	
	@Override
	public Products products() {
		return new ProductsImpl(this.base, this);
	}

	@Override
	public MesureUnits mesureUnits() throws IOException {
		return new MesureUnitsImpl(base, origin.company());
	}

	@Override
	public Taxes taxes() throws IOException {
		return new TaxesImpl(base, origin.company());
	}

	@Override
	public Company company() {
		UUID companyOwnerId = UUID.fromString("c155b7df-f18b-47bd-ba49-cb525f7efaa2");
		return new CompanyImpl(base, companyOwnerId);
	}

	@Override
	public PurchaseOrders quotations() {
		return new QuotationsImpl(base, this);
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
	public Customers customers() throws IOException {
		return new CustomersImpl(base, origin.company());
	}

	@Override
	public Payments payments() {
		return new PaymentsImpl(base);
	}

	@Override
	public String description() throws IOException {
		return origin.description();
	}

	@Override
	public void install() throws IOException {
		origin.install();
	}

	@Override
	public boolean isAvailable() {
		return origin.isAvailable();
	}

	@Override
	public boolean isInstalled() {
		return origin.isInstalled();
	}

	@Override
	public boolean isSubscribed() {
		return origin.isSubscribed();
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public int order() throws IOException {
		return origin.order();
	}

	@Override
	public String shortName() throws IOException {
		return origin.shortName();
	}

	@Override
	public ModuleType type() throws IOException {
		return origin.type();
	}

	@Override
	public void uninstall() throws IOException {
		origin.uninstall();
	}

	@Override
	public Horodate horodate() {
		return origin.horodate();
	}

	@Override
	public UUID id() {
		return origin.id();
	}

	@Override
	public boolean isEqual(Module item) {
		return origin.isEqual(item);
	}

	@Override
	public boolean isNotEqual(Module item) {
		return origin.isNotEqual(item);
	}

	@Override
	public boolean isPresent() {
		return origin.isPresent();
	}

	@Override
	public PurchaseOrders orders() {
		return new Orders(base, this);
	}
}
