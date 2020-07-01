package com.lightpro.sales.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sales.domains.api.ProductCategoryType;

public class ProductCategoryEdited {
	
	private final UUID id;
	private final int typeId;
	private final String name;
	private final String description;
	
	public ProductCategoryEdited(){
		throw new UnsupportedOperationException("#ProductCategoryEdited()");
	}
	
	@JsonCreator
	public ProductCategoryEdited( @JsonProperty("id") final UUID id,
						  @JsonProperty("typeId") final int typeId,
						  @JsonProperty("name") final String name, 
				    	  @JsonProperty("description") final String description){
		
		this.id = id;
		this.typeId = typeId;
		this.name = name;
		this.description = description;
	}
	
	public UUID id(){
		return id;
	}
	
	public ProductCategoryType type(){
		return ProductCategoryType.get(typeId);
	}
	
	public String name(){
		return name;
	}
	
	public String description(){
		return description;
	}
}
