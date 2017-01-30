package com.lightpro.sales.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.Invoice;

public class InvoiceVm {
	private final transient Invoice origin;
	
	public InvoiceVm(){
		throw new UnsupportedOperationException("#InvoiceVm()");
	}
	
	public InvoiceVm(final Invoice origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public UUID getId(){
		return origin.id();
	}
	
	@JsonGetter
	public LocalDate getOrderDate() throws IOException {
		return origin.orderDate();
	}
	
	@JsonGetter
	public LocalDate getDueDate() throws IOException {
		return origin.dueDate();
	}
	
	@JsonGetter
	public String getPaymentCondition() throws IOException {
		return origin.paymentCondition().toString();
	}
	
	@JsonGetter
	public String getOrigin() throws IOException {
		return origin.origin();
	}
	
	@JsonGetter
	public int getPaymentConditionId() throws IOException {
		return origin.paymentCondition().id();
	}
	
	@JsonGetter
	public String getReference() throws IOException {
		return origin.reference();
	}
	
	@JsonGetter
	public double getTotalAmountHt() throws IOException {
		return origin.totalAmountHt();
	}
	
	@JsonGetter
	public double getTotalTaxAmount() throws IOException {
		return origin.totalTaxAmount();
	}
	
	@JsonGetter
	public double getTotalAmountTtc() throws IOException {
		return origin.totalAmountTtc();
	}
	
	@JsonGetter
	public double getLeftAmountToPay() throws IOException {
		return origin.leftAmountToPay();
	}
	
	@JsonGetter
	public double getTotalAmountPaid() throws IOException {
		return origin.totalAmountPaid();
	}
	
	@JsonGetter
	public String description() throws IOException {
		return origin.description();
	}
	
	@JsonGetter
	public String notes() throws IOException {
		return origin.notes();
	}
	
	@JsonGetter
	public UUID getCustomerId() throws IOException {
		return origin.customer().id();
	}
	
	@JsonGetter
	public String getCustomer() throws IOException {
		return origin.customer().fullName();
	}
	
	@JsonGetter
	public int getNumberOfProducts() throws IOException {
		return origin.products().all().size();
	}
	
	@JsonGetter
	public String getStatus() throws IOException {
		return origin.status().toString();
	}
	
	@JsonGetter
	public int getStatusId() throws IOException {
		return origin.status().id();
	}
	
	@JsonGetter
	public UUID getPurchaseOrderId() throws IOException {
		return origin.purchaseOrder().id();
	}
	
	@JsonGetter
	public String getPurchaseOrder() throws IOException {
		return origin.purchaseOrder().reference();
	}
}
