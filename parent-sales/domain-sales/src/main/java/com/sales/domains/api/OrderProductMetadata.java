package com.sales.domains.api;

import com.infrastructure.core.DomainMetadata;

public class OrderProductMetadata implements DomainMetadata {

	private final transient String domainName;
	private final transient String keyName;
	
	public OrderProductMetadata() {
		this.domainName = "sales.orderproducts";
		this.keyName = "id";
	}
	
	public OrderProductMetadata(final String domainName, final String keyName){
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

	public String quantityKey(){
		return "quantity";
	}
	
	public String unitPriceKey(){
		return "unitprice";
	}	
	
	public String reductionAmountKey(){
		return "reductionamount";
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
	
	public String orderIdKey(){
		return "orderid";
	}
	
	public String productIdKey(){
		return "productid";
	}
	
	public String nameKey(){
		return "name";
	}
	
	public String descriptionKey(){
		return "description";
	}
	
	public String netCommercialKey(){
		return "net_commercial";
	}
	
	public String categoryIdKey(){
		return "categoryid";
	}
	
	public String deductibleKey(){
		return "deductible";
	}
	
	public String originProductId(){
		return "origin_productid";
	}
	
	public String reduceValueKey(){
		return "reduce_value";
	}
	
	public String reduceValueTypeIdKey(){
		return "reduce_value_typeid";
	}
	
	public static OrderProductMetadata create(){
		return new OrderProductMetadata();
	}
}
