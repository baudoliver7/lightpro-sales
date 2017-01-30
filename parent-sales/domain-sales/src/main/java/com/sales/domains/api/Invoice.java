package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;

public interface Invoice extends Order {
	LocalDate dueDate() throws IOException;
	PurchaseOrder purchaseOrder() throws IOException;
	String description() throws IOException;	
	String origin() throws IOException;
	PaymentConditionStatus paymentCondition() throws IOException;
	InvoiceType type() throws IOException;
	InvoiceStatus status() throws IOException;	
	double leftAmountToPay() throws IOException;
	double totalAmountPaid() throws IOException;
	InvoicePayments payments() throws IOException;
	
	void update(LocalDate date, PaymentConditionStatus paymentCondition, String description, String notes) throws IOException;
	void markOpened() throws IOException;
	void markPaid() throws IOException;
}
