package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class PaymentMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public PaymentMetadata() {
		this.domainName = "sales.payments";
		this.keyName = "id";
	}
	
	public PaymentMetadata(final String domainName, final String keyName){
		this.domainName = domainName;
		this.keyName = keyName;
	}
	
	@Override
	public String domainName() {
		return this.domainName;
	}

	@Override
	public String keyName() {
		return this.keyName;
	}

	public String referenceKey(){
		return "reference";
	}
	
	public String objectKey(){
		return "object";
	}
	
	public String paymentDateKey(){
		return "paymentdate";
	}
	
	public String paidAmountKey(){
		return "paidamount";
	}	
	
	public String modeIdKey(){
		return "modeid";
	}
	
	public String invoiceIdKey(){
		return "invoiceid";
	}	
	
	public static PaymentMetadata create(){
		return new PaymentMetadata();
	}
}
