package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface Payment extends Recordable<UUID, Payment> {
	
	String object() throws IOException;
	String reference() throws IOException;
	LocalDate paymentDate() throws IOException;
	double paidAmount() throws IOException;
	PaymentMode mode() throws IOException;
	Invoice invoice() throws IOException;
	
	void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode) throws IOException;
}
