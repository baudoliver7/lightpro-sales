package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.stocks.domains.api.OperationType;

public final class OperationTypeVm {
	
	public final UUID id;
	public final String name;
	public final UUID warehouseId;
	public final String warehouse;
	public final UUID defaultSourceLocationId;
	public final String defaultSourceLocation;
	public final UUID defaultDestinationLocationId;
	public final String defaultDestinationLocation;
	public final int categoryId;
	public final String category;
	public final UUID sequenceId;
	public final String sequence;
	public final long numberOfUnfinishedOperations;
	public final long numberOfTodoOperations;
	public final UUID preparationOpTypeId;
	public final String preparationOpType;
	public final UUID returnOpTypeId;
	public final String returnOpType;
	
	public OperationTypeVm(){
		throw new UnsupportedOperationException("#OperationTypeVm()");
	}
	
	public OperationTypeVm(final OperationType origin) {
		try {
			this.id = origin.id();
			this.name = origin.name();
			this.warehouseId = origin.warehouse().id();
	        this.warehouse = origin.warehouse().name();
	        this.defaultSourceLocationId = origin.defaultSourceLocation().id();
	        this.defaultSourceLocation = origin.defaultSourceLocation().name();
	        this.defaultDestinationLocationId = origin.defaultDestinationLocation().id();
	        this.defaultDestinationLocation = origin.defaultDestinationLocation().name();
			this.category = origin.category().name();
	        this.categoryId = origin.category().id();
	        this.sequenceId = origin.sequence().id();
	        this.sequence = origin.sequence().name();
	        this.numberOfUnfinishedOperations = origin.unfinishedOperations().count();
	        this.numberOfTodoOperations = origin.todoOperations().count();
	        this.preparationOpTypeId = origin.preparationOpType().id();
	        this.preparationOpType = origin.preparationOpType().name();
	        this.returnOpTypeId = origin.returnOpType().id();
	        this.returnOpType = origin.returnOpType().name();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
