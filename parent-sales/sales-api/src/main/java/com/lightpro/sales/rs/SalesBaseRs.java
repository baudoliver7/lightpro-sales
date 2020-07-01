package com.lightpro.sales.rs;

import java.io.IOException;

import com.sales.domains.api.Sales;
import com.sales.domains.impl.SalesDb;
import com.securities.api.BaseRs;
import com.securities.api.Module;
import com.securities.api.ModuleType;

public abstract class SalesBaseRs extends BaseRs {
	
	public SalesBaseRs() {
		super(ModuleType.SALES);
	}

	protected Sales sales() throws IOException {
		return sales(currentModule);
	}
	
	protected Sales sales(Module module) throws IOException {
		return new SalesDb(base, module);
	}
}
