package com.lightpro.sales.cmd;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderProductEdited {
	
	private final double quantity;
	private final double unitPrice;
	private final String name;
	private final String description;
	private final UUID productId;
	private final List<TaxEdited> taxes;
	
	public OrderProductEdited(){
		throw new UnsupportedOperationException("#OrderProductEdited()");
	}
	
	@JsonCreator
	public OrderProductEdited(@JsonProperty("quantity") final double quantity, 
							  @JsonProperty("unitPrice") final double unitPrice,
							  @JsonProperty("name") final String name,
							  @JsonProperty("description") final String description,
					    	  @JsonProperty("productId") final UUID productId,
					    	  @JsonProperty("taxes") final List<TaxEdited> taxes){
		
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.name = name;
		this.productId = productId;
		this.description = description;
		this.taxes = taxes;
	}
	
	public double quantity(){
		return quantity;
	}
	
	public double unitPrice(){
		return unitPrice;
	}
	
	public String description(){
		return description;
	}
	
	public String name(){
		return name;
	}
	
	public UUID productId(){
		return productId;
	}
	
	@JsonGetter
	public List<TaxEdited> taxes() throws IOException {
		return taxes;
	}
}
