package com.sales.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryMetadata;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.Products;
import com.sales.domains.api.Sales;

public final class ProductCategoryDb extends GuidKeyEntityDb<ProductCategory, ProductCategoryMetadata> implements ProductCategory {
	
	private final Sales module;
	
	public ProductCategoryDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Catégorie de produit introuvable !");
		this.module = module;
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public void update(String name, String description) throws IOException {

		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Libellé invalide : vous devez fournir une valeur !");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		
		ds.set(params);	
	}

	@Override
	public Products products() throws IOException {
		return module().products().of(this);
	}

	@Override
	public ProductCategoryType type() throws IOException {
		int typeId = ds.get(dm.typeIdKey());
		return ProductCategoryType.get(typeId);
	}
}
