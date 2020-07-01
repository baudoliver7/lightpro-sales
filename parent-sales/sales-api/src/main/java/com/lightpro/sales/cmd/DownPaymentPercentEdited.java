package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import com.common.utilities.convert.TimeConvert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.PaymentConditionStatus;

public class DownPaymentPercentEdited {
	
	private final UUID purchaseOrderId;
	private final LocalDate orderDate;
	private final PaymentConditionStatus paymentCondition;
	private final String description;
	private final String origin;
	private final String notes;
	private final UUID customerId;
	private final UUID sellerId;
	private final double base;
	private final int percent;
	
	public DownPaymentPercentEdited(){
		throw new UnsupportedOperationException("#DownPaymentPercentEdited()");
	}
	
	@JsonCreator
	public DownPaymentPercentEdited( @JsonProperty("purchaseOrderId") final UUID purchaseOrderId, 
							@JsonProperty("customerId") final UUID customerId,
							@JsonProperty("sellerId") final UUID sellerId,
						  @JsonProperty("orderDate") final Date orderDate, 
						  @JsonProperty("paymentConditionId") final int paymentConditionId,
				    	  @JsonProperty("description") final String description,
				    	  @JsonProperty("origin") final String origin,
				    	  @JsonProperty("notes") final String notes,
				    	  @JsonProperty("base") final double base,
						  @JsonProperty("percent") final int percent){
		
		this.purchaseOrderId = purchaseOrderId;
		this.customerId = customerId;
		this.sellerId = sellerId;
		this.orderDate = TimeConvert.toLocalDate(orderDate, ZoneId.systemDefault());
		this.description = description;
		this.notes = notes;
		this.paymentCondition = PaymentConditionStatus.get(paymentConditionId);	
		this.percent = percent;
		this.origin = origin;
		this.base = base;
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
	
	public int percent(){
		return percent;
	}
	
	public String origin(){
		return origin;
	}
	
	public double base(){
		return base;
	}
	
	public UUID customerId(){
		return customerId;
	}
	
	public UUID sellerId(){
		return sellerId;
	}
}
