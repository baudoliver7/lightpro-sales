package com.lightpro.sales.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.sales.domains.api.Provision;

public final class ProvisionVm {
	
	public final UUID id;
	public final LocalDate provisionDate;
	public final String reference;
	public final String origin;
	public final UUID modeId;
	public final String mode;
	public final int statusId;
	public final String status;
	public final int stepId;
	public final String step;
	public final String transactionReference;
	public final double amount;
	public final double availableAmount;
	public final double amountConsommed;
	public final String originPayment;
	public final UUID originPaymentId;
	public final String resumeName;
	public final UUID customerId;
	public final String customer;
	
	public ProvisionVm(){
		throw new UnsupportedOperationException("#ProvisionVm()");
	}
	
	public ProvisionVm(final Provision origin) {
		try {
			this.id = origin.id();
	        this.provisionDate = origin.provisionDate();
	        this.reference = origin.reference();
	        this.origin = origin.originPayment().object();
	        this.mode = origin.originPayment().mode().name();
	        this.modeId = origin.originPayment().mode().id();
	        this.statusId = origin.status().id();
	        this.status = origin.status().toString();
	        this.stepId = origin.step().id();
	        this.step = origin.step().toString();
	        this.customerId = origin.customer().id();
	        this.customer = origin.customer().name();
	        this.transactionReference = origin.originPayment().transactionReference();
	        this.amount = origin.amount();
	        this.availableAmount = origin.availableAmount();
	        this.amountConsommed = origin.amountConsommed();
	        this.originPayment = origin.originPayment().reference();
	        this.originPaymentId = origin.originPayment().id();
	        this.resumeName = origin.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
