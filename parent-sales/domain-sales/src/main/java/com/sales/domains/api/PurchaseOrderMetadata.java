package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class PurchaseOrderMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public PurchaseOrderMetadata() {
		this.domainName = "sales.purchaseorders";
		this.keyName = "id";
	}
	
	public PurchaseOrderMetadata(final String domainName, final String keyName){
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

	public String orderDateKey(){
		return "orderdate";
	}
	
	public String expirationDateKey(){
		return "expirationdate";
	}	
	
	public String referenceKey(){
		return "reference";
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
	
	public String cgvKey(){
		return "cgv";
	}
	
	public String notesKey(){
		return "notes";
	}
	
	public String customerIdKey(){
		return "customerid";
	}
	
	public String sellerIdKey(){
		return "sellerid";
	}
	
	public String statusIdKey(){
		return "statusid";
	}
	
	public String paymentConditionIdKey(){
		return "payment_condition_id";
	}
	
	public String moduleIdKey(){
		return "moduleid";
	}	
	
	public static PurchaseOrderMetadata create(){
		return new PurchaseOrderMetadata();
	}
}
