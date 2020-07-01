package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public interface Payments  extends AdvancedQueryable<Payment, UUID>, Updatable<Payment> {
	
	PurchaseOrderReceipt cashPurchaseOrder(LocalDate paymentDate, String object, double receivedAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	InvoiceReceipt cashInvoice(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	InvoiceReceipt cashInvoice(LocalDate paymentDate, String object, double paidAmount, Provision provision, Contact cashier) throws IOException;
	InvoiceRefundReceipt refundInvoice(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;	
	ProvisionRefundReceipt refundProvision(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException;
	
	Payments of(Invoice invoice) throws IOException;
	Payments of(Provision provision) throws IOException;
	Payments of(PurchaseOrder purchaseOrder) throws IOException;
	Payments of(PaymentType type) throws IOException;
	Payments of(PaymentStatus status) throws IOException;
	Payments of(ModulePdv modulePdv) throws IOException;
	Payments of(Contact customer) throws IOException;
	Payments ofVenteDirecte() throws IOException;
	
	PurchaseOrderReceipt getPurchaseOrderReceipt(UUID id) throws IOException;
	InvoiceReceipt getInvoiceReceipt(UUID id) throws IOException;
	InvoiceRefundReceipt getInvoiceRefundReceipt(UUID id) throws IOException;
	ProvisionRefundReceipt getProvisionRefundReceipt(UUID id) throws IOException;
}
