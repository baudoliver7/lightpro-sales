package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public interface Payment extends Nonable {
	UUID id();
	PaymentType type() throws IOException;
	PaymentNature nature() throws IOException;
	String object() throws IOException;
	String reference() throws IOException;
	String origin() throws IOException;
	UUID originId() throws IOException;
	PaymentOriginType originType() throws IOException;
	LocalDate paymentDate() throws IOException;
	double paidAmount() throws IOException;
	double providedAmount() throws IOException;
	double affectedPaidAmount() throws IOException;
	boolean paidByProvision() throws IOException;
	PaymentMode mode() throws IOException;
	String transactionReference() throws IOException;
	PaymentStep step() throws IOException;
	ModulePdv modulePdv() throws IOException;
	Customer customer() throws IOException;
	Sales module() throws IOException;
	PaymentStatus status() throws IOException;	
	Contact cashier() throws IOException;
	
	void changeStep(PaymentStep step) throws IOException;
	
	void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference) throws IOException;
}
