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
import com.infrastructure.core.UseCode;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Seller;
import com.sales.domains.api.SellerMetadata;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;
import com.sales.domains.api.TeamMetadata;
import com.securities.api.Contact;
import com.securities.api.ContactMetadata;
import com.securities.api.ContactNature;

public final class SellersDb extends GuidKeyAdvancedQueryableDb<Seller, SellerMetadata> implements Sellers {

	private final transient UseCode useCode;
	private final transient Sales module;
	private final transient Team team;
	
	public SellersDb(final Base base, final Sales module, final UseCode useCode, final Team team){
		super(base, "Vendeur introuvable !");
		this.useCode = useCode;
		this.module = module;
		this.team = team;
	}

	@Override
	public void delete(Seller item) throws IOException {
		if(contains(item)){
			ds.delete(item.id()); // suppression de l'équipe seulement
		}		
	}

	@Override
	public Seller add(Contact item) throws IOException {
		if(team.isNone())
			throw new IllegalArgumentException("Vous devez spécifier une équipe commerciale !");
		
		if(item.isNone())
			throw new IllegalArgumentException("La personne spécifiée n'existe pas !");
		
		if(item.nature() == ContactNature.SOCIETY)
			throw new IllegalArgumentException("Vous devez indiquer une personne physique !");
		
		if(ds.exists(item.id()))
			throw new IllegalArgumentException("Le vendeur existe déjà !");
					
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.teamIdKey(), team.id());
		ds.set(item.id(), params);
		
		return build(item.id());
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);

		ContactMetadata persDm = ContactMetadata.create();		
		TeamMetadata teDm = TeamMetadata.create();	
		
		String statement = String.format("%s sel "
				+ "JOIN view_contacts vctc ON vctc.%s=sel.%s "
				+ "JOIN %s te ON te.%s=sel.%s "
				+ "WHERE (vctc.name1 ILIKE ? OR vctc.name2 ILIKE ?) AND te.%s=?", 
				dm.domainName(), 
				persDm.keyName(), dm.keyName(),
				teDm.domainName(), teDm.keyName(), dm.teamIdKey(),
				teDm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(useCode != UseCode.NONE) {
			statement = String.format("%s AND vctc.%s=?", statement, persDm.useCodeIdKey());
			params.add(useCode.id());
		}
		
		if(!team.isNone()) {
			statement = String.format("%s AND te.%s=?", statement, teDm.keyName());
			params.add(team.id());
		}
		
		String orderClause;	
		HorodateMetadata hm = new HorodateMetadata();
		
		orderClause = String.format("ORDER BY sel.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("sel.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected Seller newOne(UUID id) {
		return new SellerDb(base, id, module);
	}

	@Override
	public Seller none() {
		return new SellerNone();
	}

	@Override
	public Sellers of(Team team) throws IOException {
		return new SellersDb(base, module, useCode, team);
	}

	@Override
	public boolean contains(Contact contact) {
		Seller seller = build(contact.id());
		return contains(seller);
	}
}
