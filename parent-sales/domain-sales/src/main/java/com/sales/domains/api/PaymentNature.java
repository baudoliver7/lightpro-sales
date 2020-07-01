package com.sales.domains.api;

public enum PaymentNature {
	
	NONE(0, "Non définie"), 
	RECU(1, "Reçu"), 
	TICKET_CAISSE(2, "Ticket de caisse");
	
	private final int id;
	private final String name;
	
	PaymentNature(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentNature get(int id){
		
		PaymentNature value = PaymentNature.NONE;
		for (PaymentNature item : PaymentNature.values()) {
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
