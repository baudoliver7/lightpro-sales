package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderInvoices;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderStatus;
import com.securities.api.User;
import com.securities.impl.UserImpl;

public class PurchaseOrderImpl implements PurchaseOrder {

	private final transient Base base;
	private final transient UUID id;
	private final transient PurchaseOrderMetadata dm;
	private final transient DomainStore ds;
	
	public PurchaseOrderImpl(final Base base, final UUID id){
		this.base = base;
		this.id = id;
		this.dm = PurchaseOrderMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
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
	public String cgv() throws IOException {
		return ds.get(dm.cgvKey());
	}

	@Override
	public String notes() throws IOException {
		return ds.get(dm.notesKey());
	}

	@Override
	public Customer customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return new CustomerImpl(base, customerId);
	}

	@Override
	public User seller() throws IOException {
		UUID userId = ds.get(dm.sellerIdKey());
		return new UserImpl(base, userId);
	}

	@Override
	public OrderProducts products() throws IOException {
		return new PurchaseOrderProducts(new OrderProductsImpl(base, this));
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public PurchaseOrderStatus status() throws IOException {
		int statusId = ds.get(dm.statusIdKey());
		return PurchaseOrderStatus.get(statusId);
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
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.totalAmountHtKey(), amountHt);
		params.put(dm.totalTaxAmountKey(), taxAmount);
		params.put(dm.totalAmountTtcKey(), amountTtc);
		
		ds.set(params);		
	}

	@Override
	public void markSend() throws IOException {
		if(status() != PurchaseOrderStatus.DRAFT)
			throw new IllegalArgumentException("Pour être envoyé, le devis doit être en mode Brouillon !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.DEVIS_ENVOYE.id());
	}

	@Override
	public void markSold() throws IOException {
		if(!(status() == PurchaseOrderStatus.DEVIS_ENVOYE || status() == PurchaseOrderStatus.ENTIRELY_INVOICED))
			throw new IllegalArgumentException("Pour être confirmé, le devis doit être en mode Devis envoyé !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.COMMANDE_CLIENT.id());
	}

	@Override
	public void cancel() throws IOException {
		if(!(status() == PurchaseOrderStatus.DEVIS_ENVOYE || status() == PurchaseOrderStatus.DRAFT))
			throw new IllegalArgumentException("Pour être annulé, le devis doit être en mode Brouillon ou Devis envoyé !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.ANNULE.id());
	}

	@Override
	public void reOpen() throws IOException {
		if(status() != PurchaseOrderStatus.ANNULE)
			throw new IllegalArgumentException("Pour être ré-ouvert, le devis doit être en mode Annulé !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.DRAFT.id());
	}

	@Override
	public LocalDate expirationDate() throws IOException {
		java.sql.Date date = ds.get(dm.expirationDateKey());
		return date.toLocalDate();
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		int paymentConditionId = ds.get(dm.paymentConditionIdKey());
		return PaymentConditionStatus.get(paymentConditionId);
	}

	@Override
	public void update(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String notes, Customer customer, User seller) throws IOException {
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		if (expirationDate == null)
            throw new IllegalArgumentException("Invalid expiration date : it can't be empty!");
		
		if (!customer.isPresent())
            customer = new CustomersImpl(base).get(UUIDConvert.fromObject("7a4c8230-2df3-4668-8c62-fe98776d37a9"));		
		
		if (!seller.isPresent())
            throw new IllegalArgumentException("Invalid seller : it can't be empty!");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));	
		params.put(dm.expirationDateKey(), java.sql.Date.valueOf(expirationDate));	
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.cgvKey(), cgv);
		params.put(dm.notesKey(), notes);
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.sellerIdKey(), seller.id());
		
		ds.set(params);		
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
	public PurchaseOrderInvoices invoices() {
		return new PurchaseOrderInvoicesImpl(base, this.id);
	}

	@Override
	public void markEntirelyInvoiced() throws IOException {
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.ENTIRELY_INVOICED.id());
	}
}
