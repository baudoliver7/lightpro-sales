package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class CustomerMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public CustomerMetadata() {
		this.domainName = "sales.customers";
		this.keyName = "id";
	}
	
	public CustomerMetadata(final String domainName, final String keyName){
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
	
	public static CustomerMetadata create(){
		return new CustomerMetadata();
	}
}
