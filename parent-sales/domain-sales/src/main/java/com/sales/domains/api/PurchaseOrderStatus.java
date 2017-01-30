package com.sales.domains.api;

public enum PurchaseOrderStatus {
	
	ANNULE(0, "Annulé"),
	DRAFT(1, "Brouillon"), 
	DEVIS_ENVOYE(2, "Devis envoyé"), 
	COMMANDE_CLIENT(3, "Commande client"),
	ENTIRELY_INVOICED(4, "Commande entièrement facturée"),
	TERMINE(5, "Terminé");
	
	private final int id;
	private final String name;
	
	PurchaseOrderStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PurchaseOrderStatus get(int id){
		
		PurchaseOrderStatus value = PurchaseOrderStatus.ANNULE;
		for (PurchaseOrderStatus item : PurchaseOrderStatus.values()) {
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
