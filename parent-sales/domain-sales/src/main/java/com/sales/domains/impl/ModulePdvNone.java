package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.ModulePdv;
import com.securities.api.Company;
import com.securities.impl.CompanyNone;

public final class ModulePdvNone extends GuidKeyEntityNone<ModulePdv> implements ModulePdv {

	@Override
	public String name() throws IOException {
		return "Aucun point de vente";
	}

	@Override
	public Company company() throws IOException {
		return new CompanyNone();
	}

	@Override
	public double turnover(LocalDate start, LocalDate end) throws IOException {
		return 0;
	}
}
