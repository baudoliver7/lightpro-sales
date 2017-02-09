package com.lightpro.sales.rs;

import java.io.IOException;

import com.sales.domains.api.Sales;
import com.sales.domains.impl.SalesImpl;
import com.securities.api.BaseRs;
import com.securities.api.Module;
import com.securities.api.ModuleType;

public abstract class SalesBaseRs extends BaseRs {
	protected Sales sales() throws IOException {
		Module module = currentCompany().modules().get(ModuleType.SALES);
		return new SalesImpl(base(), module.id());
	}
}
