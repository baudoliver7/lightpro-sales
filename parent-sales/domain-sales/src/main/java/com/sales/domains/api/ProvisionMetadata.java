package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class ProvisionMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public ProvisionMetadata() {
		this.domainName = "sales.provisions";
		this.keyName = "id";
	}
	
	public ProvisionMetadata(final String domainName, final String keyName){
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

	public String referenceKey(){
		return "reference";
	}
	
	public String amountKey(){
		return "amount";
	}
	
	public String statusIdKey(){
		return "statusid";
	}	
	
	public String stepIdKey(){
		return "stepid";
	}
	
	public String customerIdKey(){
		return "customerid";
	}
	
	public static ProvisionMetadata create(){
		return new ProvisionMetadata();
	}
}
