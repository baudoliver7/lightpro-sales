package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductEdited {
	
	private final UUID id;
	private final String name;
	private final String internalReference;
	private final String barCode;
	private final String description;
	private final UUID mesureUnitId;
	private final UUID categoryId;
	private final String emballage;
	private final double quantity;
	
	public ProductEdited(){
		throw new UnsupportedOperationException("#ProductEdited()");
	}
	
	@JsonCreator
	public ProductEdited( @JsonProperty("id") final UUID id,
						  @JsonProperty("name") final String name,
						  @JsonProperty("internalReference") final String internalReference, 
				    	  @JsonProperty("barCode") final String barCode,
				    	  @JsonProperty("mesureUnitId") final UUID mesureUnitId,
				    	  @JsonProperty("categoryId") final UUID categoryId,
				    	  @JsonProperty("description") final String description,
				    	  @JsonProperty("emballage") final String emballage,
				    	  @JsonProperty("quantity") final double quantity){
		
		this.id = id;
		this.name = name;
		this.internalReference = internalReference;
		this.barCode = barCode;
		this.mesureUnitId = mesureUnitId;
		this.description = description;
		this.emballage = emballage;
		this.quantity = quantity;
		this.categoryId = categoryId;
	}
	
	public UUID id(){
		return id;
	}
	
	public String name(){
		return name;
	}
	
	public String barCode(){
		return barCode;
	}
	
	public UUID mesureUnitId(){
		return mesureUnitId;
	}
	
	public UUID categoryId(){
		return categoryId;
	}
	
	public String description(){
		return description;
	}
	
	public double quantity(){
		return quantity;
	}
	
	public String emballage(){
		return emballage;
	}
	
	public String internalReference(){
		return internalReference;
	}
}
