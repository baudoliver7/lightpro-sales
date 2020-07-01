package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Customer;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Sales;
import com.securities.api.PaymentMode;
import com.securities.api.PaymentModeType;
import com.securities.api.Sequence;
import com.securities.api.Sequence.SequenceReserved;

public abstract class PaymentDbBase extends GuidKeyEntityDb<Payment, PaymentMetadata> implements Payment {

	private final Sales module;
	
	public PaymentDbBase(Base base, UUID id, String msgNotFound, Sales module) {
		super(base, id, msgNotFound);
		this.module = module;
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
		UUID modeId = ds.get(dm.modeIdKey());
		return module().paymentModes().get(modeId);
	}
	
	@Override
	public String transactionReference() throws IOException {
		return ds.get(dm.transactionReferenceKey());
	}

	@Override
	public PaymentStep step() throws IOException {
		int stepId = ds.get(dm.stepKey());
		return PaymentStep.get(stepId);
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		UUID modulePdvId = ds.get(dm.modulePdvIdKey());
		return module().modulePdvs().get(modulePdvId);
	}

	@Override
	public PaymentStatus status() throws IOException {
		int statusId = ds.get(dm.statusIdKey());
		return PaymentStatus.get(statusId);
	}
	
	protected Sequence sequence() throws IOException {
		return module().company().moduleAdmin().sequences().reserved(SequenceReserved.PAYMENT);
	}
	
	@Override
	public PaymentType type() throws IOException {
		int typeId = ds.get(dm.typeIdKey());
		return PaymentType.get(typeId);
	}
	
	@Override
	public Customer customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return module().customers().get(customerId);
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}
	
	@Override
	public double affectedPaidAmount() throws IOException {
		return paidAmount() - providedAmount();
	}
	
	@Override
	public double providedAmount() throws IOException {
		return module().provisions().build(id).amount();
	}
	
	@Override
	public void changeStep(PaymentStep step) throws IOException {
		ds.set(dm.stepKey(), step.id());
	}	
	
	public static void validate(PaymentStatus status, LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference) throws IOException{
		
		if(status == PaymentStatus.VALIDATED)
			throw new IllegalArgumentException("Vous ne pouvez pas modifier un paiement validé !");
		
		if (paymentDate == null) {
            throw new IllegalArgumentException("La date de paiement doit être renseignée !");
        }
		
		if (StringUtils.isBlank(object)) {
            throw new IllegalArgumentException("L'objet doit être renseignée !");
        }
		
		if (paidAmount <= 0) {
            throw new IllegalArgumentException("Vous devez renseigner un montant positif !");
        }
		
		if(mode.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un mode de paiement !");
		
		if(mode.type() == PaymentModeType.BANK && StringUtils.isBlank(transactionReference))
			throw new IllegalArgumentException(String.format("Vous devez renseigner la référence de la transaction !"));
	}
}
