package com.sales.domains.api;

public enum PurchaseOrderStatus {
	
	NONE(0, "Non d�fini"),
	CREATED(1, "Cr��e"), 
	COMMANDE_CLIENT(3, "Commande client"),
	ENTIRELY_INVOICED(4, "Commande enti�rement factur�e"),
	PAID(5, "Commande r�gl�e"),
	CANCELLED(6, "Commande annul�e");
	
	private final int id;
	private final String name;
	
	PurchaseOrderStatus(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PurchaseOrderStatus get(int id){
		
		PurchaseOrderStatus value = PurchaseOrderStatus.NONE;
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
