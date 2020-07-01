package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.Payments;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderReceipt;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;

public final class PurchaseOrderDb extends GuidKeyEntityDb<PurchaseOrder, PurchaseOrderMetadata> implements PurchaseOrder {

	private final Sales module;
	
	public PurchaseOrderDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Devis introuvable !");
		this.module = module;
	}
	
	@Override
	public LocalDate orderDate() throws IOException {
		java.sql.Date date = ds.get(dm.orderDateKey());
		return date.toLocalDate();
	}

	@Override
	public String reference() throws IOException {
		return ds.get(dm.referenceKey());
	}

	@Override
	public String cgv() throws IOException {
		return ds.get(dm.cgvKey());
	}

	@Override
	public String notes() throws IOException {
		return ds.get(dm.notesKey());
	}

	@Override
	public Contact customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return module().contacts().get(customerId);		
	}

	@Override
	public Contact seller() throws IOException {
		UUID sellerId = ds.get(dm.sellerIdKey());
		return module().sellers().get(sellerId);
	}

	@Override
	public OrderProducts products() throws IOException {
		return new PurchaseOrderProducts(new OrderProductsDb(base, this), this);
	}

	@Override
	public PurchaseOrderStatus status() throws IOException {
		int statusId = ds.get(dm.statusIdKey());
		return PurchaseOrderStatus.get(statusId);
	}

	@Override
	public void updateAmounts() throws IOException {
		
		SaleAmount amount = new OrderSaleAmountComputing(products().all());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.totalAmountHtKey(), amount.totalAmountHt());
		params.put(dm.reduceAmountKey(), amount.reduceAmount());
		params.put(dm.netCommercialKey(), amount.netCommercial());
		params.put(dm.totalTaxAmountKey(), amount.totalTaxAmount());
		params.put(dm.totalAmountTtcKey(), amount.totalAmountTtc());
		
		ds.set(params);
	}

	@Override
	public void markSold(LocalDate date, boolean isDeliverDirectly) throws IOException {
		
		if(status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez pas confirmer de vente pour ce devis !");
		
		// 1 - renseigner la date d'édition du bon de commande (date de vente)
		if(date == null)
			throw new IllegalArgumentException("Vous devez renseigner la date de vente !");
		
		if(orderDate().isAfter(date) || date.isAfter(LocalDate.now()))
			throw new IllegalArgumentException("La date de vente contient une valeur erronnée !");
		
		if(products().isEmpty())
			throw new IllegalArgumentException("Vous devez créer au moins une ligne de produit !");
		
		ds.set(dm.soldDateKey(), java.sql.Date.valueOf(date));

		// 2 - renseigner la date de livraison
		LocalDate livraisonDate = date.plusDays(livraisonDelayInDays());
		ds.set(dm.livraisonDateKey(), java.sql.Date.valueOf(livraisonDate));
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.COMMANDE_CLIENT.id());
			
		if(isDeliverDirectly)
			module().interfacage().stocksInterface().deliver(this);
		else
			module().interfacage().stocksInterface().prepareDelivery(this);
	}

	@Override
	public void cancel() throws IOException {
		PurchaseOrderStatus status = status();
		
		if(status == PurchaseOrderStatus.CANCELLED)
			throw new IllegalArgumentException("Le devis est déjà annulé !");
		
		if(status == PurchaseOrderStatus.COMMANDE_CLIENT && !invoices().all().isEmpty())
			throw new IllegalArgumentException("Vous ne pouvez pas annuler une commande partiellement ou totalement facturée !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.CANCELLED.id());
	}

	@Override
	public void reOpen() throws IOException {
		if(PurchaseOrderStatus.CREATED == status())
			return;
		
		if(status() != PurchaseOrderStatus.CANCELLED)
			throw new IllegalArgumentException("Pour être ré-ouvert, le devis doit être en mode Annulé !");
		
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.CREATED.id());
	}

	@Override
	public LocalDate expirationDate() throws IOException {
		java.sql.Date date = ds.get(dm.expirationDateKey());
		return date.toLocalDate();
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		int paymentConditionId = ds.get(dm.paymentConditionIdKey());
		return PaymentConditionStatus.get(paymentConditionId);
	}

	@Override
	public void update(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String description, String notes, Contact customer, Contact seller, int livraisonDelayInDays) throws IOException {
		
		if (seller.isNone())
            throw new IllegalArgumentException("Vous devez indiquer un vendeur !");
		
		if(!seller.equals(seller()) && !module().sellers().contains(seller))
			throw new IllegalArgumentException("Vous devez être un vendeur pour effectuer cette action !");
		
		if(paymentCondition == PaymentConditionStatus.NONE)
			throw new IllegalArgumentException("Vous devez spécifier les conditions de règlement !");
		
		if(status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez plus modifier ce bon de commande !");
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		if (expirationDate == null)
            throw new IllegalArgumentException("Invalid expiration date : it can't be empty!");
		
		if (customer.isNone()){
			customer = module().customers().defaultCustomer(); // mettre un client par défaut		
		}		
		
		if(livraisonDelayInDays < 0)
			throw new IllegalArgumentException("Délai de livraison invalide : vous devez entrer un nombre supérieur à 0 !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));	
		params.put(dm.expirationDateKey(), java.sql.Date.valueOf(expirationDate));	
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.cgvKey(), cgv);
		params.put(dm.descriptionKey(), description);
		params.put(dm.notesKey(), notes);
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.sellerIdKey(), seller.id());
		
		Team team = module().sellers().get(seller.id()).team();
		params.put(dm.teamIdKey(), team.id());
		
		params.put(dm.livraisonDelayDateKey(), livraisonDelayInDays);
		
		ds.set(params);		
	}

	@Override
	public Invoices invoices() throws IOException {
		return module().invoices().of(this);
	}

	@Override
	public void markEntirelyInvoiced() throws IOException {
		ds.set(dm.statusIdKey(), PurchaseOrderStatus.ENTIRELY_INVOICED.id());
	}
	
	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public LocalDate soldDate() throws IOException {
		java.sql.Date date = ds.get(dm.soldDateKey());
		
		if(date == null)
			return null;
		
		return date.toLocalDate();
	}

	@Override
	public int livraisonDelayInDays() throws IOException {
		return ds.get(dm.livraisonDelayDateKey());
	}

	@Override
	public LocalDate livraisonDate() throws IOException {
		java.sql.Date date = ds.get(dm.livraisonDateKey());
		
		if(date == null)
			return null;
		
		return date.toLocalDate();
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		UUID pdvId = ds.get(dm.modulePdvIdKey());
		return module().modulePdvs().get(pdvId);
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		return new OrderTaxesImpl(products().all());
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public double amountInvoiced() throws IOException {
		double amount = 0;
		
		if(cashReceipt().isNone()){
			for (Invoice inv : invoices().all()) {
				if(inv.status() != InvoiceStatus.DRAFT && inv.status() != InvoiceStatus.GET_RID)
					amount += inv.saleAmount().totalAmountTtc();
			}
		}else{
			amount = cashReceipt().affectedPaidAmount();
		}
			
		return amount;
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		double amountHt = ds.get(dm.totalAmountHtKey());
		double reduceAmount = ds.get(dm.reduceAmountKey());
		double netCommercial = ds.get(dm.netCommercialKey());
		double taxAmount = ds.get(dm.totalTaxAmountKey());
		double amountTtc = ds.get(dm.totalAmountTtcKey());
		
		return new OrderSaleAmount(amountHt, reduceAmount, netCommercial, taxAmount, amountTtc);
	}

	@Override
	public String title() throws IOException {
		if(status() == PurchaseOrderStatus.NONE)
			return String.format("Devis");
		else
			return String.format("Devis N° %s", reference());
	}

	@Override
	public double leftAmountToInvoice() throws IOException {
		return saleAmount().totalAmountTtc() - amountInvoiced();
	}

	@Override
	public PurchaseOrderReceipt cash(LocalDate paymentDate, String object, double receivedAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {	
		
		if(paymentDate == null)
			paymentDate = LocalDate.now();
		
		if(status() == PurchaseOrderStatus.CREATED)
			markSold(paymentDate, true);
		
		PurchaseOrderReceipt receipt = module().payments().of(this).of(modulePdv()).cashPurchaseOrder(paymentDate, object, receivedAmount, mode, transactionReference, cashier);
		receipt.validate();
		
		markEntirelyInvoiced();		
		markPaid();
		
		return receipt;
	}

	@Override
	public PurchaseOrderReceipt cashReceipt() throws IOException {
		Payment cashReceipt;
		
		Payments payments = module().payments().of(this);
		if(payments.count() == 0)
			cashReceipt = new PurchaseOrderReceiptNone();
		else
			cashReceipt = (PurchaseOrderReceipt)payments.all().get(0);
			
		return (PurchaseOrderReceipt)cashReceipt;
	}

	@Override
	public void markPaid() throws IOException {
		if(status() == PurchaseOrderStatus.ENTIRELY_INVOICED){
			// vérifier que toutes les factures sont classées payées
			boolean allPaid;
			if(cashReceipt().isNone()){
				allPaid = invoices().all().stream().allMatch(m -> {
					try {
						return m.status() == InvoiceStatus.PAID;
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}else{
				allPaid = true;
			}
			
			
			if(allPaid)
				ds.set(dm.statusIdKey(), PurchaseOrderStatus.PAID.id());
		}
	}

	@Override
	public void changeCustomer(Contact contact) throws IOException {
		
		if(contact.isNone())
			throw new IllegalArgumentException("Vous devez indiquer une personne !");
		
		if(status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez changer que le client d'une commande en mode de création !");
		
		ds.set(dm.customerIdKey(), contact.id());
	}

	@Override
	public void changeSeller(Contact seller) throws IOException {
		
		if(!module().sellers().contains(seller))
			throw new IllegalArgumentException("Vous devez indiquer un vendeur !");
		
		if(status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez changer que le vendeur d'une commande en mode de création !");
		
		ds.set(dm.sellerIdKey(), seller.id());
	}

	@Override
	public double paidAmount() throws IOException {
		double amount = 0;
		
		for (Invoice invoice : invoices().all()) {
			if(invoice.type() == InvoiceType.FACTURE_DOIT)
				amount += invoice.totalAmountPaid();
		}
		
		amount += cashReceipt().paidAmount();
		return amount;
	}

	@Override
	public double returnAmount() throws IOException {
		double amount = 0;
		
		for (Invoice invoice : invoices().all()) {
			if(invoice.type() == InvoiceType.FACTURE_DOIT)
				amount += invoice.totalAmountRembourse();	
			
			if(invoice.type() == InvoiceType.FACTURE_AVOIR)
				amount += invoice.totalAmountPaid();
		}
		
		return amount;
	}

	@Override
	public double realPaidAmount() throws IOException {
		return paidAmount() - returnAmount();
	}

	@Override
	public Team team() throws IOException {
		UUID teamId = ds.get(dm.teamIdKey());
		return new TeamDb(base, teamId, module);
	}
}
