package com.sales.domains.api;

public enum InvoiceNature {
	
	NONE(0, "Non défini", 0),
	DOWN_PAYMENT(1, "Facture d'acompte", 1), 
	FINAL_INVOICE(2, "Facture finale", 1),
	REMISE(3, "Remise", 2),
	RABAIS(4, "Rabais", 2),
	RISTOURNE(5, "Ristourne", 2),
	ESCOMPTE(6, "Escompte", 2),
	ANNULATION_FACTURE(7, "Annulation de facture", 2);
	
	private final int id;
	private final String name;
	private final int typeId;
	
	InvoiceNature(final int id, final String name, int typeId){
		this.id = id;
		this.name = name;
		this.typeId = typeId;
	}
	
	public static InvoiceNature get(int id){
		
		InvoiceNature value = InvoiceNature.NONE;
		for (InvoiceNature item : InvoiceNature.values()) {
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
	
	public InvoiceType type(){
		return InvoiceType.get(typeId);
	}
}
