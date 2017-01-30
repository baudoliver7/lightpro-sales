package com.sales.domains.api;

import com.securities.api.Company;
import com.securities.api.Membership;
import com.securities.api.MesureUnits;
import com.securities.api.Taxes;

public interface Sales {
	Company company();
	Products products();
	MesureUnits mesureUnits();
	Taxes taxes();
	PurchaseOrders quotations();
	PurchaseOrders purchases();
	Invoices invoices();
	Membership membership();
	Customers customers();
	Payments payments();
}
