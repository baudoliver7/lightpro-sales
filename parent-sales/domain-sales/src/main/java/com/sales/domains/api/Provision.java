package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public interface Provision extends Nonable {
	UUID id();
	LocalDate provisionDate() throws IOException;
	String reference() throws IOException;
	double amount() throws IOException;
	double availableAmount() throws IOException;
	double amountConsommed() throws IOException;
	ProvisionStatus status() throws IOException;
	ProvisionStep step() throws IOException;
	InvoiceReceipt originPayment() throws IOException;
	Payments payments() throws IOException;
	Payments receipts() throws IOException;
	Payments refunds() throws IOException;
	Customer customer() throws IOException;
	
	void validate() throws IOException;
	void update(double amount) throws IOException;
	
	InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Invoice invoice, Contact cashier) throws IOException;
	ProvisionRefundReceipt refund(LocalDate date, String object, double amount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	
	void checkConsommed() throws IOException;
	void changeStep(ProvisionStep step) throws IOException;
}
