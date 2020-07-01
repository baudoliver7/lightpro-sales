package com.sales.interfacage.compta.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.Team;
import com.securities.api.ModuleInterface;

public interface StocksInterface extends ModuleInterface {
	boolean available();
	void linkTeamToStock(final Team team, final UUID warehouseId, final UUID livraisonOptId) throws IOException;
	void prepareDelivery(PurchaseOrder order) throws IOException;
	void deliver(PurchaseOrder order) throws IOException;
	List<TeamStockInterface> teamStockInterfaces() throws IOException;
}
