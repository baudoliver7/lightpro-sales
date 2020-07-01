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
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionRefundReceipt;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class ProvisionRefundReceiptDb extends PaymentDbBase implements ProvisionRefundReceipt {
	
	public ProvisionRefundReceiptDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Reçu de provision introuvable !", module);	
	}
	
	@Override
	protected boolean isPresent(){
		try {
			return super.isPresent() && ds.get(dm.provisionIdKey()) != null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference) throws IOException {
		
		validate(status(), paymentDate, object, paidAmount, mode, transactionReference);
	
		if(provision().availableAmount() < paidAmount)
			throw new IllegalArgumentException("Le montant payé est supérieur au montant de provision disponible !");
		
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
		
		Provision provision = provision();
		if(provision.availableAmount() < paidAmount())
			throw new IllegalArgumentException("Le montant payé est supérieur au montant de provision disponible !");	
		
		ds.set(dm.referenceKey(), sequence().generate());
		ds.set(dm.statusIdKey(), PaymentStatus.VALIDATED.id());
		ds.set(dm.stepKey(), PaymentStep.EMITTED.id());
		
		if(provision.availableAmount() < 0)
			throw new IllegalArgumentException("Le montant de provision disponible n'est pas suffisant pour valider le paiement !");
		
		provision.checkConsommed();
		
		ComptaInterface comptaInterface = module().interfacage().comptaInterface();		
		if(comptaInterface.available())
			comptaInterface.send(this, true);
	}

	@Override
	public Provision provision() throws IOException {
		UUID provisionId = ds.get(dm.provisionIdKey());
		return module().provisions().get(provisionId);
	}

	@Override
	public PaymentNature nature() throws IOException {
		return PaymentNature.RECU;
	}

	@Override
	public String origin() throws IOException {
		return String.format("Provision N°%s", provision().reference());
	}

	@Override
	public UUID originId() throws IOException {
		return provision().id();
	}
	
	@Override
	public PaymentOriginType originType() throws IOException {
		return PaymentOriginType.PROVISION;
	}	
	
	@Override
	public Contact cashier() throws IOException {
		UUID cashierId = ds.get(dm.cashierIdKey());
		return module().contacts().get(cashierId);
	}

	@Override
	public boolean paidByProvision() throws IOException {
		return true;
	}	
}
