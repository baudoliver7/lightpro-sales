package com.sales.domains.api;

public enum PaymentMode {
	
	NONE(0, "Non défini"),
	BANK(1, "Banque"), 
	CASH_FLOW(2, "Liquidités");
	
	private final int id;
	private final String name;
	
	PaymentMode(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentMode get(int id){
		
		PaymentMode value = PaymentMode.NONE;
		for (PaymentMode item : PaymentMode.values()) {
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
