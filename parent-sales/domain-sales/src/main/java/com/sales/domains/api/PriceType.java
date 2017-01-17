package com.sales.domains.api;

public enum PriceType {
	NONE(0, "Non défini"),
	TRANCHE_PRICE(1, "Prix tranche"), 
	UNIT_PRICE(2, "Prix unitaire"), 
	PRORATA_PRICE(3, "Prix au prorata");
	
	private final int id;
	private final String name;
	
	PriceType(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PriceType get(int id){
		
		PriceType value = PriceType.NONE;
		for (PriceType item : PriceType.values()) {
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
