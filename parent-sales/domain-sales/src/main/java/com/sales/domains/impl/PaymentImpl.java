package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentMode;

public class PaymentImpl implements Payment {

	private final transient Base base;
	private final transient UUID id;
	private final transient PaymentMetadata dm;
	private final transient DomainStore ds;
	
	public PaymentImpl(final Base base, final UUID id){
		this.base = base;
		this.id = id;
		this.dm = PaymentMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);	
	}
	
	@Override
	public String object() throws IOException {
		return ds.get(dm.objectKey());
	}

	@Override
	public String reference() throws IOException {
		return ds.get(dm.referenceKey());
	}

	@Override
	public LocalDate paymentDate() throws IOException {
		java.sql.Date date = ds.get(dm.paymentDateKey());
		return date.toLocalDate();
	}

	@Override
	public double paidAmount() throws IOException {
		return ds.get(dm.paidAmountKey());
	}

	@Override
	public PaymentMode mode() throws IOException {
		int modeId = ds.get(dm.modeIdKey());
		return PaymentMode.get(modeId);
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return UUIDConvert.fromObject(this.id);
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public Invoice invoice() throws IOException {
		UUID invoiceId = ds.get(dm.invoiceIdKey());
		return new InvoiceImpl(base, invoiceId);
	}

	@Override
	public void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode) throws IOException {
		
		if (paymentDate == null) {
            throw new IllegalArgumentException("Invalid payment date : it can't be empty!");
        }
		
		if (StringUtils.isBlank(object)) {
            throw new IllegalArgumentException("Invalid object : it can't be empty!");
        }
		
		if (paidAmount == 0) {
            throw new IllegalArgumentException("Invalid paid amount : it can't be zero !");
        }
		
		if((invoice().totalAmountPaid() - paidAmount()) + paidAmount > invoice().totalAmountTtc())
			throw new IllegalArgumentException("Montant trop élevé par rapport au reste à payer  !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.paymentDateKey(), java.sql.Date.valueOf(paymentDate));	
		params.put(dm.objectKey(), object);
		params.put(dm.paidAmountKey(), paidAmount);
		params.put(dm.modeIdKey(), mode.id());
		
		ds.set(params);	
		
		invoice().updateAmounts();
	}
	
	@Override
	public boolean isEqual(Payment item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(Payment item) {
		return !isEqual(item);
	}
}
