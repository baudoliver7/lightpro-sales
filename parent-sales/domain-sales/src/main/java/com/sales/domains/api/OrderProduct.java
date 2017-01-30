package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface OrderProduct extends Recordable<UUID, OrderProduct> {
	int quantity() throws IOException;
	double unitPrice() throws IOException;
	double unitPriceApplied() throws IOException;
	double reductionAmount() throws IOException;
	double totalAmountHt() throws IOException;
	double totalTaxAmount() throws IOException;
	double totalAmountTtc() throws IOException;
	Order order() throws IOException;
	Product product() throws IOException;
	OrderProductTaxes taxes() throws IOException;
	String description() throws IOException;
	
	void update(int quantity, double unitPrice, double reductionAmount, String description, Product product) throws IOException;
	void modifyUnitPrice(double amount) throws IOException;
}
