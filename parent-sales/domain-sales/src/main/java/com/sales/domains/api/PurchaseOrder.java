package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;

import com.securities.api.User;

public interface PurchaseOrder extends Order {
	PurchaseOrderStatus status() throws IOException;
	String cgv() throws IOException;
	User seller() throws IOException;
	LocalDate expirationDate() throws IOException;
	PaymentConditionStatus paymentCondition() throws IOException;	
	PurchaseOrderInvoices invoices();	
	
	void update(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String notes, Customer customer, User seller) throws IOException;	
	void markSend() throws IOException;
	void markSold() throws IOException;
	void cancel() throws IOException;
	void reOpen() throws IOException;
	void markEntirelyInvoiced() throws IOException;
}
