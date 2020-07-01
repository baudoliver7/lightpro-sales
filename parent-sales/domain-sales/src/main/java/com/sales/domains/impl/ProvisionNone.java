package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionRefundReceipt;
import com.sales.domains.api.ProvisionStatus;
import com.sales.domains.api.ProvisionStep;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class ProvisionNone extends GuidKeyEntityNone<Provision> implements Provision {

	@Override
	public String reference() throws IOException {
		return null;
	}

	@Override
	public double amount() throws IOException {
		return 0;
	}

	@Override
	public ProvisionStatus status() throws IOException {
		return ProvisionStatus.NONE;
	}

	@Override
	public Customer customer() throws IOException {
		return new CustomerNone();
	}

	@Override
	public void validate() throws IOException {
		
	}

	@Override
	public void update(double amount) throws IOException {

	}

	@Override
	public double availableAmount() throws IOException {
		return 0;
	}

	@Override
	public double amountConsommed() throws IOException {
		return 0;
	}

	@Override
	public InvoiceReceipt originPayment() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Payments payments() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public Payments receipts() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public Payments refunds() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public ProvisionRefundReceipt refund(LocalDate date, String object, double amount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !"); 
	}

	@Override
	public void checkConsommed() throws IOException {

	}

	@Override
	public LocalDate provisionDate() throws IOException {
		return null;
	}

	@Override
	public String toString(){
		return "Aucune provision";
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Invoice invoice, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public ProvisionStep step() throws IOException {
		return ProvisionStep.NONE;
	}

	@Override
	public void changeStep(ProvisionStep step) throws IOException {
		
	}
}
