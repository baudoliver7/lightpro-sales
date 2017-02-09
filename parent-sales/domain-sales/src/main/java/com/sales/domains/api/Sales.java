package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.Company;
import com.securities.api.Membership;
import com.securities.api.MesureUnits;
import com.securities.api.Module;
import com.securities.api.Taxes;

public interface Sales extends Module {
	Company company();
	Products products();
	MesureUnits mesureUnits() throws IOException;
	Taxes taxes() throws IOException;
	PurchaseOrders quotations();
	PurchaseOrders purchases();
	PurchaseOrders orders();
	Invoices invoices();
	Membership membership();
	Customers customers() throws IOException;
	Payments payments();
}
