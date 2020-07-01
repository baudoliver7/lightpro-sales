package com.sales.domains.api;

import com.sales.interfacage.compta.api.ComptaInterface;
import com.sales.interfacage.compta.api.StocksInterface;
import com.securities.api.ModuleInterfacage;

public interface SalesInterfacage extends ModuleInterfacage {
	ComptaInterface comptaInterface();
	StocksInterface stocksInterface();
}
