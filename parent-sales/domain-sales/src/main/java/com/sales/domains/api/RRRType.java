package com.sales.domains.api;

public enum RRRType {
	
	NONE(0, "Non définie"), 
	REMISE(1, "Remise"), 
	RABAIS(2, "Rabais"), 
	RISTOURNE(3, "Ristourne");
	
	private final int id;
	private final String name;
	
	RRRType(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static RRRType get(int id){
		
		RRRType value = RRRType.NONE;
		for (RRRType item : RRRType.values()) {
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
