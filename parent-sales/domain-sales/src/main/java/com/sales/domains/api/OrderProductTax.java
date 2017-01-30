package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;
import com.securities.api.Tax;

public interface OrderProductTax extends Recordable<UUID, OrderProductTax> {
	double amount() throws IOException;
	OrderProduct orderProduct() throws IOException;
	Tax tax() throws IOException;	
}
