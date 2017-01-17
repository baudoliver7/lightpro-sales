package com.sales.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductMetadata;
import com.sales.domains.api.ProductTaxes;
import com.securities.api.MesureUnit;
import com.securities.impl.MesureUnitImpl;

public class ProductImpl implements Product {

	private final transient Base base;
	private final transient Object id;
	private final transient ProductMetadata dm;
	private final transient DomainStore ds;
	
	public ProductImpl(final Base base, final Object id){
		this.base = base;
		this.id = id;
		this.dm = dm();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return UUIDConvert.fromObject(this.id);
	}

	@Override
	public boolean isPresent() throws IOException {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public String barCode() throws IOException {
		return ds.get(dm.barCodeKey());
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public MesureUnit unit() throws IOException {
		UUID mesureUnitId = ds.get(dm.mesureUnitIdKey());
		return new MesureUnitImpl(this.base, mesureUnitId);
	}

	public static ProductMetadata dm(){
		return new ProductMetadata();
	}

	@Override
	public void update(String name, String barCode, String description, MesureUnit unit) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (!unit.isPresent()) {
            throw new IllegalArgumentException("Invalid unit : it can't be empty!");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		params.put(dm.mesureUnitIdKey(), unit.id());
		params.put(dm.barCodeKey(), barCode);
		
		ds.set(params);					
	}

	@Override
	public ProductTaxes taxes() {
		return new ProductTaxesImpl(base, id);
	}

	@Override
	public Pricing pricing() throws IOException {
		return new PricingImpl(base, id);
	}
}
