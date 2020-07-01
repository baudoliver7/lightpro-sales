package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.RRRSetting;
import com.sales.domains.api.RRRSettingMetadata;
import com.sales.domains.api.RRRType;
import com.sales.domains.api.Sales;

public final class RRRSettingDb extends GuidKeyEntityDb<RRRSetting, RRRSettingMetadata> implements RRRSetting {

	private final Sales module;
	public RRRSettingDb(Base base, UUID id, Sales module) {
		super(base, id, "Interface RRR introuvable !");
		this.module = module;
	}

	@Override
	public RRRType reductionType() throws IOException {
		int typeId = ds.get(dm.reductionTypeIdKey());
		return RRRType.get(typeId);
	}

	@Override
	public ProductCategoryType exploitationProductCategory() throws IOException {
		int typeId = ds.get(dm.productCategoryTypeIdKey());
		return ProductCategoryType.get(typeId);
	}

	@Override
	public Product reductionProduct() throws IOException {
		UUID productId = ds.get(dm.reductionProductIdKey());
		return new ProductDb(base, productId, module);
	}

	@Override
	public void update(Product reductionProduct) throws IOException {
		ds.set(dm.reductionProductIdKey(), reductionProduct.id());
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}
}
