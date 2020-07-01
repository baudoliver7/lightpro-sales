package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderReceipt;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class PurchaseOrderNone extends GuidKeyEntityNone<PurchaseOrder> implements PurchaseOrder {
	
	@Override
	public LocalDate orderDate() throws IOException {
		return null;
	}

	@Override
	public String reference() throws IOException {
		return null;
	}

	@Override
	public String cgv() throws IOException {
		return null;
	}

	@Override
	public String notes() throws IOException {
		return null;
	}

	@Override
	public Contact customer() throws IOException {
		return null;
	}

	@Override
	public Contact seller() throws IOException {
		return null;
	}

	@Override
	public OrderProducts products() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public PurchaseOrderStatus status() throws IOException {
		return PurchaseOrderStatus.NONE;
	}

	@Override
	public void updateAmounts() throws IOException {
		
	}

	@Override
	public void markSold(LocalDate date, boolean isDeliverDirectly) throws IOException {
		
	}

	@Override
	public void cancel() throws IOException {

	}

	@Override
	public void reOpen() throws IOException {
		
	}

	@Override
	public LocalDate expirationDate() throws IOException {
		return null;
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		return PaymentConditionStatus.NONE;
	}

	@Override
	public void update(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String description, String notes, Contact customer, Contact seller, int livraisonDelayInDays) throws IOException {
		
	}

	@Override
	public Invoices invoices() {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void markEntirelyInvoiced() throws IOException {
		
	}
	
	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public LocalDate soldDate() throws IOException {
		return null;
	}

	@Override
	public int livraisonDelayInDays() throws IOException {
		return 0;
	}

	@Override
	public LocalDate livraisonDate() throws IOException {
		return null;
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		return new ModulePdvNone();
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public double amountInvoiced() throws IOException {
		return 0;
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		return new OrderSaleAmount(0, 0, 0, 0, 0);
	}

	@Override
	public String title() throws IOException {
		return null;
	}

	@Override
	public double leftAmountToInvoice() throws IOException {
		return 0;
	}

	@Override
	public PurchaseOrderReceipt cashReceipt() throws IOException {
		Payment payment = new PaymentNone();
		return (PurchaseOrderReceipt)(payment);
	}

	@Override
	public PurchaseOrderReceipt cash(LocalDate paymentDate, String object, double receivedAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}

	@Override
	public void markPaid() throws IOException {
		
	}

	@Override
	public void changeCustomer(Contact contact) throws IOException {
		
	}

	@Override
	public void changeSeller(Contact seller) throws IOException {
		
	}

	@Override
	public double paidAmount() throws IOException {
		return 0;
	}

	@Override
	public double returnAmount() throws IOException {
		return 0;
	}

	@Override
	public double realPaidAmount() throws IOException {
		return 0;
	}

	@Override
	public Team team() throws IOException {
		return new TeamNone();
	}
}
