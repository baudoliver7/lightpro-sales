package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.infrastructure.datasource.Base.OrderDirection;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentMode;
import com.securities.api.Sequence;
import com.securities.api.SequenceMetadata;
import com.securities.api.Sequence.SequenceReserved;
import com.securities.impl.SequenceImpl;
import com.sales.domains.api.InvoicePayments;

public class InvoicePaymentsImpl implements InvoicePayments {

	private transient final Base base;
	private final transient PaymentMetadata dm;
	private final transient DomainsStore ds;
	private final transient Invoice invoice;
	
	public InvoicePaymentsImpl(final Base base, final Invoice invoice){
		this.base = base;
		this.dm = PaymentMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.invoice = invoice;
	}
	
	@Override
	public List<Payment> all() throws IOException {
		List<Payment> values = new ArrayList<Payment>();
		
		List<DomainStore> results = ds.getAllByKeyOrdered(dm.invoiceIdKey(), invoice.id(), HorodateImpl.dm().dateCreatedKey(), OrderDirection.ASC);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}			
		
		return values;
	}

	@Override
	public Payment get(UUID id) throws IOException {
		Payment item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("Le paiement n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public Payment build(UUID id) throws IOException {
		return new PaymentImpl(base, id);
	}

	@Override
	public void delete(Payment item) throws IOException {
		ds.delete(item.id());
		
		invoice.updateAmounts();
	}

	@Override
	public Payment add(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode) throws IOException {
		
		if (paymentDate == null) {
            throw new IllegalArgumentException("Invalid payment date : it can't be empty!");
        }
		
		if (StringUtils.isBlank(object)) {
            throw new IllegalArgumentException("Invalid object : it can't be empty!");
        }
		
		if (paidAmount == 0) {
            throw new IllegalArgumentException("Invalid paid amount : it can't be zero !");
        }
		
		if(invoice.leftAmountToPay() < paidAmount)
			throw new IllegalArgumentException("Montant trop élevé par rapport au reste à payer  !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.paymentDateKey(), java.sql.Date.valueOf(paymentDate));	
		params.put(dm.objectKey(), object);
		params.put(dm.paidAmountKey(), paidAmount);
		params.put(dm.referenceKey(), sequence().generate());
		params.put(dm.modeIdKey(), mode.id());
		params.put(dm.invoiceIdKey(), invoice.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		invoice.updateAmounts();
		
		return build(id);
	}
	
	private Sequence sequence() throws IOException{
		SequenceMetadata dm = SequenceImpl.dm();
		DomainsStore ds = base.domainsStore(dm);
	
		SequenceReserved code = SequenceReserved.PAYMENT;		
		List<Object> values = ds.find(String.format("SELECT %s FROM %s WHERE %s=?", dm.keyName(), dm.domainName(), dm.codeIdKey()), Arrays.asList(code.id()));
		
		if(values.isEmpty())
			throw new IllegalArgumentException(String.format("Vous devez configurer la séquence de %s !", code.toString()));
		
		return new SequenceImpl(base, UUIDConvert.fromObject(values.get(0)));
	}
}
