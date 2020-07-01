package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class SellerMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public SellerMetadata() {
		this.domainName = "sales.sellers";
		this.keyName = "id";
	}
	
	public SellerMetadata(final String domainName, final String keyName){
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
	
	public String teamIdKey(){
		return "teamid";
	}
	
	public static SellerMetadata create(){
		return new SellerMetadata();
	}
}
