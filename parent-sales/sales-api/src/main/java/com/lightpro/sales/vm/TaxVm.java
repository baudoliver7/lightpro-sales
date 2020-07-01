package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.securities.api.Tax;

public final class TaxVm {
	
	public final String type;
	public final int typeId;
	public final UUID id;
	public final String name;
	public final String shortName;
	public final double value;
	public final int valueTypeId;
	public final String valueSummary;
	public final String resumeName;
	
	public TaxVm(){
		throw new UnsupportedOperationException("#TaxVm()");
	}
		
	public TaxVm(Tax origin){
		try {
			this.id = origin.id();
			this.name = origin.name();
			this.shortName = origin.shortName();
	        this.value = origin.value();
	        this.valueTypeId = origin.valueType().id();
	        this.type = origin.type().toString();
	        this.typeId = origin.type().id();
	        this.valueSummary = origin.valueToString();
	        this.resumeName = origin.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
}
