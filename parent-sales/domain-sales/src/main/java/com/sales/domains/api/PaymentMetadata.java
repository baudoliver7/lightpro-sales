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
	
	public String transactionReferenceKey(){
		return "transaction_reference";
	}
	
	public String invoiceIdKey(){
		return "invoiceid";
	}	
	
	public String stepKey(){
		return "step";
	}
	
	public String statusIdKey(){
		return "statusid";
	}
	
	public String typeIdKey(){
		return "typeid";
	}
	
	public String provisionIdKey(){
		return "provisionid";
	}
	
	public String customerIdKey(){
		return "customerid";
	}
	
	public String moduleIdKey(){
		return "moduleid";
	}
	
	public String modulePdvIdKey(){
		return "module_pdvid";
	}
	
	public String purchaseOrderIdKey(){
		return "purchase_orderid";
	}
	
	public String receivedAmountKey(){
		return "received_amount";
	}
	
	public String changeKey(){
		return "change";
	}
	
	public String cashierIdKey(){
		return "cashierid";
	}
	
	public static PaymentMetadata create(){
		return new PaymentMetadata();
	}
}
