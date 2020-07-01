package com.sales.domains.api;

import java.io.IOException;

public interface ProvisionRefundReceipt extends Payment {
	Provision provision() throws IOException;	
	void validate() throws IOException;
}
