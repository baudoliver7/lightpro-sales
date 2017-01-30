package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class InvoiceMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public InvoiceMetadata() {
		this.domainName = "sales.invoices";
		this.keyName = "id";
	}
	
	public InvoiceMetadata(final String domainName, final String keyName){
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
	
	public String orderDateKey(){
		return "orderdate";
	}
	
	public String dueDateKey(){
		return "duedate";
	}
	
	public String purchaseOrderIdKey(){
		return "purchaseorderid";
	}
	
	public String statusKey(){
		return "statusid";
	}	
	
	public String totalAmountHtKey(){
		return "totalamountht";
	}	
	
	public String totalTaxAmountKey(){
		return "totaltaxamount";
	}
	
	public String totalAmountTtcKey(){
		return "totalamountttc";
	}
	
	public String leftAmountToPayKey(){
		return "leftamounttopay";
	}	
	
	public String totalAmountPaidKey(){
		return "totalamountpaid";
	}
	
	public String descriptionKey(){
		return "description";
	}
	
	public String originKey(){
		return "origin";
	}
	
	public String notesKey(){
		return "notes";
	}
	
	public String paymentConditionIdKey(){
		return "payment_condition_id";
	}
	
	public String invoiceTypeIdKey(){
		return "invoicetypeid";
	}
	
	public static InvoiceMetadata create(){
		return new InvoiceMetadata();
	}
}
