package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderInvoices;
import com.sales.domains.api.PurchaseOrderStatus;
import com.securities.api.Sequence;
import com.securities.api.Sequence.SequenceReserved;

public class PurchaseOrderInvoicesImpl implements PurchaseOrderInvoices {

	private transient final Base base;
	private final transient InvoiceMetadata dm;
	private final transient DomainsStore ds;
	private final transient PurchaseOrder purchaseOrder;
	
	public PurchaseOrderInvoicesImpl(final Base base, UUID purchaseOrderId){
		this.base = base;
		this.dm = InvoiceMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
		this.purchaseOrder = new PurchaseOrderImpl(base, purchaseOrderId);
	}
	
	@Override
	public List<Invoice> all() throws IOException {
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement = String.format("SELECT %s FROM %s WHERE %s=? ORDER BY %s ASC", dm.keyName(), dm.domainName(), dm.purchaseOrderIdKey(), hm.dateCreatedKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(purchaseOrder.id());
		
		return ds.find(statement, params)
				 .stream()
				 .map(m -> build(UUIDConvert.fromObject(m)))
				 .collect(Collectors.toList());
	}

	@Override
	public Invoice build(UUID id) {
		return new InvoiceImpl(base, id);		
	}

	@Override
	public boolean contains(Invoice item) {
		try {
			return item.isPresent() && item.purchaseOrder().isEqual(purchaseOrder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Invoice get(UUID id) throws IOException {
		Invoice item = build(id);
		
		if(!contains(item))
			throw new NotFoundException("La facture n'a pas été trouvée !");
		
		return item;
	}

	@Override
	public void delete(Invoice item) throws IOException {
		if(contains(item))
		{
			item.products().deleteAll();
			ds.delete(item.id());
			purchaseOrder.markSold();
		}
	}

	private Invoice add(LocalDate date, InvoiceType type, PaymentConditionStatus paymentCondition, String description, String notes) throws IOException {
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));		
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.originKey(), String.format("Selon bon de commande N° %s", purchaseOrder.reference()));
		params.put(dm.descriptionKey(), description);
		params.put(dm.notesKey(), notes);
		params.put(dm.purchaseOrderIdKey(), purchaseOrder.id());
		params.put(dm.referenceKey(), sequence().generate());
		params.put(dm.statusKey(), InvoiceStatus.OPENED.id());
		params.put(dm.invoiceTypeIdKey(), type.id());
		
		LocalDate dueDate = date.plusDays(paymentCondition.numberOfDays());
		params.put(dm.dueDateKey(), java.sql.Date.valueOf(dueDate));
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}
	
	private Sequence sequence() throws IOException{
		return purchaseOrder.module().company().sequences().reserved(SequenceReserved.INVOICE);
	}

	@Override
	public void deleteAll() throws IOException {
		for (Invoice inv : all()) {
			delete(inv);
		}
	}
	
	@Override
	public Invoice generateDownPayment(LocalDate invoiceDate, double amount, boolean withTax) throws IOException {		
		
		Invoice invoice = add(invoiceDate, InvoiceType.DOWN_PAYMENT, purchaseOrder.paymentCondition(), null, null);
		invoice.products().add(1, amount, 0, invoice.reference(), purchaseOrder.module().products().getDownPaymentProduct(), withTax);
			
		return invoice;
	}

	@Override
	public Invoice generateDownPayment(LocalDate invoiceDate, int percent, boolean withTax) throws IOException {
		
		if(percent > 100 || percent <= 0)
			throw new IllegalArgumentException("Vous devez spécifier un nombre compris entre 1 et 100 !");
		
		Invoice invoice = add(invoiceDate, InvoiceType.DOWN_PAYMENT, purchaseOrder.paymentCondition(), null, null);
		invoice.products().add(1, (percent / 100.0) * purchaseOrder.totalAmountHt(), 0, invoice.reference(), purchaseOrder.module().products().getDownPaymentProduct(), withTax);
				
		return invoice;
	}

	@Override
	public Invoice generateFinalInvoice(LocalDate invoiceDate) throws IOException {
		
		if(purchaseOrder.status() == PurchaseOrderStatus.ENTIRELY_INVOICED)
			throw new IllegalArgumentException("Le bon de commande est entièrement facturé !");
		
		Invoice invoice = add(invoiceDate, InvoiceType.FINAL_INVOICE, purchaseOrder.paymentCondition(), null, null);
		
		for (OrderProduct op : purchaseOrder.products().all()) {
			invoice.products().add(op.quantity(), op.unitPrice(), op.reductionAmount(), op.description(), op.product(), op.totalTaxAmount() > 0);
		}
		
		for (Invoice inv : all()) {
			if(inv.type() == InvoiceType.DOWN_PAYMENT)
			{
				OrderProduct op = inv.products().all().get(0);
				invoice.products().add(-1, op.unitPrice(), op.reductionAmount(), op.description(), op.product(), op.totalTaxAmount() > 0);
			}
		}
		
		purchaseOrder.markEntirelyInvoiced();
		
		return invoice;
	}
}
