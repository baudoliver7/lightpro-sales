package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.infrastructure.core.GuidKeyEntityBase;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Sales;
import com.securities.api.Company;

public final class ModuleVenteDirecte extends GuidKeyEntityBase<ModulePdv> implements ModulePdv {

	private transient final Sales module;
	private transient final Base base;
	
	public ModuleVenteDirecte(Base base, Sales module){
		super(module.id());
		this.module = module;
		this.base = base;
	}

	@Override
	public String name() throws IOException {
		return "Vente libre";
	}

	@Override
	public Company company() throws IOException {
		return module.company();
	}

	@Override
	public double turnover(LocalDate start, LocalDate end) throws IOException {
		PaymentMetadata dmPay = PaymentMetadata.create();
		DomainsStore ds = base.domainsStore(dmPay);
		
		String statement = String.format("SELECT SUM(pay.%s) FROM %s pay "
										+ "WHERE (pay.%s BETWEEN ? AND ?) AND pay.%s=? AND pay.%s=? AND pay.%s=?", 
										dmPay.paidAmountKey(), dmPay.domainName(),
										dmPay.paymentDateKey(), dmPay.modulePdvIdKey(), dmPay.statusIdKey(), dmPay.typeIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(this.id);
		params.add(PaymentStatus.VALIDATED.id());
		params.add(PaymentType.ENCAISSEMENT.id());
		
		List<Object> results = ds.find(statement, params);
		return Double.parseDouble(results.get(0) == null ? "0": results.get(0).toString());
	}
}
