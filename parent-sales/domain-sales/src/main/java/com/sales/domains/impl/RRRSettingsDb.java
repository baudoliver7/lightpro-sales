package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.RRRSetting;
import com.sales.domains.api.RRRSettingMetadata;
import com.sales.domains.api.RRRSettings;
import com.sales.domains.api.RRRType;
import com.sales.domains.api.Sales;

public final class RRRSettingsDb implements RRRSettings {

	private transient final Base base;
	private transient final RRRSettingMetadata dm;
	private transient final Sales module;
	
	public RRRSettingsDb(Base base, Sales module){
		this.base = base;
		this.dm = RRRSettingMetadata.create();
		this.module = module;
	}
	
	private RRRSetting build(UUID id){
		return new RRRSettingDb(base, id, module);
	}
	
	private RRRSetting add(RRRType reductionType, ProductCategoryType exploitationProduct, Product reductionProduct) throws IOException {
		
		if(reductionType == RRRType.NONE)
			throw new IllegalArgumentException("Vous devez indiquer le type de réduction !");
		
		if(exploitationProduct == ProductCategoryType.NONE)
			throw new IllegalArgumentException("Vous devez spécifier le type de produit !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.reductionTypeIdKey(), reductionType.id());
		params.put(dm.productCategoryTypeIdKey(), exploitationProduct.id());
		params.put(dm.reductionProductIdKey(), reductionProduct.id());
		params.put(dm.reductionProductIdKey(), reductionProduct.id());
		
		UUID id = UUID.randomUUID();
		base.domainsStore(dm).set(id, params);
		
		return build(id);
	}
	
	private Product defaultReductionProduct(RRRType reductionType) throws IOException{
		// récupérer la réduction commerciale par défaut
		Product product = new ProductNone();
		
		switch (reductionType) {
			case REMISE:
				product = module.products().getDefaultRemiseProduct();
				break;
			case RABAIS:
				product = module.products().getDefaultRemiseProduct();
				break;
			case RISTOURNE:
				product = module.products().getDefaultRemiseProduct();
				break;
			default:
				break;
		}
		
		return product;
	}
	
	@Override
	public RRRSetting setting(RRRType reductionType, ProductCategoryType exploitationProduct) throws IOException {

		RRRSetting setting;
		
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.reductionProductIdKey(), dm.domainName(), dm.reductionTypeIdKey(), dm.productCategoryTypeIdKey());
		List<Object> results = base.executeQuery(statement, Arrays.asList(reductionType.id(), exploitationProduct.id()));
		
		if(results.isEmpty()){
						
			setting = add(reductionType, exploitationProduct, defaultReductionProduct(reductionType));
		}else{
			setting = build(UUIDConvert.fromObject(results.get(0)));
			
			if(setting.reductionProduct().isNone()) // vérifier que la réduction commerciale est paramétrée
				setting.update(defaultReductionProduct(reductionType)); // sinon paramétrer la réduction commerciale par défaut
		}
		
		return setting;
	}

	@Override
	public List<RRRSetting> settings(RRRType reductionType) throws IOException {
		List<RRRSetting> settings = new ArrayList<RRRSetting>();
		
		for (ProductCategoryType explProdCat : ProductCategoryType.exploitationProducts()) {
			settings.add(setting(reductionType, explProdCat));
		}
		
		return settings;
	}

}
