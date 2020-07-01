package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.securities.api.Contact;

public interface PurchaseOrders extends AdvancedQueryable<PurchaseOrder, UUID> {	
	
	PurchaseOrder add(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String description, String notes, Contact customer, Contact seller, int livraisonDelayInDays) throws IOException;
	Sales module() throws IOException;
	
	PurchaseOrders of(ModulePdv modulePdv) throws IOException;
	PurchaseOrders ofVenteDirecte() throws IOException;
	PurchaseOrders of(PurchaseOrderStatus status) throws IOException;
}
