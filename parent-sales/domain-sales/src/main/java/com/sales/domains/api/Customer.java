package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.Contact;

public interface Customer extends Contact {
	Sales module() throws IOException;
	Provisions provisions() throws IOException;
}
