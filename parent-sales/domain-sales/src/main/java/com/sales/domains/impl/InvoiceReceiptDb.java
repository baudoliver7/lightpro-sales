package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.datasource.Base;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.PaymentNature;
import com.sales.domains.api.PaymentOriginType;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Provision;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class InvoiceReceiptDb extends PaymentDbBase implements InvoiceReceipt {
	
	public InvoiceReceiptDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Paiement de facture introuvable !", module);	
	}	
	
	@Override
	protected boolean isPresent(){
		try {
			return super.isPresent() && ds.get(dm.invoiceIdKey()) != null && type() == PaymentType.ENCAISSEMENT;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Invoice invoice() throws IOException {
		UUID invoiceId = ds.get(dm.invoiceIdKey());
		return module().invoices().get(invoiceId);
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
	public void validate(boolean forcePayment) throws IOException {
		
		if(status() != PaymentStatus.DRAFT)
			throw new IllegalArgumentException("Vous ne pouvez effectuer une opération de validation sur ce paiement !");
		
		PaymentType type = type();
		
		if(type == PaymentType.ENCAISSEMENT){
			if(invoice().leftAmountToPay() < paidAmount() && !forcePayment)
				throw new IllegalArgumentException("Le montant payé excède le net à payer !");
		}
		
		if(type == PaymentType.REMBOURSEMENT){
			if(!invoice().isNone() && invoice().totalAmountPaid() < paidAmount())
				throw new IllegalArgumentException("Le montant remboursé excède le montant payé !");
		}	
		
		ds.set(dm.referenceKey(), sequence().generate());
		ds.set(dm.statusIdKey(), PaymentStatus.VALIDATED.id());
		ds.set(dm.stepKey(), PaymentStep.EMITTED.id());	
		
		ComptaInterface comptaInterface = module().interfacage().comptaInterface();		
		if(comptaInterface.available())
			comptaInterface.send(this, true);
		
		if(forcePayment){
			// Faire une provision avec la différence
			makeProvision(-invoice().solde());
		}else{
			if(!provision().isNone()){
				// paiement à partir d'une provision
				provision().checkConsommed();
			}
		}
	}	

	@Override
	public Provision makeProvision(double amount) throws IOException {
		Provision provision = module().provisions().add(amount, this);
		provision.validate();
		return provision;
	}

	@Override
	public PaymentNature nature() throws IOException {
		return PaymentNature.RECU;
	}

	@Override
	public String origin() throws IOException {
		return String.format("Facture N°%s", invoice().reference());
	}

	@Override
	public Provision provision() throws IOException {
		UUID provisionId = ds.get(dm.provisionIdKey());
		return module().provisions().build(provisionId);
	}

	@Override
	public UUID originId() throws IOException {
		return invoice().id();
	}

	@Override
	public PaymentOriginType originType() throws IOException {
		return PaymentOriginType.INVOICE;
	}

	@Override
	public Contact cashier() throws IOException {
		UUID cashierId = ds.get(dm.cashierIdKey());
		return module().contacts().get(cashierId);
	}

	@Override
	public boolean paidByProvision() throws IOException {
		return !provision().isNone();
	}	
}
