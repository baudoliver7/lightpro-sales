package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionMetadata;
import com.sales.domains.api.ProvisionRefundReceipt;
import com.sales.domains.api.ProvisionStatus;
import com.sales.domains.api.ProvisionStep;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.api.Sequence;
import com.securities.api.Sequence.SequenceReserved;

public final class ProvisionDb extends GuidKeyEntityDb<Provision, ProvisionMetadata> implements Provision {

	private final Sales module;
	
	public ProvisionDb(Base base, UUID id, Sales module) {
		super(base, id, "Provision introuvable !");
		this.module = module;
	}

	@Override
	public String reference() throws IOException {
		return ds.get(dm.referenceKey());
	}

	@Override
	public double amount() throws IOException {
		return ds.get(dm.amountKey());
	}

	@Override
	public ProvisionStatus status() throws IOException {
		int statusId = ds.get(dm.statusIdKey());
		return ProvisionStatus.get(statusId);
	}

	@Override
	public InvoiceReceipt originPayment() throws IOException {
		return new InvoiceReceiptDb(base, id, module);
	}

	@Override
	public Customer customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return new CustomerDb(base, customerId, module);
	}

	@Override
	public void validate() throws IOException {
		
		if(status() != ProvisionStatus.DRAFT)
			throw new IllegalArgumentException("Vous ne pouvez valider qu'une provision en mode Brouillon !");
		
		if(originPayment().paidAmount() < amount())
			throw new IllegalArgumentException("Le montant de provision ne peut pas être supérieur au montant du paiement !");
		
		ds.set(dm.statusIdKey(), ProvisionStatus.NON_CONSOMMEE.id());
		ds.set(dm.stepIdKey(), ProvisionStep.EMITTED.id());
		ds.set(dm.referenceKey(), sequence().generate()); 
		
		ComptaInterface comptaInterface = customer().module().interfacage().comptaInterface();		
		if(comptaInterface.available())
			comptaInterface.send(this, true);
	}

	@Override
	public void update(double amount) throws IOException {
		
		if(amount <= 0)
			throw new IllegalArgumentException("Vous devez indiquer un montant supérieur à zéro !");
		
		if(status() != ProvisionStatus.DRAFT)
			throw new IllegalArgumentException("Vous ne pouvez pas modifier la provision !");
		
		ds.set(dm.amountKey(), amount); 
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Invoice invoice, Contact cashier) throws IOException {		
		return invoice.cash(paymentDate, object, amount, this, cashier);
	}

	@Override
	public ProvisionRefundReceipt refund(LocalDate date, String object, double amount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		return (ProvisionRefundReceipt)originPayment().invoice().payments().of(this).ofVenteDirecte().refundProvision(date, object, amount, mode, transactionReference, cashier);
	}

	@Override
	public Payments payments() throws IOException {
		return originPayment().invoice().module().payments().of(this);
	}

	@Override
	public Payments receipts() throws IOException {
		return originPayment().invoice().payments().of(this).of(PaymentType.ENCAISSEMENT);
	}

	@Override
	public Payments refunds() throws IOException {
		return originPayment().invoice().payments().of(this).of(PaymentType.REMBOURSEMENT);
	}

	@Override
	public void checkConsommed() throws IOException {
		
		if(availableAmount() == 0)
			ds.set(dm.statusIdKey(), ProvisionStatus.CONSOMMEE.id());
	}

	@Override
	public double amountConsommed() throws IOException {
		double amount = 0;
		
		for (Payment payment : payments().all()) {
			if(payment.status() == PaymentStatus.VALIDATED)
				amount += payment.paidAmount();
		}
		
		return amount;
	}

	@Override
	public double availableAmount() throws IOException {
		return amount() - amountConsommed();
	}
	
	private Sequence sequence() throws IOException {
		return originPayment().invoice().module().company().moduleAdmin().sequences().reserved(SequenceReserved.PROVISION);
	}
	
	@Override
	public LocalDate provisionDate() throws IOException {
		return horodate().dateCreated().toLocalDate();
	}
	
	@Override
	public String toString(){
		try {
			return String.format("%s (%s)", reference(), originPayment().invoice().module().company().currency().toString(availableAmount()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public ProvisionStep step() throws IOException {
		int stepId = ds.get(dm.stepIdKey());
		return ProvisionStep.get(stepId);
	}

	@Override
	public void changeStep(ProvisionStep step) throws IOException {
		ds.set(dm.stepIdKey(), step.id());
	}
}
