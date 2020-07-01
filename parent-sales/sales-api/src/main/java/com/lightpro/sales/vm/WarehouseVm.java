package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.stocks.domains.api.LocationType;
import com.stocks.domains.api.Warehouse;

public final class WarehouseVm {
	
	public final UUID id;
	public final String name;
	public final String shortName;
	public final long numberOfLocations;
	public final long numberOfOperationTypes;
	
	public WarehouseVm(){
		throw new UnsupportedOperationException("#WarehouseVm()");
	}
	
	public WarehouseVm(final Warehouse origin) {
		try {
			this.id = origin.id();
			this.name = origin.name();
			this.shortName = origin.shortName();
	        this.numberOfLocations = origin.locations().of(LocationType.INTERNAL).count();
	        this.numberOfOperationTypes = origin.operationTypes().count();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }	
}
