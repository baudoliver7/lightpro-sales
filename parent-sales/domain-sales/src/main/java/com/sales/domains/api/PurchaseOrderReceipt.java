package com.sales.domains.api;

import java.io.IOException;

public interface PurchaseOrderReceipt extends Payment {
	PurchaseOrder order() throws IOException;
	double receivedAmount() throws IOException;
	double change() throws IOException;
	
	void validate() throws IOException;
}
