package com.sales.domains.api;

public enum PaymentStep {
	
	NONE(0, "Non d�finie"), 
	CREATING(1, "Paiement en cr�ation"), 
	EMITTED(2, "Paiement effectu�"), 
	SEND_TO_ACCOUNTING(3, "Envoy� � la comptabilit�");
	
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
