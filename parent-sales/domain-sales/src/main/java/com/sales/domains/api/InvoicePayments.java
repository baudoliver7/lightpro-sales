package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoicePayments {
	List<Payment> all() throws IOException;
	Payment get(UUID id) throws IOException;
	Payment build(UUID id) throws IOException;
	
	Payment add(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode) throws IOException;
	void delete(Payment item) throws IOException;
}
