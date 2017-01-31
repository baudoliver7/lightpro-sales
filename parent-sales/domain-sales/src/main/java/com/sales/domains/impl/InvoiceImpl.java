package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.InvoicePayments;
import com.sales.domains.api.PurchaseOrder;

public class InvoiceImpl implements Invoice {

	private final transient Base base;
	private final transient InvoiceMetadata dm;
	private final transient UUID id;
	private final transient DomainStore ds;
	
	public InvoiceImpl(final Base base, final UUID id){
		this.base = base;
		this.dm = InvoiceMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
		this.id = id;
	}
	
	@Override
	public LocalDate orderDate() throws IOException {
		java.sql.Date date = ds.get(dm.orderDateKey());
		return date.toLocalDate();
	}

	@Override
	public String reference() throws IOException {
		return ds.get(dm.referenceKey());
	}

	@Override
	public double totalAmountHt() throws IOException {
		return ds.get(dm.totalAmountHtKey());
	}

	@Override
	public double totalTaxAmount() throws IOException {
		return ds.get(dm.totalTaxAmountKey());
	}

	@Override
	public double totalAmountTtc() throws IOException {
		return ds.get(dm.totalAmountTtcKey());
	}

	@Override
	public String notes() throws IOException {
		return ds.get(dm.notesKey());
	}

	@Override
	public Customer customer() throws IOException {
		return purchaseOrder().customer();
	}

	@Override
	public OrderProducts products() throws IOException {
		return new InvoiceProducts(new OrderProductsImpl(base, this));
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return id;
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public PurchaseOrder purchaseOrder() throws IOException {
		UUID orderId = ds.get(dm.purchaseOrderIdKey());
		return new PurchaseOrderImpl(base, orderId);
	}

	@Override
	public InvoiceStatus status() throws IOException {
		int statusId = ds.get(dm.statusKey());
		return InvoiceStatus.get(statusId);
	}

	@Override
	public double leftAmountToPay() throws IOException {
		return ds.get(dm.leftAmountToPayKey());
	}

	@Override
	public double totalAmountPaid() throws IOException {
		return ds.get(dm.totalAmountPaidKey());
	}

	@Override
	public void updateAmounts() throws IOException {
		
		double amountHt = 0;
		double taxAmount = 0;
		double amountTtc = 0;
		
		for (OrderProduct op : products().all()) {
			amountHt += op.totalAmountHt();
			taxAmount += op.totalTaxAmount();
			amountTtc += op.totalAmountTtc();
		}
		
		double paidAmount = payments().all().stream().mapToDouble(m -> {
			try {
				return m.paidAmount();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		}).sum();
		
		double leftAmountToPay = amountTtc - paidAmount;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.totalAmountHtKey(), amountHt);
		params.put(dm.totalTaxAmountKey(), taxAmount);
		params.put(dm.totalAmountTtcKey(), amountTtc);
		params.put(dm.totalAmountPaidKey(), paidAmount);
		params.put(dm.leftAmountToPayKey(), leftAmountToPay);
		
		ds.set(params);	
		
		if(leftAmountToPay == 0)
			markPaid();
		else
			markOpened();
	}

	@Override
	public LocalDate dueDate() throws IOException {
		java.sql.Date date = ds.get(dm.dueDateKey());
		return date.toLocalDate();
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public String origin() throws IOException {
		return ds.get(dm.originKey());
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		int paymentCondition = ds.get(dm.paymentConditionIdKey());
		return PaymentConditionStatus.get(paymentCondition);
	}

	@Override
	public void update(LocalDate date, PaymentConditionStatus paymentCondition, String description, String notes) throws IOException {
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));	
		
		LocalDate dueDate = date.plusDays(paymentCondition.numberOfDays());
		params.put(dm.dueDateKey(), java.sql.Date.valueOf(dueDate));	
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.descriptionKey(), description);
		params.put(dm.notesKey(), notes);
		
		ds.set(params);		
		
		updateAmounts();
	}
	
	@Override
	public boolean isEqual(Order item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(Order item) {
		return !isEqual(item);
	}

	@Override
	public InvoiceType type() throws IOException {
		int typeId = ds.get(dm.invoiceTypeIdKey());
		return InvoiceType.get(typeId);
	}

	@Override
	public InvoicePayments payments() throws IOException {
		return new InvoicePaymentsImpl(base, this);
	}

	@Override
	public void markOpened() throws IOException {
		ds.set(dm.statusKey(), InvoiceStatus.OPENED.id());
	}

	@Override
	public void markPaid() throws IOException {
		ds.set(dm.statusKey(), InvoiceStatus.PAID.id());
	}
}
