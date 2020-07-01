package com.sales.domains.api;

public enum PaymentStatus {
	
	NONE(0, "Non définie"), 
	DRAFT(1, "Brouillon"), 
	VALIDATED(2, "Validé");
	
	private final int id;
	private final String name;
	
	PaymentStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentStatus get(int id){
		
		PaymentStatus value = PaymentStatus.NONE;
		for (PaymentStatus item : PaymentStatus.values()) {
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
