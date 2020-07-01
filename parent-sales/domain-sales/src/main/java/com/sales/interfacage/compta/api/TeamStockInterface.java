package com.sales.interfacage.compta.api;

import java.io.IOException;
import java.util.UUID;

import com.sales.domains.api.Team;

public interface TeamStockInterface {
	Team team() throws IOException;
	UUID warehouseId() throws IOException;
	UUID deliveryOperationId() throws IOException;
}
