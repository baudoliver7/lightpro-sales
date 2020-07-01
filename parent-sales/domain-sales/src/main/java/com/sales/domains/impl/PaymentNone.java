package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Customer;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentNature;
import com.sales.domains.api.PaymentOriginType;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Sales;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.impl.ContactNone;
import com.securities.impl.PaymentModeNone;

public final class PaymentNone extends GuidKeyEntityNone<Payment> implements Payment {

	@Override
	public PaymentType type() throws IOException {
		return PaymentType.NONE;
	}

	@Override
	public String object() throws IOException {
		return null;
	}

	@Override
	public String reference() throws IOException {
		return null;
	}

	@Override
	public LocalDate paymentDate() throws IOException {
		return null;
	}

	@Override
	public double paidAmount() throws IOException {
		return 0;
	}

	@Override
	public PaymentMode mode() throws IOException {
		return new PaymentModeNone();
	}

	@Override
	public String transactionReference() throws IOException {
		return null;
	}

	@Override
	public PaymentStep step() throws IOException {
		return PaymentStep.NONE;
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		return new ModulePdvNone();
	}

	@Override
	public PaymentStatus status() throws IOException {
		return PaymentStatus.NONE;
	}

	@Override
	public void update(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference) throws IOException {
		
	}

	@Override
	public double providedAmount() throws IOException {
		return 0;
	}

	@Override
	public Customer customer() throws IOException {
		return new CustomerNone();
	}

	@Override
	public Sales module() throws IOException {
		return new SalesNone();
	}

	@Override
	public PaymentNature nature() throws IOException {
		return PaymentNature.NONE;
	}

	@Override
	public String origin() throws IOException {
		return null;
	}

	@Override
	public double affectedPaidAmount() throws IOException {
		return 0;
	}

	@Override
	public UUID originId() throws IOException {
		return null;
	}
	
	@Override
	public PaymentOriginType originType() throws IOException {
		return PaymentOriginType.NONE;
	}	
	
	@Override
	public Contact cashier() throws IOException {
		return new ContactNone();
	}

	@Override
	public void changeStep(PaymentStep step) throws IOException {

	}

	@Override
	public boolean paidByProvision() throws IOException {
		return false;
	}	
}
