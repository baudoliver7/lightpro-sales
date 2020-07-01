package com.sales.domains.impl;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.sales.interfacage.compta.api.TeamStockInterface;
import com.sales.interfacage.compta.api.TeamStockInterfaceMetadata;

public final class TeamStockInterfaceDb implements TeamStockInterface {

	private final transient Base base;
	private final transient UUID id;
	private final transient TeamStockInterfaceMetadata dm;
	private final transient DomainStore ds;
	private final Sales module;
	
	public TeamStockInterfaceDb(final Base base, final UUID id, final Sales module){
		this.id = id;
		this.base = base;
		this.dm = TeamStockInterfaceMetadata.create();
		this.ds = base.domainsStore(dm).createDs(id);	
		this.module = module;
	}
	
	@Override
	public Team team() throws IOException {
		return new TeamDb(base, id, module);
	}

	@Override
	public UUID warehouseId() throws IOException {
		return ds.get(dm.warehouseIdKey());
	}

	@Override
	public UUID deliveryOperationId() throws IOException {
		return ds.get(dm.livraisonOpTypeIdKey());
	}
}
