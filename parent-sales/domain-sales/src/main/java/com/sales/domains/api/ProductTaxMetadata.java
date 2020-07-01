package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class ProductTaxMetadata implements DomainMetadata {
	
	private final transient String domainName;
	private final transient String keyName;
	
	public ProductTaxMetadata() {
		this.domainName = "sales.producttaxes";
		this.keyName = "id";
	}
	
	public ProductTaxMetadata(final String domainName, final String keyName){
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

	public String productIdKey(){
		return "productid";
	}
	
	public String taxIdKey(){
		return "taxid";
	}
	
	public static ProductTaxMetadata create(){
		return new ProductTaxMetadata();
	}
}
