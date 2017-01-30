package com.lightpro.sales.cmd;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAmountsCmd {
	private final int quantity;
	private final double reductionAmount;
	private final LocalDate orderDate;
	
	public ProductAmountsCmd(){
		throw new UnsupportedOperationException("#ProductAmountsCmd()");
	}
	
	@JsonCreator
	public ProductAmountsCmd(@JsonProperty("quantity") final int quantity, 
							 @JsonProperty("reductionAmount") final double reductionAmount, 
				    	     @JsonProperty("orderDate") final LocalDate orderDate){
		
		this.quantity = quantity;
		this.orderDate = orderDate;
		this.reductionAmount = reductionAmount;
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public int quantity(){
		return quantity;
	}
	
	public double reductionAmount(){
		return reductionAmount;
	}
}
