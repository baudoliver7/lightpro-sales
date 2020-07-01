package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.ModulePdvMetadata;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.securities.api.Company;
import com.securities.impl.CompanyDb;

public final class ModulePdvDb extends GuidKeyEntityDb<ModulePdv, ModulePdvMetadata> implements ModulePdv {
	
	public ModulePdvDb(final Base base, UUID id){
		super(base, id, "Module de vente introuvable !");
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public Company company() throws IOException {
		UUID companyId = ds.get(dm.companyIdKey());
		return new CompanyDb(base, companyId);
	}

	@Override
	public double turnover(LocalDate start, LocalDate end) throws IOException {
		PaymentMetadata dmPay = PaymentMetadata.create();
		DomainsStore ds = base.domainsStore(dmPay);
		
		String statement = String.format("SELECT SUM(pay.%s) FROM %s pay "
										+ "WHERE (pay.%s BETWEEN ? AND ?) AND pay.%s=? AND pay.%s=? AND pay.%s=? AND pay.%s IS NULL", 
										dmPay.paidAmountKey(), dmPay.domainName(),
										dmPay.paymentDateKey(), dmPay.modulePdvIdKey(), dmPay.statusIdKey(), dmPay.typeIdKey(), dmPay.provisionIdKey());
		
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
