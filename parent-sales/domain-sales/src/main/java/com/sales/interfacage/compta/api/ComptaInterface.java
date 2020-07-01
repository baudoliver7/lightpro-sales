package com.sales.interfacage.compta.api;

import java.io.IOException;

import com.sales.domains.api.Invoice;
import com.sales.domains.api.Payment;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Provision;
import com.securities.api.ModuleInterface;

public interface ComptaInterface extends ModuleInterface {
	
	void send(Invoice invoice, boolean silentMode) throws IOException;
	void send(Payment payment, boolean silentMode) throws IOException;
	void send(Provision provision, boolean silentMode) throws IOException;
	void removeProductCategoryInterface(ProductCategory productCategory) throws IOException;
}
