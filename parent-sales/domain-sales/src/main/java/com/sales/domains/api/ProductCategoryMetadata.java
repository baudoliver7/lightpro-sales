package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class ProductCategoryMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public ProductCategoryMetadata() {
		this.domainName = "sales.product_categories";
		this.keyName = "id";
	}
	
	public ProductCategoryMetadata(final String domainName, final String keyName){
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
	
	public String descriptionKey(){
		return "description";
	}	
	
	public String moduleIdKey(){
		return "moduleid";
	}	
	
	public String useCodeKey(){
		return "use_code";
	}
	
	public String shortNameKey(){
		return "shortname";
	}
	
	public String typeIdKey(){
		return "typeid";
	}
	
	public static ProductCategoryMetadata create(){
		return new ProductCategoryMetadata();
	}
}
