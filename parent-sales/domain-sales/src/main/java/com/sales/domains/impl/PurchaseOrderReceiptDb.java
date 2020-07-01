package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.datasource.Base;
import com.sales.domains.api.PaymentNature;
import com.sales.domains.api.PaymentOriginType;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderReceipt;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class PurchaseOrderReceiptDb extends PaymentDbBase implements PurchaseOrderReceipt {
	
	public PurchaseOrderReceiptDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Ticket de caisse introuvable !", module);	
	}
	
	@Override
	protected boolean isPresent(){
		
		try {
			return super.isPresent() && ds.get(dm.purchaseOrderIdKey()) != null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public PurchaseOrder order() throws IOException {
		UUID orderId = ds.get(dm.purchaseOrderIdKey());
		return module().purchases().get(orderId);
	}

	@Override
	public void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference) throws IOException {
		
		validate(status(), paymentDate, object, paidAmount, mode, transactionReference);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.paymentDateKey(), java.sql.Date.valueOf(paymentDate));	
		params.put(dm.objectKey(), object);
		params.put(dm.paidAmountKey(), paidAmount);
		params.put(dm.modeIdKey(), mode.id());
		params.put(dm.transactionReferenceKey(), transactionReference);
		
		ds.set(params);	
	}

	@Override
	public void validate() throws IOException {
		
		if(status() != PaymentStatus.DRAFT)
			throw new IllegalArgumentException("Vous ne pouvez effectuer une opération de validation sur ce paiement !");
		
		PaymentType type = type();
		
		if(type == PaymentType.ENCAISSEMENT){
			if(order().saleAmount().totalAmountTtc() < paidAmount())
				throw new IllegalArgumentException("Le montant payé excède le net à payer !");
		}
		
		if(type == PaymentType.REMBOURSEMENT){
			if(!order().isNone() && order().saleAmount().totalAmountTtc() < paidAmount())
				throw new IllegalArgumentException("Le montant remboursé excède le montant payé !");
		}	
		
		ds.set(dm.referenceKey(), sequence().generate());
		ds.set(dm.statusIdKey(), PaymentStatus.VALIDATED.id());
		ds.set(dm.stepKey(), PaymentStep.EMITTED.id());
		
		ComptaInterface comptaInterface = module().interfacage().comptaInterface();		
		if(comptaInterface.available())
			comptaInterface.send(this, true);
	}

	@Override
	public PaymentNature nature() throws IOException {
		return PaymentNature.TICKET_CAISSE;
	}

	@Override
	public String origin() throws IOException {
		return String.format("Commande N°%s", order().reference());
	}

	@Override
	public double receivedAmount() throws IOException {
		return ds.get(dm.receivedAmountKey());
	}

	@Override
	public double change() throws IOException {
		return ds.get(dm.changeKey());
	}

	@Override
	public UUID originId() throws IOException {
		return order().id();
	}
	
	@Override
	public PaymentOriginType originType() throws IOException {
		return PaymentOriginType.PURCHASE_ORDER;
	}	
	
	@Override
	public Contact cashier() throws IOException {
		UUID cashierId = ds.get(dm.cashierIdKey());
		return module().contacts().get(cashierId);
	}

	@Override
	public boolean paidByProvision() throws IOException {
		return false;
	}	
}
