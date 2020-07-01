package com.sales.domains.api;

public enum PaymentStep {
	
	NONE(0, "Non définie"), 
	CREATING(1, "Paiement en création"), 
	EMITTED(2, "Paiement effectué"), 
	SEND_TO_ACCOUNTING(3, "Envoyé à la comptabilité");
	
	private final int id;
	private final String name;
	
	PaymentStep(final int id, final String name){
		this.id = id;
		this.name = name;
	}
	
	public static PaymentStep get(int id){
		
		PaymentStep value = PaymentStep.NONE;
		for (PaymentStep item : PaymentStep.values()) {
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
