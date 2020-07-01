package com.sales.domains.api;

public enum InvoiceStep {
	
	NONE(0, "Non définie"), 
	CREATING(1, "Facture en création"), 
	EMITTED(2, "Facture émise"), 
	SEND_TO_ACCOUNTING(3, "Envoyée à la comptabilité");
	
	private final int id;
	private final String name;
	
	InvoiceStep(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static InvoiceStep get(int id){
		
		InvoiceStep value = InvoiceStep.NONE;
		for (InvoiceStep item : InvoiceStep.values()) {
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
