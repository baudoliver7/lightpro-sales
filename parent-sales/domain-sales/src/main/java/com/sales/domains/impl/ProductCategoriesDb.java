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
import com.sales.domains.api.ProductCategories;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryMetadata;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;

public final class ProductCategoriesDb extends GuidKeyAdvancedQueryableDb<ProductCategory, ProductCategoryMetadata> implements ProductCategories {

	private transient final Sales module;
	private transient final UseCode useCode;
	private transient final String shortName;
	
	public ProductCategoriesDb(Base base, Sales module, UseCode useCode, String shortName) {
		super(base, "Catégorie de produit introuvable !");
		
		this.module = module;
		this.useCode = useCode;
		this.shortName = shortName;
	}

	private ProductCategory addCategory(UUID id, ProductCategoryType type, String name, String description, UseCode useCode, String shortName) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Libellé invalide : vous devez fournir une valeur !");
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		params.put(dm.moduleIdKey(), module.id());
		params.put(dm.shortNameKey(), shortName);
		params.put(dm.useCodeKey(), useCode.id());
		params.put(dm.typeIdKey(), type.id());
		
		ds.set(id, params);
		
		return build(id);
	}
	
	@Override
	public ProductCategory add(ProductCategoryType type, String name, String description) throws IOException {		
		return addCategory(UUID.randomUUID(), type, name, description, UseCode.USER, StringUtils.EMPTY);
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		String statement = String.format("%s pdcat "
				+ "WHERE pdcat.%s ILIKE ? AND pdcat.%s=?",				 
				dm.domainName(), 
				dm.nameKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(useCode != UseCode.NONE){
			statement = String.format("%s AND pdcat.%s=?", statement, dm.useCodeKey());
			params.add(useCode.id());
		}
		
		if(!StringUtils.isBlank(shortName)){
			statement = String.format("%s AND pdcat.%s=?", statement, dm.shortNameKey());
			params.add(shortName);
		}
		
		HorodateMetadata horodateDm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY pdcat.%s DESC", horodateDm.dateCreatedKey());
		
		String keyResult = String.format("pdcat.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected ProductCategory newOne(UUID id) {
		return new ProductCategoryDb(base, id, module);
	}

	@Override
	public ProductCategory none() {
		return new ProductCategoryNone();
	}

	@Override
	public ProductCategory getAcompteCategory() throws IOException {
		
		ProductCategory category;
		
		ProductCategories categories = withUseCode(UseCode.SYSTEM).withShortName("ACPT");
		
		if(categories.count() == 0){
			category = addCategory(UUID.randomUUID(), ProductCategoryType.ACOMPTE_AVANCE, "Acomptes", "", UseCode.SYSTEM, "ACPT");
		}else{
			category = categories.all().get(0);
		}
		
		return category;
	}

	@Override
	public ProductCategories withUseCode(UseCode code) throws IOException {
		return new ProductCategoriesDb(base, module, code, shortName);
	}

	@Override
	public ProductCategories withShortName(String shortName) throws IOException {
		return new ProductCategoriesDb(base, module, useCode, shortName);
	}

	@Override
	public ProductCategory getDefaultRemiseCategory() throws IOException {
		
		ProductCategory category;
		
		ProductCategories categories = withUseCode(UseCode.SYSTEM).withShortName("REM");
		
		if(categories.count() == 0){
			category = addCategory(UUID.randomUUID(), ProductCategoryType.REDUCTION_COMMERCIALE, "Remise", "", UseCode.SYSTEM, "REM");
		}else{
			category = categories.all().get(0);
		}
		
		return category;
	}

	@Override
	public ProductCategory getDefaultRabaisCategory() throws IOException {
		
		ProductCategory category;
		
		ProductCategories categories = withUseCode(UseCode.SYSTEM).withShortName("RAB");
		
		if(categories.count() == 0){
			category = addCategory(UUID.randomUUID(), ProductCategoryType.REDUCTION_COMMERCIALE, "Rabais", "", UseCode.SYSTEM, "RAB");
		}else{
			category = categories.all().get(0);
		}
		
		return category;
	}

	@Override
	public ProductCategory getDefaultRistourneCategory() throws IOException {

		ProductCategory category;
		
		ProductCategories categories = withUseCode(UseCode.SYSTEM).withShortName("RIS");
		
		if(categories.count() == 0){
			category = addCategory(UUID.randomUUID(), ProductCategoryType.REDUCTION_COMMERCIALE, "Ristourne", "", UseCode.SYSTEM, "RIS");
		}else{
			category = categories.all().get(0);
		}
		
		return category;
	}

	@Override
	public ProductCategory getEscompteCategory() throws IOException {
		ProductCategory category;
		
		ProductCategories categories = withUseCode(UseCode.SYSTEM).withShortName("ESC");
		
		if(categories.count() == 0){
			category = addCategory(UUID.randomUUID(), ProductCategoryType.REDUCTION_FINANCIERE, "Escompte", "", UseCode.SYSTEM, "ESC");
		}else{
			category = categories.all().get(0);
		}
		
		return category; 
	}

	@Override
	public ProductCategory add(ProductCategoryType type, String name, String description, UseCode useCode) throws IOException {
		return addCategory(UUID.randomUUID(), type, name, description, useCode, StringUtils.EMPTY);
	}

	@Override
	public ProductCategory add(UUID id, ProductCategoryType type, String name, String description, UseCode useCode) throws IOException {
		return addCategory(id, type, name, description, useCode, StringUtils.EMPTY);
	}
	
	@Override
	public void delete(ProductCategory item) throws IOException {
		
		ComptaInterface comptaInterface = module.interfacage().comptaInterface();
		
		if(comptaInterface.available())
			comptaInterface.removeProductCategoryInterface(item);
		
		super.delete(item);
	}
}
