package com.sales.domains.api;

public enum InvoiceStatus {
	
	NONE(0, "Non défini"), 
	OPENED(1, "Ouverte"), 
	PAID(2, "Payée");
	
	private final int id;
	private final String name;
	
	InvoiceStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static InvoiceStatus get(int id){
		
		InvoiceStatus value = InvoiceStatus.NONE;
		for (InvoiceStatus item : InvoiceStatus.values()) {
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
