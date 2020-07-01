package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;

public final class TeamNone extends GuidKeyEntityNone<Team> implements Team {

	@Override
	public String name() throws IOException {
		return null;
	}

	@Override
	public String shortName() throws IOException {
		return null;
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public Sellers members() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void update(String name, String shortName) throws IOException {
		
	}
}
