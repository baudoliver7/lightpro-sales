package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductMetadata;
import com.sales.domains.api.Products;
import com.securities.api.MesureUnit;

public class ProductsImpl implements Products {

	private transient final Base base;
	private final transient ProductMetadata dm;
	private final transient DomainsStore ds;
	
	public ProductsImpl(final Base base){
		this.base = base;
		this.dm = ProductImpl.dm();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public List<Product> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Product> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Product> find(int page, int pageSize, String filter) throws IOException {
		List<Product> values = new ArrayList<Product>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement = String.format("SELECT %s FROM %s WHERE %s ILIKE ? OR %s ILIKE ? OR %s ILIKE ? ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.nameKey(), dm.barCodeKey(), dm.descriptionKey(), hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(new ProductImpl(this.base, domainStore.key())); 
		}		
		
		return values;
	}

	@Override
	public int totalCount(String filter) throws IOException {
		String statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s ILIKE ? OR %s ILIKE ? OR %s ILIKE ?", dm.keyName(), dm.domainName(), dm.nameKey(), dm.barCodeKey(), dm.descriptionKey());
		
		List<Object> params = new ArrayList<Object>();
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());	
	}

	@Override
	public Product get(UUID id) throws IOException {
		if(!ds.exists(id))
			throw new NotFoundException("Le produit n'a pas été trouvé !");
		
		return new ProductImpl(this.base, id);
	}

	@Override
	public Product add(String name, String barCode, String description, MesureUnit unit) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (!unit.isPresent()) {
            throw new IllegalArgumentException("Invalid mesure unit : it can't be empty!");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		params.put(dm.mesureUnitIdKey(), unit.id());
		params.put(dm.barCodeKey(), barCode);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return new ProductImpl(this.base, id);	
	}

	@Override
	public void delete(Product item) throws IOException {
		ds.delete(item.id());
	}

	@Override
	public boolean contains(Product item) throws IOException {
		return ds.exists(item.id());
	}

	@Override
	public Product build(Object id) {
		return new ProductImpl(base, id);
	}
}
