package com.sales.domains.api;

import java.io.IOException;

public interface InvoiceRefundReceipt extends Payment {
	Invoice invoice() throws IOException;
	void validate() throws IOException;
}
