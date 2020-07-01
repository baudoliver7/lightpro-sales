package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public final class ModulePdvMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public ModulePdvMetadata() {
		this.domainName = "sales.module_pdvs";
		this.keyName = "id";
	}
	
	public ModulePdvMetadata(final String domainName, final String keyName){
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
	
	public String companyIdKey(){
		return "companyid";
	}
	
	public static ModulePdvMetadata create(){
		return new ModulePdvMetadata();
	}
}
