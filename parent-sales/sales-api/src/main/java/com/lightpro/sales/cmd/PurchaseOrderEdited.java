package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentConditionStatus;

public class PurchaseOrderEdited {
	
	private final LocalDate orderDate;
	private final LocalDate expirationDate;
	private final PaymentConditionStatus paymentCondition;
	private final String cgv;
	private final String description;
	private final String notes;
	private final UUID customerId;
	private final UUID sellerId;
	private final int livraisonDelayInDays;
	
	public PurchaseOrderEdited(){
		throw new UnsupportedOperationException("#PurchaseOrderEdited()");
	}
	
	@JsonCreator
	public PurchaseOrderEdited( @JsonProperty("orderDate") final LocalDate orderDate, 
						  @JsonProperty("expirationDate") final LocalDate expirationDate, 
						  @JsonProperty("paymentConditionId") final int paymentConditionId,						  
						  @JsonProperty("livraisonDelayInDays") final int livraisonDelayInDays,
				    	  @JsonProperty("cgv") final String cgv,
				    	  @JsonProperty("notes") final String notes,
				    	  @JsonProperty("description") final String description,
				    	  @JsonProperty("sellerId") final UUID sellerId,
				    	  @JsonProperty("customerId") final UUID customerId){
		
		this.orderDate = orderDate;
		this.expirationDate = expirationDate;
		this.livraisonDelayInDays = livraisonDelayInDays;
		this.cgv = cgv;
		this.description = description;
		this.notes = notes;
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.paymentCondition = PaymentConditionStatus.get(paymentConditionId);				
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public LocalDate expirationDate(){
		return expirationDate;
	}
	
	public PaymentConditionStatus paymentCondition(){
		return paymentCondition;
	}
	
	public int livraisonDelayInDays(){
		return livraisonDelayInDays;
	}
	
	public String cgv(){
		return cgv;
	}
	
	public String notes(){
		return notes;
	}
	
	public String description(){
		return description;
	}
	
	public UUID sellerId(){
		return sellerId;
	}
	
	public UUID customerId(){
		return customerId;
	}
}
