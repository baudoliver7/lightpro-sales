package com.lightpro.sales.cmd;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAmountsCmd {
	private final double quantity;
	private final LocalDate orderDate;
	private final double unitPrice;
	
	private final List<TaxEdited> taxes;
	
	public ProductAmountsCmd(){
		throw new UnsupportedOperationException("#ProductAmountsCmd()");
	}
	
	@JsonCreator
	public ProductAmountsCmd(@JsonProperty("quantity") final double quantity, 
				    	     @JsonProperty("orderDate") final LocalDate orderDate,
				    	     @JsonProperty("unitPrice") final double unitPrice,
				    	     @JsonProperty("taxes") final List<TaxEdited> taxes){
		
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.orderDate = orderDate;
		this.taxes = taxes;
	}
	
	public LocalDate orderDate(){
		return orderDate;
	}
	
	public double quantity(){
		return quantity;
	}
	
	public List<TaxEdited> taxes(){
		return taxes;
	}
	
	public double unitPrice(){
		return unitPrice;
	}
}
