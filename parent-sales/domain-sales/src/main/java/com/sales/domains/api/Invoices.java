package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;
import com.securities.api.Contact;

public interface Invoices extends AdvancedQueryable<Invoice, UUID>, Updatable<Invoice> {
	
	Invoice addFinalInvoice(LocalDate date, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException;
	Invoice addDownPayment(LocalDate date, double amount, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException;
	Invoice addDownPayment(LocalDate date, int percent, double base, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException;
	
	Invoice addRemise(LocalDate date, double amount) throws IOException;
	Invoice addRemise(LocalDate date, int percent) throws IOException;	
	Invoice addRistourne(LocalDate date, double amount) throws IOException;
	Invoice addRistourne(LocalDate date, int percent) throws IOException;	
	Invoice addRabais(LocalDate date) throws IOException;	
	Invoice addEscompte(LocalDate date, int percent) throws IOException;	
	Invoice addCancelInvoice(LocalDate date) throws IOException;
	
	Invoices of(ModulePdv modulePdv) throws IOException;
	Invoices ofVenteDirecte() throws IOException;
	Invoices of(InvoiceStep step) throws IOException;
	Invoices of(InvoiceType type) throws IOException;
	Invoices of(InvoiceStatus status) throws IOException;
	Invoices of(PurchaseOrder purchaseOrder) throws IOException;
	Invoices of(PaymentConditionStatus paymentCondition) throws IOException;
	Invoices ofCustomer(Contact customer) throws IOException;
	Invoices ofSeller(Contact seller) throws IOException;
	Invoices of(Invoice originInvoice) throws IOException;
}
