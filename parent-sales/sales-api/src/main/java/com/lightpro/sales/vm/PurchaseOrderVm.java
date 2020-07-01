package com.lightpro.sales.vm;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sales.domains.api.PurchaseOrder;

public final class PurchaseOrderVm {
	
	public final UUID id;
	public final LocalDate orderDate;
	public final LocalDate expirationDate;
	public final String paymentCondition;
	public final int paymentConditionId;
	public final String reference;
	public final double amountInvoiced;
	public final double leftAmountToInvoice;
	public final String cgv;
	public final String description;
	public final String notes;
	public final UUID customerId;
	public final String customer;
	public final UUID sellerId;
	public final String seller;
	public final long numberOfProducts;
	public final long numberOfInvoices;
	public final String status;
	public final int statusId;
	public final int livraisonDelayInDays;
	public final LocalDate soldDate;
	public final LocalDate livraisonDate;
	public final UUID modulePdvId;
	public final String modulePdv;
	public final List<SaleTaxVm> taxes;
	public final double totalAmountHt;
	public final double totalTaxAmount;
	public final double totalAmountTtc;
	public final double netCommercial;
	public final double reduceAmount;
	public final String title;
	
	public PurchaseOrderVm(){
		throw new UnsupportedOperationException("#PurchaseOrderVm()");
	}
	
	public PurchaseOrderVm(final PurchaseOrder origin) {
		try {
			this.id = origin.id();
			this.orderDate = origin.orderDate();
			this.expirationDate = origin.expirationDate();
			this.paymentCondition = origin.paymentCondition().toString();
			this.paymentConditionId = origin.paymentCondition().id();
			this.reference = origin.reference();
			this.amountInvoiced = origin.amountInvoiced();
			this.leftAmountToInvoice = origin.leftAmountToInvoice();
			this.cgv = origin.cgv();
			this.description = origin.description();
			this.notes = origin.notes();
			this.customerId = origin.customer().id();
			this.customer = origin.customer().name();
			this.sellerId = origin.seller().id();
			this.seller = origin.seller().name();
			this.numberOfProducts = origin.products().count();
			this.numberOfInvoices = origin.invoices().count();
			this.status = origin.status().toString();
			this.statusId = origin.status().id();
			this.livraisonDelayInDays = origin.livraisonDelayInDays();
			this.soldDate = origin.soldDate();
			this.livraisonDate = origin.livraisonDate();
			this.modulePdvId = origin.modulePdv().id();
			this.modulePdv = origin.modulePdv().name();
			this.taxes = origin.taxes().all().stream().map(m -> new SaleTaxVm(m)).collect(Collectors.toList());			
			this.totalAmountHt = origin.saleAmount().totalAmountHt();
			this.totalTaxAmount = origin.saleAmount().totalTaxAmount();
			this.totalAmountTtc = origin.saleAmount().totalAmountTtc();
			this.netCommercial = origin.saleAmount().netCommercial();
			this.reduceAmount = origin.saleAmount().reduceAmount();
			this.title = origin.title();		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
