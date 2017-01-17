package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class PricingMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public PricingMetadata() {
		this.domainName = "sales.pricings";
		this.keyName = "id";
	}
	
	public PricingMetadata(final String domainName, final String keyName){
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

	public String modeIdKey(){
		return "modeid";
	}
	
	public String fixPriceKey(){
		return "fixprice";
	}		
}
