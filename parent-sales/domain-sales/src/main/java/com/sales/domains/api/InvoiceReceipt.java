package com.sales.domains.api;

import java.io.IOException;

public interface InvoiceReceipt extends Payment {
	Invoice invoice() throws IOException;
	Provision makeProvision(double amount) throws IOException;
	Provision provision() throws IOException;
	
	void validate(boolean forcePayment) throws IOException;
}
