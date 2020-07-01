package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class TeamMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public TeamMetadata() {
		this.domainName = "sales.teams";
		this.keyName = "id";
	}
	
	public TeamMetadata(final String domainName, final String keyName){
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
	
	public String shortNameKey(){
		return "shortname";
	}		
	
	public String moduleIdKey(){
		return "moduleid";
	}
	
	public static TeamMetadata create(){
		return new TeamMetadata();
	}

}
