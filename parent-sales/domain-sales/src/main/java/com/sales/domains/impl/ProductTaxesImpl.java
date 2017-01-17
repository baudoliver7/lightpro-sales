package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.ProductTaxMetadata;
import com.sales.domains.api.ProductTaxes;
import com.securities.api.Tax;
import com.securities.impl.TaxImpl;

public class ProductTaxesImpl implements ProductTaxes {

	private final transient Base base;
	private final transient Object productId;
	private final transient ProductTaxMetadata dm;
	private final transient DomainsStore ds;
	
	public ProductTaxesImpl(final Base base, Object productId){
		this.base = base;
		this.dm = new ProductTaxMetadata();
		this.ds = base.domainsStore(dm);
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
			values.add(new TaxImpl(this.base, domainStore.key())); 
		}		
		
		return values;
	}

	@Override
	public void add(Tax item) throws IOException {
		if (!item.isPresent()) {
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
}
