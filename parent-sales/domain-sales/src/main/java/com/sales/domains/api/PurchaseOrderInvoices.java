package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Queryable;
import com.infrastructure.core.Updatable;

public interface PurchaseOrderInvoices extends Queryable<Invoice, UUID>, Updatable<Invoice> {
	
	Invoice generateDownPayment(LocalDate invoiceDate, double amount, boolean withTax) throws IOException;
	Invoice generateDownPayment(LocalDate invoiceDate, int percent, boolean withTax) throws IOException;
	Invoice generateFinalInvoice(LocalDate invoiceDate) throws IOException;
	
	void deleteAll() throws IOException;
}
