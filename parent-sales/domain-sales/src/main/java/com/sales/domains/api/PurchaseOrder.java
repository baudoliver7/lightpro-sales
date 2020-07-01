package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;

import com.securities.api.Contact;
import com.securities.api.PaymentMode;


public interface PurchaseOrder extends Order {
	
	PurchaseOrderStatus status() throws IOException;
	String cgv() throws IOException;
	Contact seller() throws IOException;
	Team team() throws IOException;
	LocalDate expirationDate() throws IOException;
	LocalDate soldDate() throws IOException;
	int livraisonDelayInDays() throws IOException;
	LocalDate livraisonDate() throws IOException;
	PaymentConditionStatus paymentCondition() throws IOException;	
	Invoices invoices() throws IOException;	
	ModulePdv modulePdv() throws IOException;
	double amountInvoiced() throws IOException;
	double leftAmountToInvoice() throws IOException;
	double paidAmount() throws IOException;
	double returnAmount() throws IOException;
	double realPaidAmount() throws IOException;
	OrderProducts products() throws IOException;
	PurchaseOrderReceipt cashReceipt() throws IOException;
	
	void update(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String description, String notes, Contact customer, Contact seller, int livraisonDelayInDays) throws IOException;	
	void markSold(LocalDate date, boolean isDeliverDirectly) throws IOException;
	void markPaid() throws IOException;
	void cancel() throws IOException;
	void reOpen() throws IOException;
	void markEntirelyInvoiced() throws IOException;
	
	void changeCustomer(Contact contact) throws IOException;
	void changeSeller(Contact seller) throws IOException;
	
	PurchaseOrderReceipt cash(LocalDate paymentDate, String object, double receivedAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
}
