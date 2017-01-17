package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class IntervalPricingMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public IntervalPricingMetadata() {
		this.domainName = "sales.intervalpricings";
		this.keyName = "id";
	}
	
	public IntervalPricingMetadata(final String domainName, final String keyName){
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

	public String beginKey(){
		return "begin";
	}
	
	public String endKey(){
		return "endt";
	}	
	
	public String priceKey(){
		return "price";
	}	
	
	public String pricingIdKey(){
		return "pricingid";
	}
	
	public String priceTypeIdKey(){
		return "pricetypeid";
	}
	
	public static IntervalPricingMetadata create(){
		return new IntervalPricingMetadata();
	}
}
