package com.sales.domains.api;

public enum ProvisionStep {
	
	NONE(0, "Non d�finie"), 
	CREATING(1, "Provision en cr�ation"), 
	EMITTED(2, "Provision effectu�e"), 
	SEND_TO_ACCOUNTING(3, "Envoy�e � la comptabilit�");
	
	private final int id;
	private final String name;
	
	ProvisionStep(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static ProvisionStep get(int id){
		
		ProvisionStep value = ProvisionStep.NONE;
		for (ProvisionStep item : ProvisionStep.values()) {
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
