package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Pricing;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductMetadata;
import com.sales.domains.api.ProductTaxes;
import com.sales.domains.api.Products;
import com.sales.domains.api.Sales;
import com.securities.api.MesureUnit;
import com.securities.api.Tax;
import com.securities.impl.MesureUnitDb;

public final class ProductDb extends GuidKeyEntityDb<Product, ProductMetadata> implements Product {
	
	private final Sales module;
	
	public ProductDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Produit introuvable !");
		this.module = module;
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
		return new MesureUnitDb(this.base, mesureUnitId);
	}

	public static ProductMetadata dm(){
		return new ProductMetadata();
	}

	@Override
	public void update(String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException {
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (unit.isNone()) {
            throw new IllegalArgumentException("Invalid unit : it can't be empty!");
        }
		
		if(StringUtils.isBlank(emballage))
			throw new IllegalArgumentException("Emballage invalide : il ne peut pas être vide !");
		
		if(quantity <= 0)
			throw new IllegalArgumentException("Quantité invalide : elle doit être supérieure à 0 !");
		
		if(category.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la catégorie du produit !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(!StringUtils.isBlank(internalReference)){
			internalReference = internalReference.trim();
			
			Products products = module().products().withInternalReference(internalReference);
			if(!products.isEmpty() && !products.first().equals(this))
				throw new IllegalArgumentException("Cette référence interne existe déjà !");
			
			params.put(dm.internalReferenceKey(), internalReference);
		}
					
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		params.put(dm.mesureUnitIdKey(), unit.id());
		params.put(dm.barCodeKey(), barCode);
		params.put(dm.emballageKey(), emballage);
		params.put(dm.quantityKey(), quantity);
		params.put(dm.categoryIdKey(), category.id());
				
		ds.set(params);					
	}

	@Override
	public ProductTaxes taxes() {
		return new ProductTaxesDb(base, id);
	}

	@Override
	public Pricing pricing() throws IOException {
		return new PricingDb(base, id, module);
	}	

	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public String emballage() throws IOException {
		return ds.get(dm.emballageKey());
	}

	@Override
	public double quantity() throws IOException {
		return ds.get(dm.quantityKey());
	}

	@Override
	public ProductCategory category() throws IOException {
		UUID categoryId = ds.get(dm.categoryIdKey());
		return module().productCategories().get(categoryId);
	}

	@Override
	public OrderProduct generate(double quantity, LocalDate orderDate, double unitPrice, List<Tax> taxes) throws IOException {
		return new OrderProductTemplate(this, quantity, unitPrice, orderDate, taxes);
	}

	@Override
	public String internalReference() throws IOException {
		return ds.get(dm.internalReferenceKey());
	}
}
