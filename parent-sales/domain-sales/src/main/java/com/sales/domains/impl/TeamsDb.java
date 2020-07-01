package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Seller;
import com.sales.domains.api.Team;
import com.sales.domains.api.TeamMetadata;
import com.sales.domains.api.Teams;

public final class TeamsDb extends GuidKeyAdvancedQueryableDb<Team, TeamMetadata> implements Teams {

	private final transient Sales module;
	
	public TeamsDb(final Base base, final Sales module){
		super(base, "Equipe commerciale introuvable !");
		this.module = module;
	}

	@Override
	public void delete(Team item) throws IOException {
		if(contains(item))
		{
			for (Seller sl : item.members().all()) {
				item.members().delete(sl);
			}
			
			ds.delete(item.id());
		}
	}

	@Override
	public Team add(String name, String shortName) throws IOException {
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (StringUtils.isBlank(shortName)) {
            throw new IllegalArgumentException("Invalid short name : it can't be empty!");
        }		
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.shortNameKey(), shortName);
		params.put(dm.moduleIdKey(), module.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);	
		
		return build(id);
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);

		String statement = String.format("%s te "
				+ "WHERE (te.%s ILIKE ? OR te.%s ILIKE ?) AND te.%s=?", 
				dm.domainName(), 
				dm.nameKey(), dm.shortNameKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		String orderClause;	
		HorodateMetadata hm = new HorodateMetadata();
		
		orderClause = String.format("ORDER BY te.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("te.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected Team newOne(UUID id) {
		return new TeamDb(base, id, module);
	}

	@Override
	public Team none() {
		return new TeamNone();
	}
}
