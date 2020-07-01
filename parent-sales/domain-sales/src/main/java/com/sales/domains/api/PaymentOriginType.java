package com.sales.domains.api;

public enum PaymentOriginType {
	
	NONE(0, "Non définie"), 
	PURCHASE_ORDER(1, "Commande"), 
	INVOICE(2, "Facture"),
	PROVISION(3, "Provision");
	
	private final int id;
	private final String name;
	
	PaymentOriginType(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentOriginType get(int id){
		
		PaymentOriginType value = PaymentOriginType.NONE;
		for (PaymentOriginType item : PaymentOriginType.values()) {
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
