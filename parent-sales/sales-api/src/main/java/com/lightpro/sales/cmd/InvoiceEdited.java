package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentConditionStatus;

public class InvoiceEdited {
	
	private final LocalDate orderDate;
	private final PaymentConditionStatus paymentCondition;
	private final String description;
	private final String notes;
	private final List<OrderProductEdited> products;
	
	public InvoiceEdited(){
		throw new UnsupportedOperationException("#QuotationEdited()");
	}
	
	@JsonCreator
	public InvoiceEdited( @JsonProperty("orderDate") final LocalDate orderDate, 
						  @JsonProperty("paymentConditionId") final int paymentConditionId,
				    	  @JsonProperty("description") final String description,
				    	  @JsonProperty("notes") final String notes,
				    	  @JsonProperty("products") final List<OrderProductEdited> products){
		
		this.orderDate = orderDate;
		this.description = description;
		this.notes = notes;
		this.products = products;
		this.paymentCondition = PaymentConditionStatus.get(paymentConditionId);				
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public PaymentConditionStatus paymentCondition(){
		return paymentCondition;
	}
	
	public String description(){
		return description;
	}
	
	public String notes(){
		return notes;
	}
	
	public List<OrderProductEdited> products(){
		return products;
	}
}
