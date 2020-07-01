package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.Contacts;
import com.securities.api.Currency;
import com.securities.api.Membership;
import com.securities.api.MesureUnits;
import com.securities.api.Module;
import com.securities.api.NumberValueType;
import com.securities.api.PaymentModes;
import com.securities.api.Taxes;

public interface Sales extends Module {
	
	ProductCategories productCategories();
	Products products();
	MesureUnits mesureUnits() throws IOException;
	Taxes taxes() throws IOException;
	PaymentModes paymentModes() throws IOException;
	PurchaseOrders purchases();
	Invoices invoices();
	Membership membership() throws IOException;
	Customers customers();
	Sellers sellers();
	Contacts contacts() throws IOException;
	Payments payments();
	Provisions provisions();
	Teams teams();	
	ModulePdvs modulePdvs() throws IOException;	
	ModulePdv modulePointDeVenteDirecte();
	RRRSettings RRRSettings() throws IOException;
	
	Remise remiseWithCurrency(double value, NumberValueType valueType) throws IOException;
	Remise remise(double value, NumberValueType valueType, Currency currency) throws IOException;
	
	SalesInterfacage interfacage();
}
