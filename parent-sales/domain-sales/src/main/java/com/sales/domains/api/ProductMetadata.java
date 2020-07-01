package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class ProductMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public ProductMetadata() {
		this.domainName = "sales.products";
		this.keyName = "id";
	}
	
	public ProductMetadata(final String domainName, final String keyName){
		this.domainName = domainName;
		this.keyName = keyName;
	}
	
	@Override
	public String domainName() {
		return this.domainName;
	}

	@Override
	public String keyName() {
		return this.keyName;
	}

	public String nameKey(){
		return "name";
	}
	
	public String barCodeKey(){
		return "barcode";
	}	
	
	public String descriptionKey(){
		return "description";
	}
	
	public String mesureUnitIdKey(){
		return "mesureunitid";
	}	
	
	public String moduleIdKey(){
		return "moduleid";
	}		
	
	public String emballageKey(){
		return "emballage";
	}
	
	public String quantityKey(){
		return "quantity";
	}
	
	public String categoryIdKey(){
		return "categoryid";
	}
	
	public String manageStockKey(){
		return "manage_stock";
	}
	
	public String articleIdKey(){
		return "articleid";
	}
	
	public String internalReferenceKey(){
		return "internal_reference";
	}
	
	public static ProductMetadata create(){
		return new ProductMetadata();
	}
}
