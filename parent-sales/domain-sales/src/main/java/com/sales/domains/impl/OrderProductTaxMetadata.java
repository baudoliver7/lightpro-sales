package com.sales.domains.impl;

import com.infrastructure.core.DomainMetadata;

public class OrderProductTaxMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public OrderProductTaxMetadata() {
		this.domainName = "sales.orderproducttaxes";
		this.keyName = "id";
	}
	
	public OrderProductTaxMetadata(final String domainName, final String keyName){
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

	public String amountKey(){
		return "amount";
	}
	
	public String taxIdKey(){
		return "taxid";
	}	
	
	public String orderProductIdKey(){
		return "orderproductid";
	}
	
	public String nameKey(){
		return "name";
	}	
	
	public String shortNameKey(){
		return "shortname";
	}	
	
	public String valueKey(){
		return "value";
	}
	
	public String valueTypeKey(){
		return "valuetype";
	}
	
	public String typeIdKey(){
		return "typeid";
	}
	
	public static OrderProductTaxMetadata create(){
		return new OrderProductTaxMetadata();
	}
}
