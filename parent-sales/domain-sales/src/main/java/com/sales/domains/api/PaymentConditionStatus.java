package com.sales.domains.api;

public enum PaymentConditionStatus {
	DIRECT_PAYMENT(0, "Paiement immédiat", 0),
	DAYS_10NET(1, "10 jours nets", 10), 
	DAYS_15NET(2, "15 jours nets", 15), 
	DAYS_30NET(3, "30 jours nets", 30);
	
	private final int id;
	private final String name;
	private final int numberOfDays;
	
	PaymentConditionStatus(final int id, final String name, final int numberOfDays){
		this.id = id;
		this.name = name;
		this.numberOfDays = numberOfDays;
	}
	
	public static PaymentConditionStatus get(int id){
		
		PaymentConditionStatus value = PaymentConditionStatus.DIRECT_PAYMENT;
		for (PaymentConditionStatus item : PaymentConditionStatus.values()) {
			if(item.id() == id)
				value = item;
		}
		
		return value;
	}

	public int id(){
		return id;
	}
	
	public int numberOfDays(){
		return numberOfDays;
	}
	
	public String toString(){
		return name;
	}
}
