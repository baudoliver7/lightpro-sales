package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.infrastructure.datasource.Base;
import com.sales.domains.api.Sales;
import com.sales.domains.api.SalesInterfacage;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.sales.interfacage.compta.api.StocksInterface;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.api.Modules;

public final class SalesInterfacageDb implements SalesInterfacage {

	private final transient Base base;
	private final transient Sales sales;	
	
	public SalesInterfacageDb(final Base base, final Sales sales){
		this.base = base;
		this.sales = sales;
	}
	
	@Override
	public ComptaInterface comptaInterface() {
		return new ComptaInterfaceDb(base, sales);
	}

	@Override
	public StocksInterface stocksInterface() {
		return new StocksInterfaceDb(base, sales);
	}

	@Override
	public List<Module> modulesAvailable() throws IOException {
		List<Module> modules = new ArrayList<Module>();
		
		Modules modulesInstalled = sales.company().modulesInstalled();
		
		if(modulesInstalled.contains(ModuleType.STOCKS))
			modules.add(modulesInstalled.get(ModuleType.STOCKS));
		
		if(modulesInstalled.contains(ModuleType.COMPTA))
			modules.add(modulesInstalled.get(ModuleType.COMPTA));
		
		return modules;
	}
}
