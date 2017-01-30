package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentConditionStatus;

public class QuotationEdited {
	
	private final LocalDate orderDate;
	private final LocalDate expirationDate;
	private final PaymentConditionStatus paymentCondition;
	private final String cgv;
	private final String notes;
	private final UUID customerId;
	private final UUID sellerId;
	private final List<OrderProductEdited> products;
	
	public QuotationEdited(){
		throw new UnsupportedOperationException("#QuotationEdited()");
	}
	
	@JsonCreator
	public QuotationEdited( @JsonProperty("orderDate") final LocalDate orderDate, 
						  @JsonProperty("expirationDate") final LocalDate expirationDate, 
						  @JsonProperty("paymentConditionId") final int paymentConditionId,
				    	  @JsonProperty("cgv") final String cgv,
				    	  @JsonProperty("notes") final String notes,
				    	  @JsonProperty("sellerId") final UUID sellerId,
				    	  @JsonProperty("customerId") final UUID customerId,
				    	  @JsonProperty("products") final List<OrderProductEdited> products){
		
		this.orderDate = orderDate;
		this.expirationDate = expirationDate;
		this.cgv = cgv;
		this.notes = notes;
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.products = products;
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
	
	public String cgv(){
		return cgv;
	}
	
	public String notes(){
		return notes;
	}
	
	public UUID sellerId(){
		return sellerId;
	}
	
	public UUID customerId(){
		return customerId;
	}
	
	public List<OrderProductEdited> products(){
		return products;
	}
}
