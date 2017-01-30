package com.sales.domains.api;

public enum InvoiceType {
	NONE(0, "Non défini"),
	DOWN_PAYMENT(1, "Acompte"), 
	FINAL_INVOICE(2, "Facture finale");
	
	private final int id;
	private final String name;
	
	InvoiceType(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static InvoiceType get(int id){
		
		InvoiceType value = InvoiceType.NONE;
		for (InvoiceType item : InvoiceType.values()) {
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
