package com.sales.domains.api;

public enum PricingMode {
	NONE(0, "Non défini"),
	FIX(1, "Prix fixe"), 
	TRANCHE_SIMPLE(2, "Tranche simple"), 
	TRANCHE_MONTH_DAYS(3, "Tranche jours mois");
	
	private final int id;
	private final String name;
	
	PricingMode(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PricingMode get(int id){
		
		PricingMode value = PricingMode.NONE;
		for (PricingMode item : PricingMode.values()) {
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
