package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentConditionStatus;

public class InvoiceEdited {
	
	private final UUID purchaseOrderId;
	private final LocalDate orderDate;
	private final PaymentConditionStatus paymentCondition;
	private final String description;
	private final String origin;
	private final String notes;
	private final UUID customerId;
	private final UUID sellerId;
	
	public InvoiceEdited(){
		throw new UnsupportedOperationException("#InvoiceEdited()");
	}
	
	@JsonCreator
	public InvoiceEdited( @JsonProperty("purchaseOrderId") final UUID purchaseOrderId, 
							@JsonProperty("customerId") final UUID customerId,
							@JsonProperty("sellerId") final UUID sellerId,
						  @JsonProperty("orderDate") final Date orderDate, 
						  @JsonProperty("paymentConditionId") final int paymentConditionId,
				    	  @JsonProperty("description") final String description,
				    	  @JsonProperty("origin") final String origin,
				    	  @JsonProperty("notes") final String notes){
		
		this.purchaseOrderId = purchaseOrderId;
		this.customerId = customerId;
		this.sellerId = sellerId;
		this.orderDate = TimeConvert.toLocalDate(orderDate, ZoneId.systemDefault());
		this.description = description;
		this.notes = notes;
		this.paymentCondition = PaymentConditionStatus.get(paymentConditionId);	
		this.origin = origin;
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
	
	public UUID purchaseOrderId(){
		return purchaseOrderId;
	}
	
	public String origin(){
		return origin;
	}
	
	public UUID customerId(){
		return customerId;
	}
	
	public UUID sellerId(){
		return sellerId;
	}
}
