package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;

import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public interface Invoice extends Order {
		
	LocalDate dueDate() throws IOException;
	PurchaseOrder purchaseOrder() throws IOException;
	String origin() throws IOException;
	PaymentConditionStatus paymentCondition() throws IOException;
	InvoiceType type() throws IOException;
	InvoiceNature nature() throws IOException;
	InvoiceStatus status() throws IOException;
	InvoiceStep step() throws IOException;
	double leftAmountToPay() throws IOException;
	double totalAmountPaid() throws IOException;
	double realTotalAmountPaid() throws IOException;
	double totalAmountRembourse() throws IOException;
	double avoirAmount() throws IOException;
	double solde() throws IOException;
	Payments payments() throws IOException;
	InvoiceProducts products() throws IOException;
	Invoice originInvoice() throws IOException; // renseigné pour les avoirs
	ModulePdv modulePdv() throws IOException;
	Contact seller() throws IOException;
	Team team() throws IOException;
	SaleTaxes taxes() throws IOException;
	Invoices avoirs() throws IOException;
	
	InvoiceReceipt cash(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Provision provision, Contact cashier) throws IOException;
	InvoiceRefundReceipt refund(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	
	void update(LocalDate date, PaymentConditionStatus paymentCondition, String origin, String description, String notes, Contact newCustomer, Contact newSeller) throws IOException;
	void validate() throws IOException;
	void getRid() throws IOException;
	void markPaid() throws IOException;
	void changeStep(InvoiceStep step) throws IOException;
}
