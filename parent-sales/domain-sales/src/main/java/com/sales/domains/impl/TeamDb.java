package com.sales.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;
import com.sales.domains.api.TeamMetadata;

public final class TeamDb extends GuidKeyEntityDb<Team, TeamMetadata> implements Team {

	private final Sales module;
	
	public TeamDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Equipe commerciale introuvable !");
		this.module = module;
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public String shortName() throws IOException {
		return ds.get(dm.shortNameKey());
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}
	
	@Override
	public void update(String name, String shortName) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (StringUtils.isBlank(shortName)) {
            throw new IllegalArgumentException("Invalid short name : it can't be empty!");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.shortNameKey(), shortName);
		
		ds.set(params);		
	}

	@Override
	public Sellers members() throws IOException {
		return module().sellers().of(this);
	}
}
