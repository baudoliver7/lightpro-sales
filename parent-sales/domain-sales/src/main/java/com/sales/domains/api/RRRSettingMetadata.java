package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class RRRSettingMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public RRRSettingMetadata() {
		this.domainName = "sales.rrr_settings";
		this.keyName = "id";
	}
	
	public RRRSettingMetadata(final String domainName, final String keyName){
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

	public String productCategoryTypeIdKey(){
		return "product_category_typeid";
	}
	
	public String reductionProductIdKey(){
		return "reduction_productid";
	}	
	
	public String reductionTypeIdKey(){
		return "reduction_typeid";
	}
	
	public String moduleIdKey(){
		return "moduleid";
	}
	
	public static RRRSettingMetadata create(){
		return new RRRSettingMetadata();
	}
}
