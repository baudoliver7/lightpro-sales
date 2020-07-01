package com.sales.domains.api;

public enum PaymentType {
	
	NONE(0, "Non définie"), 
	ENCAISSEMENT(1, "Encaissement"), 
	REMBOURSEMENT(2, "Remboursement");
	
	private final int id;
	private final String name;
	
	PaymentType(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentType get(int id){
		
		PaymentType value = PaymentType.NONE;
		for (PaymentType item : PaymentType.values()) {
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
