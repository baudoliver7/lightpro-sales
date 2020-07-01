package com.sales.domains.api;

public enum InvoiceType {
	
	NONE(0, "Non défini"),
	FACTURE_DOIT(1, "Facture Doit"), 
	FACTURE_AVOIR(2, "Facture Avoir");
	
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
