package com.sales.domains.api;

public enum ProvisionStatus {
	
	NONE(0, "Non défini"),
	DRAFT(1, "Brouillon"), 
	NON_CONSOMMEE(2, "Non consommée"), 
	CONSOMMEE(3, "Consommée");
	
	private final int id;
	private final String name;
	
	ProvisionStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static ProvisionStatus get(int id){
		
		ProvisionStatus value = ProvisionStatus.NONE;
		for (ProvisionStatus item : ProvisionStatus.values()) {
			if(item.id() == id)
				value = item;
		}
		
		return value;
	}

	public int id(){
		return id;
	}
	
	public String toString(){
		return name;
	}
}
