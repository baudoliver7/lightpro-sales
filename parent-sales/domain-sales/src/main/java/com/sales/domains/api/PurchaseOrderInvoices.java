package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.infrastructure.core.Updatable;

public interface PurchaseOrderInvoices extends Queryable<Invoice, UUID>, Updatable<Invoice> {
	
	Invoice generateDownPayment(double amount, boolean withTax) throws IOException;
	Invoice generateDownPayment(int percent, boolean withTax) throws IOException;
	Invoice generateFinalInvoice() throws IOException;
	
	void deleteAll() throws IOException;
}
