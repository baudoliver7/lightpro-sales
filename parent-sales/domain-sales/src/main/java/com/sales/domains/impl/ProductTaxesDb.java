package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.ProductTaxMetadata;
import com.sales.domains.api.ProductTaxes;
import com.securities.api.Tax;
import com.securities.impl.TaxDb;
import com.securities.impl.TaxListFormatter;
import com.securities.impl.TaxNone;

public final class ProductTaxesDb extends GuidKeyQueryableDb<Tax, ProductTaxMetadata> implements ProductTaxes {

	private final transient UUID productId;
	
	public ProductTaxesDb(final Base base, UUID productId){
		super(base, "Taxe du produit introuvable !");
		this.productId = productId;
	}
	
	@Override
	public List<Tax> all() throws IOException {
		List<Tax> values = new ArrayList<Tax>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement = String.format("SELECT %s FROM %s WHERE %s=? ORDER BY %s ASC", dm.taxIdKey(), dm.domainName(), dm.productIdKey(), hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(productId);
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(new TaxDb(this.base, UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		return values;
	}

	@Override
	public void add(Tax item) throws IOException {
		if (item.isNone()) {
            throw new IllegalArgumentException("Invalid tax : it can't be empty!");
        }
		
		if(getId(item) != null){
			throw new IllegalArgumentException("Cette taxe est déjà configurée pour ce produit !");
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.productIdKey(), productId);
		params.put(dm.taxIdKey(), item.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
	}

	@Override
	public void delete(Tax item) throws IOException {		
		ds.delete(getId(item));		
	}	
	
	private Object getId(Tax item) throws IOException{
		List<Object> results = ds.find(String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.productIdKey(), dm.taxIdKey()), Arrays.asList(this.productId, item.id()));
		
		if(results.isEmpty())
			return null;
		else
			return results.get(0);
	}

	@Override
	public double evaluateAmount(double amountHt) throws IOException {
		double amount = 0;
		
		for (Tax tax : all()) {
			amount += tax.evaluateAmount(amountHt);
		}
		
		return amount;
	}

	@Override
	public void deleteAll() throws IOException {
		for (Tax tax : all()) {
			delete(tax);
		}
	}

	@Override
	public String toString() {
		try {
			return new TaxListFormatter(all()).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Tax newOne(UUID id) {
		return new TaxDb(base, id);
	}

	@Override
	public Tax none() {
		return new TaxNone();
	}
}
