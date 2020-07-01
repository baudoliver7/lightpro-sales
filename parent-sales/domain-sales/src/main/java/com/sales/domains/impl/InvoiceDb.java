package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Customer;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceNature;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceStep;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.sales.domains.api.InvoiceProducts;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.InvoiceRefundReceipt;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.SaleAmount;
import com.sales.domains.api.SaleTaxes;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Seller;
import com.sales.domains.api.Team;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.api.Sequence;
import com.securities.api.Sequence.SequenceReserved;

public final class InvoiceDb extends GuidKeyEntityDb<Invoice, InvoiceMetadata> implements Invoice {

	private final Sales module;
	
	public InvoiceDb(final Base base, final UUID id, final Sales module){
		super(base, id, "Facture introuvable !");
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
	public String notes() throws IOException {
		return ds.get(dm.notesKey());
	}

	@Override
	public Contact customer() throws IOException {
		UUID customerId = ds.get(dm.customerIdKey());
		return module().contacts().get(customerId);
	}

	@Override
	public InvoiceProducts products() throws IOException {
		return new InvoiceProductsImpl(this, new OrderProductsDb(base, this));
	}

	@Override
	public PurchaseOrder purchaseOrder() throws IOException {
		UUID orderId = ds.get(dm.purchaseOrderIdKey());
		
		if(orderId == null)
			return new PurchaseOrderNone();
		
		return new PurchaseOrderDb(base, orderId, module);
	}

	@Override
	public InvoiceStatus status() throws IOException {
		int statusId = ds.get(dm.statusKey());
		return InvoiceStatus.get(statusId);
	}

	@Override
	public double leftAmountToPay() throws IOException {
		return saleAmount().totalAmountTtc() - totalAmountPaid() + totalAmountRembourse();
		
	}

	@Override
	public double totalAmountPaid() throws IOException {
		
		double paidAmount = 0;
		InvoiceType type = type();
		
		if(type == InvoiceType.FACTURE_DOIT){
			for (Payment p : payments().all()) {
				if(p.status() == PaymentStatus.VALIDATED && p.type() == PaymentType.ENCAISSEMENT){
					paidAmount += p.affectedPaidAmount();
				}
			}
		}
		
		if(type == InvoiceType.FACTURE_AVOIR){
			for (Payment p : payments().all()) {
				if(p.status() == PaymentStatus.VALIDATED && p.type() == PaymentType.REMBOURSEMENT){
					paidAmount += p.paidAmount();
				}
			}
		}
		
		return paidAmount;
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
	public LocalDate dueDate() throws IOException {
		java.sql.Date date = ds.get(dm.dueDateKey());
		return date.toLocalDate();
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public String origin() throws IOException {
		return ds.get(dm.originKey());
	}

	@Override
	public PaymentConditionStatus paymentCondition() throws IOException {
		int paymentCondition = ds.get(dm.paymentConditionIdKey());
		return PaymentConditionStatus.get(paymentCondition);
	}

	@Override
	public void update(LocalDate date, PaymentConditionStatus paymentCondition, String origin, String description, String notes, Contact newCustomer, Contact newSeller) throws IOException {
		
		if(status() != InvoiceStatus.DRAFT)
			throw new IllegalArgumentException("Vous ne pouvez pas modifier cette facture !");
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		if(paymentCondition == PaymentConditionStatus.NONE)
			throw new IllegalArgumentException("Les conditions de règlement doivent être spécifiées !");
		
		if (newCustomer.isNone() || newCustomer.equals(module().contacts().defaultPerson()))
			throw new IllegalArgumentException("Vous devez spécifier un client !");
		
		if(newSeller.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un vendeur !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));	
		
		LocalDate dueDate = date.plusDays(paymentCondition.numberOfDays());
		params.put(dm.dueDateKey(), java.sql.Date.valueOf(dueDate));	
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());
		params.put(dm.descriptionKey(), description);
		params.put(dm.notesKey(), notes);
		params.put(dm.originKey(), origin);
		params.put(dm.customerIdKey(), newCustomer.id());
		params.put(dm.sellerIdKey(), newSeller.id());
		
		Team team = module().sellers().get(newSeller.id()).team();
		params.put(dm.teamIdKey(), team.id());
		
		ds.set(params);		
	}

	@Override
	public InvoiceType type() throws IOException {
		int typeId = ds.get(dm.invoiceTypeIdKey());
		return InvoiceType.get(typeId);
	}

	@Override
	public Payments payments() throws IOException {
		return module().payments().of(this);
	}

	private Sequence sequence() throws IOException{
		return module().company().moduleAdmin().sequences().reserved(SequenceReserved.INVOICE);
	}
	
	@Override
	public void validate() throws IOException {
		
		ds.set(dm.referenceKey(), sequence().generate());
		ds.set(dm.statusKey(), InvoiceStatus.OPENED.id());
		ds.set(dm.stepKey(), InvoiceStep.EMITTED.id());
		
		InvoiceNature nature = nature();
		InvoiceType type = nature.type();
		
		if(type == InvoiceType.FACTURE_DOIT){
			if(nature == InvoiceNature.FINAL_INVOICE && !purchaseOrder().isNone())
				purchaseOrder().markEntirelyInvoiced();
						
			Customer customer = module().customers().build(customer().id());
			if(customer.isNone()) // ajouter le contact comme un client
				module().customers().add(customer());
		}
		
		if(type == InvoiceType.FACTURE_AVOIR){
			if(nature == InvoiceNature.ANNULATION_FACTURE)
			{
				ds.set(dm.statusKey(), InvoiceStatus.GET_RID.id());
				originInvoice().getRid();				
			}			
		}
		
		ComptaInterface comptaInterface = module().interfacage().comptaInterface();		
		if(comptaInterface.available())
			comptaInterface.send(this, true);		
	}

	@Override
	public void markPaid() throws IOException {
		
		double solde = solde();
		
		if(solde > 0.01)
			throw new IllegalArgumentException(String.format("Il reste un reliquat de %s à payer ! Réglez le montant restant ou établissez un avoir sans remboursement (Abandonné).", module().company().currency().toString(solde))); 
		
		if(solde < 0 && -solde > 0.01)
			throw new IllegalArgumentException(String.format("Il existe un trop payé de %s sur la facture ! Etablissez un avoir avec remboursement ou converti en bon de réduction.", module().company().currency().toString(solde)));
		
		ds.set(dm.statusKey(), InvoiceStatus.PAID.id());
		
		// vérifier que le bon de commande d'origine est totalement réglé
		purchaseOrder().markPaid();		
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public Seller seller() throws IOException {
		UUID sellerId = ds.get(dm.sellerIdKey());
		return new SellerDb(base, sellerId, module);
	}

	@Override
	public InvoiceStep step() throws IOException {
		int stepId = ds.get(dm.stepKey());
		return InvoiceStep.get(stepId);
	}

	@Override
	public ModulePdv modulePdv() throws IOException {
		UUID pdvId = ds.get(dm.modulePdvIdKey());
		return module().modulePdvs().get(pdvId);
	}

	@Override
	public void changeStep(InvoiceStep step) throws IOException {
		ds.set(dm.stepKey(), step.id());
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		return new OrderTaxesImpl(products().all());
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
		String title;
		
		switch (nature()) {
		case FINAL_INVOICE:
			if(status() == InvoiceStatus.DRAFT)
				title = String.format("Facture en création");
			else
				title = String.format("Facture N° %s", reference());
			break;
		case DOWN_PAYMENT:			
			if(status() == InvoiceStatus.DRAFT)
				title = String.format("Facture d'acompte en création");
			else
				title = String.format("Facture d'acompte N° %s", reference());
			break;
		case REMISE:
		case RABAIS:	
		case RISTOURNE:	
		case ESCOMPTE:
		case ANNULATION_FACTURE:	
			if(status() == InvoiceStatus.DRAFT)
				title = String.format("Avoir en création");
			else
				title = String.format("Avoir N° %s", reference());
			break;
		default:
			title = StringUtils.EMPTY;
			break;
		}
		
		return title;
	}

	@Override
	public InvoiceNature nature() throws IOException {
		int natureId = ds.get(dm.natureIdKey());
		return InvoiceNature.get(natureId);
	}

	@Override
	public Invoice originInvoice() throws IOException {
		UUID originId = ds.get(dm.originInvoiceIdKey());
		return new InvoiceDb(base, originId, module);
	}

	@Override
	public void getRid() throws IOException {
		
		InvoiceStatus status = status();
		InvoiceType type = type();
		
		if(status != InvoiceStatus.OPENED)
			throw new IllegalArgumentException("Vous ne pouvez abandonner qu'une facture ouverte !");
		
		// vérifier pour une facture doit qu'elle possède un avoir d'annulation
		if(type == InvoiceType.FACTURE_DOIT){
			boolean gotAvoirAnnulation = false;
			for (Invoice avoir : avoirs().all()) {
				if(avoir.nature() == InvoiceNature.ANNULATION_FACTURE)
				{
					gotAvoirAnnulation = true;
					break;
				}
			}
			
			if(!gotAvoirAnnulation)
				throw new IllegalArgumentException("Vous ne pouvez abandonner qu'une facture Doit possédant un avoir d'annulation !");			
		}		
		
		if(totalAmountPaid() > 0)
			throw new IllegalArgumentException("Vous ne pouvez pas annuler une facture partiellement payée !");		
		
		ds.set(dm.statusKey(), InvoiceStatus.GET_RID.id());
	}

	@Override
	public Invoices avoirs() throws IOException {
		return module().invoices().of(this);
	}

	@Override
	public double avoirAmount() throws IOException {
		double amount = 0;
		
		if(type() == InvoiceType.FACTURE_DOIT){
			for (Invoice avoir : avoirs().all()) {
				InvoiceStatus status = avoir.status();
				if(status == InvoiceStatus.GET_RID || status == InvoiceStatus.OPENED)
					amount += avoir.leftAmountToPay();
			}
		}	
		
		return amount;
	}

	@Override
	public double totalAmountRembourse() throws IOException {
		double amount = 0;
		
		if(type() == InvoiceType.FACTURE_DOIT){
			for (Payment payment : payments().all()) {
				if(payment.type() == PaymentType.REMBOURSEMENT && payment.status() == PaymentStatus.VALIDATED)
					amount += payment.paidAmount();
			}
			
			for (Invoice avoir : avoirs().all()) {
				amount += avoir.totalAmountPaid();
			}
		}
		
		return amount;
	}

	@Override
	public double solde() throws IOException {
		return leftAmountToPay() - avoirAmount();
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		return payments().of(modulePdv()).cashInvoice(paymentDate, object, paidAmount, mode, transactionReference, cashier);
	}

	@Override
	public InvoiceReceipt cash(LocalDate paymentDate, String object, double amount, Provision provision, Contact cashier) throws IOException {
		return payments().cashInvoice(paymentDate, object, amount, provision, cashier);
	}

	@Override
	public InvoiceRefundReceipt refund(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		return payments().refundInvoice(paymentDate, object, paidAmount, mode, transactionReference, cashier);			
	}

	@Override
	public double realTotalAmountPaid() throws IOException {
		return totalAmountPaid() - totalAmountRembourse();
	}

	@Override
	public Team team() throws IOException {
		UUID teamId = ds.get(dm.teamIdKey());
		return new TeamDb(base, teamId, module);
	}	
}
