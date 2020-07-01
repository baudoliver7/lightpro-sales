package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyQueryableDb;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.ModulePdvMetadata;
import com.sales.domains.api.ModulePdvs;
import com.sales.domains.api.Sales;

public final class ModulePdvsDb extends GuidKeyQueryableDb<ModulePdv, ModulePdvMetadata> implements ModulePdvs {

	private final transient ModulePdv moduleVenteDirecte;
	private final transient Sales module;
	
	public ModulePdvsDb(final Base base, final Sales module){
		super(base, "Module de vente introuvable !");
		this.module = module;
		this.moduleVenteDirecte = new ModuleVenteDirecte(base, module);		
	}
	
	private QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		String statement = String.format("%s mpd "
				+ "WHERE mpd.%s=? AND mpd.%s ILIKE ?",				 
				dm.domainName(), 
				dm.companyIdKey(), dm.nameKey());
		
		params.add(module.company().id());
		params.add("%" + filter + "%"); 
		

		String orderClause = String.format("ORDER BY mpd.%s ASC", dm.nameKey());
		
		String keyResult = String.format("mpd.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}
	
	@Override
	public List<ModulePdv> all() throws IOException {
		
		List<ModulePdv> pdvs =  buildQuery(StringUtils.EMPTY).find()
				  .stream()
				  .map(m -> build(UUIDConvert.fromObject(m)))
				  .collect(Collectors.toList());
		
		pdvs.add(0, moduleVenteDirecte);
		
		return pdvs;
	}

	@Override
	protected ModulePdv newOne(UUID id) {
		if(moduleVenteDirecte.id().equals(id))
			return moduleVenteDirecte;
		
		return new ModulePdvDb(base, id);
	}

	@Override
	public ModulePdv none() {
		return new ModulePdvNone();
	}
}
