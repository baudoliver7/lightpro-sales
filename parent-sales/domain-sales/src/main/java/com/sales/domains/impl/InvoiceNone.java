package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceNature;
import com.sales.domains.api.InvoiceProducts;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.InvoiceRefundReceipt;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceStep;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.impl.ContactNone;

public final class InvoiceNone extends GuidKeyEntityNone<Invoice> implements Invoice {

	@Override
	public String title() throws IOException {
		return null;
	}

	@Override
	public LocalDate orderDate() throws IOException {
		return null;
	}

	@Override
	public String reference() throws IOException {
		return null;
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		return null;
	}

	@Override
	public String notes() throws IOException {
		return null;
	}

	@Override
	public Customer customer() throws IOException {
		return new CustomerNone();
	}

	@Override
	public void updateAmounts() throws IOException {
		
	}

	@Override
	public LocalDate dueDate() throws IOException {
		return null;
	}

	@Override
	public PurchaseOrder purchaseOrder() throws IOException {
		return new PurchaseOrderNone();
	}

	@Override
	public String origin() throws IOException {
		return null;
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		return PaymentConditionStatus.NONE;
	}

	@Override
	public InvoiceType type() throws IOException {
		return InvoiceType.NONE;
	}

	@Override
	public InvoiceNature nature() throws IOException {
		return InvoiceNature.NONE;
	}

	@Override
	public InvoiceStatus status() throws IOException {
		return InvoiceStatus.NONE;
	}

	@Override
	public InvoiceStep step() throws IOException {
		return InvoiceStep.NONE;
	}

	@Override
	public double leftAmountToPay() throws IOException {
		return 0;
	}

	@Override
	public double totalAmountPaid() throws IOException {
		return 0;
	}

	@Override
	public Payments payments() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public InvoiceProducts products() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public Invoice originInvoice() throws IOException {
		return new InvoiceNone();
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		return new ModulePdvNone();
	}

	@Override
	public Contact seller() throws IOException {
		return new ContactNone();
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void update(LocalDate date, PaymentConditionStatus paymentCondition, String origin, String description,
			String notes, Contact newCustomer, Contact newSeller) throws IOException {

	}

	@Override
	public void validate() throws IOException {

	}

	@Override
	public void markPaid() throws IOException {

	}

	@Override
	public void changeStep(InvoiceStep step) throws IOException {

	}

	@Override
	public void getRid() throws IOException {
	
	}

	@Override
	public Invoices avoirs() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public double avoirAmount() throws IOException {
		return 0;
	}

	@Override
	public double solde() throws IOException {
		return 0;
	}

	@Override
	public double totalAmountRembourse() throws IOException {
		return 0;
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Provision provision, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public InvoiceRefundReceipt refund(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public double realTotalAmountPaid() throws IOException {
		return 0;
	}

	@Override
	public Team team() throws IOException {
		return new TeamNone();
	}
}
