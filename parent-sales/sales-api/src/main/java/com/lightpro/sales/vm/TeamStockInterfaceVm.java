package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.sales.interfacage.compta.api.TeamStockInterface;

public final class TeamStockInterfaceVm {
	
	public final UUID teamId;
	public final String team;
	public final UUID warehouseId;
	public final UUID deliveryOperationId;
	
	public TeamStockInterfaceVm(){
		throw new UnsupportedOperationException("#TeamStockInterfaceVm()");
	}
	
	public TeamStockInterfaceVm(final TeamStockInterface origin) {
		try {
			this.teamId = origin.team().id();
			this.team = origin.team().name();
	        this.warehouseId = origin.warehouseId();
	        this.deliveryOperationId = origin.deliveryOperationId();	 
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
