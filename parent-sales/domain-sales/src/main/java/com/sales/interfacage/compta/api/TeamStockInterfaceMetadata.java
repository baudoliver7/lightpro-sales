package com.sales.interfacage.compta.api;

import com.infrastructure.core.DomainMetadata;

public final class TeamStockInterfaceMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public TeamStockInterfaceMetadata() {
		this.domainName = "sales.team_stock_interfaces";
		this.keyName = "id";
	}
	
	public TeamStockInterfaceMetadata(final String domainName, final String keyName){
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

	public String warehouseIdKey(){
		return "warehouseid";
	}
	
	public String livraisonOpTypeIdKey(){
		return "livraison_optypeid";
	}	
	
	public static TeamStockInterfaceMetadata create(){
		return new TeamStockInterfaceMetadata();
	}
}
