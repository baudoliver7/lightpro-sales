package com.sales.reporting.datasets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.PojoDataSet;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.Sales;

public final class QuotationDataSet extends PojoDataSet<PurchaseOrder> {

	@Override
	public void open(Object appContext, Map<String, Object> dataSetParamValues) throws IOException {
		try {
			
			@SuppressWarnings("unchecked")
			Map<String, Object> mur = (Map<String, Object>)appContext;
			
			final Sales sales = (Sales)mur.get("moduleContext");
			
			UUID quotationId = UUID.fromString(dataSetParamValues.get("QuotationId").toString());
			PurchaseOrder purchaseOrder = sales.purchases().get(quotationId);			
			
			itr = Arrays.asList(purchaseOrder).iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

}
