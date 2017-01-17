package com.lightpro.sales.rs;

import com.infrastructure.core.BaseRs;
import com.sales.domains.api.Sales;
import com.sales.domains.impl.SalesImpl;

public abstract class SalesBaseRs extends BaseRs {
	protected Sales sales(){
		return new SalesImpl(base);
	}
}
