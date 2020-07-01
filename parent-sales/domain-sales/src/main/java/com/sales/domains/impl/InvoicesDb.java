package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.formular.Formular;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceNature;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceStep;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategoryType;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.Tax;

public final class InvoicesDb extends GuidKeyAdvancedQueryableDb<Invoice, InvoiceMetadata> implements Invoices {

	private final transient Sales module;
	private final transient PurchaseOrder purchaseOrder;
	private final transient ModulePdv modulePdv;
	private final transient InvoiceStatus status;
	private final transient InvoiceStep step;
	private final transient InvoiceType type;
	private final transient PaymentConditionStatus paymentCondition;
	private final transient Contact customer;
	private final transient Contact seller;
	private final transient Invoice originInvoice;
		
	public InvoicesDb(final Base base, final Sales module, final ModulePdv modulePdv, final InvoiceStatus status, final InvoiceStep step, final InvoiceType type, final PurchaseOrder purchaseOrder, final PaymentConditionStatus paymentCondition, final Contact customer, final Contact seller, final Invoice originInvoice){
		super(base, "Facture introuvable !");	
		this.module = module;
		this.modulePdv = modulePdv;
		this.status = status;
		this.step = step;
		this.type = type;
		this.purchaseOrder = purchaseOrder;
		this.paymentCondition = paymentCondition;
		this.customer = customer;
		this.seller = seller;
		this.originInvoice = originInvoice;
	}
	
	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		String statement = String.format("%s inv "
				+ "WHERE (inv.%s ILIKE ? OR inv.%s ILIKE ? OR inv.%s ILIKE ?) AND inv.%s=? ",
				dm.domainName(), 
				dm.referenceKey(), dm.descriptionKey(), dm.originKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!modulePdv.isNone()){
			statement = String.format("%s AND inv.%s=?", statement, dm.modulePdvIdKey());
			params.add(modulePdv.id());
		}
		
		if(status != InvoiceStatus.NONE){
			statement = String.format("%s AND inv.%s=?", statement, dm.statusKey());
			params.add(status.id());
		}
		
		if(step != InvoiceStep.NONE){
			statement = String.format("%s AND inv.%s=?", statement, dm.stepKey());
			params.add(step.id());
		}
		
		if(type != InvoiceType.NONE){
			statement = String.format("%s AND inv.%s=?", statement, dm.invoiceTypeIdKey());
			params.add(type.id());
		}
		
		if(!purchaseOrder.isNone()){
			statement = String.format("%s AND inv.%s=?", statement, dm.purchaseOrderIdKey());
			params.add(purchaseOrder.id());
		}
		
		if(paymentCondition != PaymentConditionStatus.NONE){
			statement = String.format("%s AND inv.%s=?", statement, dm.paymentConditionIdKey());
			params.add(paymentCondition.id());
		}
		
		if(!customer.isNone()){
			statement = String.format("%s AND inv.%s=?", statement, dm.customerIdKey());
			params.add(customer.id());
		}
		
		if(!seller.isNone()){
			statement = String.format("%s AND inv.%s=?", statement, dm.sellerIdKey());
			params.add(seller.id());
		}
		
		if(!originInvoice.isNone()){
			statement = String.format("%s AND inv.%s=?", statement, dm.originInvoiceIdKey());
			params.add(originInvoice.id());
		}
		
		String orderClause;	
		HorodateMetadata hm = HorodateImpl.dm();
		
		if(!purchaseOrder.isNone())
			orderClause = String.format("ORDER BY inv.%s ASC", hm.dateCreatedKey());	
		else
			orderClause = String.format("ORDER BY inv.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("inv.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}	
	
	private Invoice add(InvoiceNature nature, LocalDate date, String origin, String description, String notes, PaymentConditionStatus paymentCondition, ModulePdv modulePdv, Contact customer, Contact seller) throws IOException {
		
		if (seller.isNone())
            throw new IllegalArgumentException("Vous devez indiquer un vendeur !");
		
		if(!module.sellers().contains(seller))
			throw new IllegalArgumentException("Vous devez être un vendeur pour effectuer cette action !");
		
		if(nature == InvoiceNature.NONE)
			throw new IllegalArgumentException("Vous devez indiquer la nature de la facture !");
		
		if (date == null)
            throw new IllegalArgumentException("La date d'émission ne peut pas être vide !");
		
		if(paymentCondition == PaymentConditionStatus.NONE)
			throw new IllegalArgumentException("Les conditions de règlement doivent être spécifiées !");
		
		if (customer.isNone() || customer.equals(module.contacts().defaultPerson()))
			throw new IllegalArgumentException("Vous devez spécifier un client !");		
		
		if(modulePdv.isNone())
			throw new IllegalArgumentException("Vous devez spécifier le module de vente !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.orderDateKey(), java.sql.Date.valueOf(date));				
		params.put(dm.originKey(), origin);
		params.put(dm.descriptionKey(), description);
		params.put(dm.notesKey(), notes);
		params.put(dm.purchaseOrderIdKey(), purchaseOrder.id());
		params.put(dm.referenceKey(), "Brouillon");
		params.put(dm.statusKey(), InvoiceStatus.DRAFT.id());
		params.put(dm.natureIdKey(), nature.id());
		params.put(dm.invoiceTypeIdKey(), nature.type().id());
		params.put(dm.stepKey(), InvoiceStep.CREATING.id());
		params.put(dm.customerIdKey(), customer.id());
		
		Team team = module.sellers().get(seller.id()).team();
		params.put(dm.teamIdKey(), team.id());
		
		params.put(dm.sellerIdKey(), seller.id());
		params.put(dm.moduleIdKey(), module.id());
		params.put(dm.modulePdvIdKey(), modulePdv.id());
		params.put(dm.originInvoiceIdKey(), originInvoice.id());		
		LocalDate dueDate = date.plusDays(paymentCondition.numberOfDays());
		params.put(dm.dueDateKey(), java.sql.Date.valueOf(dueDate));
		params.put(dm.paymentConditionIdKey(), paymentCondition.id());		
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}
	
	@Override
	public Invoice addFinalInvoice(LocalDate date, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException {
		
		ModulePdv modulePdv;
		
		if(!purchaseOrder.isNone()){
			
			if(purchaseOrder.status() != PurchaseOrderStatus.COMMANDE_CLIENT)
				throw new IllegalArgumentException("Vous ne pouvez pas créer de facture pour cette commande !");
			
			if(StringUtils.isBlank(description))
				description = String.format("Facture définitive selon %s", purchaseOrder.title());
			
			if(StringUtils.isBlank(origin))
				origin = purchaseOrder.reference();
			
			paymentCondition = purchaseOrder.paymentCondition();
			modulePdv = purchaseOrder.modulePdv();
			customer = purchaseOrder.customer();
			seller = purchaseOrder.seller();
		}else {
			modulePdv = module.modulePointDeVenteDirecte();
		}
		
		Invoice invoice = add(InvoiceNature.FINAL_INVOICE, date, origin, description, notes, paymentCondition, modulePdv, customer, seller);
		
		if(!purchaseOrder.isNone()) // générer les lignes à partir des articles du bon de commande
	    {
			for (OrderProduct product : purchaseOrder.products().all()) {
				invoice.products().add(product);
			}
			
			// ajouter les lignes acomptes validés
			for (Invoice otherInvoice : purchaseOrder.invoices().all()) {
				if(otherInvoice.nature() == InvoiceNature.DOWN_PAYMENT && (otherInvoice.status() == InvoiceStatus.OPENED || otherInvoice.status() == InvoiceStatus.PAID)){
					Product downPaymentProduct = module.products().getDownPaymentProduct();
					invoice.products().add(downPaymentProduct.category(), downPaymentProduct, downPaymentProduct.name(), otherInvoice.title(), -1, otherInvoice.saleAmount().totalAmountHt(), new RemiseNone(), otherInvoice.taxes().all().stream().map(m -> m).collect(Collectors.toList()));
				}
			}			
		}
				
		return invoice;
	}
	
	@Override
	public Invoices of(ModulePdv modulePdv) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices of(InvoiceStep step) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices of(InvoiceType type) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices of(InvoiceStatus status) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices of(PurchaseOrder purchaseOrder) throws IOException {
		
		if(!purchaseOrder.isNone())
			return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, purchaseOrder.customer(), purchaseOrder.seller(), originInvoice);
		else
			return new InvoicesDb(base, module, modulePdv, status, step, type, new PurchaseOrderNone(), paymentCondition, new CustomerNone(), new SellerNone(), originInvoice);
	}

	@Override
	public Invoice addDownPayment(LocalDate date, double amount, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException {
		
		ModulePdv modulePdv;
		
		if(!purchaseOrder.isNone()){
			if(StringUtils.isBlank(description))
				description = String.format("Acompte sur %s", purchaseOrder.title());
			
			if(StringUtils.isBlank(origin))
				origin = purchaseOrder.reference();
			
			paymentCondition = purchaseOrder.paymentCondition();
			modulePdv = purchaseOrder.modulePdv();
			customer = purchaseOrder.customer();
			seller = purchaseOrder.seller();
		}else{
			modulePdv = module.modulePointDeVenteDirecte();
		}
				
		Invoice invoice = add(InvoiceNature.DOWN_PAYMENT, date, origin, description, notes, paymentCondition, modulePdv, customer, seller);
		Product downPaymentProduct = module.products().getDownPaymentProduct();
		
		invoice.products().add(downPaymentProduct.category(), downPaymentProduct, downPaymentProduct.name(), description, 1, amount, new RemiseNone(), Arrays.asList(), new ProductNone());
		
		return invoice;
	}

	@Override
	public Invoice addDownPayment(LocalDate date, int percent, double base, String origin, String description, String notes, PaymentConditionStatus paymentCondition, Contact customer, Contact seller) throws IOException {
		
		if(percent > 100 || percent <= 0)
			throw new IllegalArgumentException("Vous devez spécifier un nombre compris entre 1 et 100 !");
		
		Formular formular = module.company().currency().calculator().withExpression("{base} * {percent}")
																  .withParam("{base}", base)
																  .withParam("{percent}", percent / 100.0);
		
		return addDownPayment(date, formular.result(), origin, description, notes, paymentCondition, customer, seller);
	}

	@Override
	public Invoices of(PaymentConditionStatus paymentCondition) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices ofCustomer(Contact customer) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public Invoices ofSeller(Contact seller) throws IOException {
		return new InvoicesDb(base, module, modulePdv, status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	public void delete(Invoice item) throws IOException {
		if(contains(item)){
			
			if(item.status() != InvoiceStatus.DRAFT)
				throw new IllegalArgumentException("Cette facture ne peut être supprimée !");
			
			ds.delete(item.id());
		}
	}

	@Override
	public Invoice addRemise(LocalDate date, double amount) throws IOException {
			
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.nature() == InvoiceNature.DOWN_PAYMENT)
			throw new IllegalArgumentException("Vous ne pouvez pas faire de remise sur une facture d'acompte !");
		
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Remise sur %s", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.REMISE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getDefaultRemiseProduct();
		
		for (OrderProduct op : originInvoice.products().all()) {
			
			if(!ProductCategoryType.isExploitationProduct(op.category().type()))
				continue;
			
			if(op.saleAmount().netCommercial() < amount)
				throw new IllegalArgumentException(String.format("La réduction ne peut être appliquée sur l'article %s (net commercial < montant remise) !", op.name()));
				
			String descriptionArticle = String.format("Remise sur %s", op.name());
			List<Tax> taxes = op.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			
			invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), descriptionArticle, 1, amount, new RemiseNone(), taxes, op.product());
		}		
		
		return invoice;
	}

	@Override
	public Invoice addRemise(LocalDate date, int percent) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.nature() == InvoiceNature.DOWN_PAYMENT)
			throw new IllegalArgumentException("Vous ne pouvez pas faire de remise sur une facture d'acompte !");
		
		if(percent > 100 || percent <= 0)
			throw new IllegalArgumentException("Vous devez spécifier un nombre compris entre 1 et 100 !");		
				
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Remise de %s sur %s", Integer.toString(percent) + " %", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.REMISE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getDefaultRemiseProduct();
		Formular formular = module.company().currency().calculator().withExpression("{base} * {percent}")
										  .withParam("{percent}", percent / 100.0);
		
		for (OrderProduct op : originInvoice.products().all()) {
			
			if(!ProductCategoryType.isExploitationProduct(op.category().type()))
				continue;
			
			formular = formular.withParam("{base}", op.saleAmount().totalAmountHt());			
			double amount = formular.result();
				
			String descriptionArticle = String.format("Remise sur %s", op.name());
			List<Tax> taxes = op.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			
			invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), descriptionArticle, 1, amount, new RemiseNone(), taxes, op.product());
		}
				
		return invoice;
	}

	@Override
	public Invoice addRistourne(LocalDate date, double amount) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.nature() == InvoiceNature.DOWN_PAYMENT)
			throw new IllegalArgumentException("Vous ne pouvez pas faire de ristourne sur une facture d'acompte !");
		
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Ristourne sur %s", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.RISTOURNE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getDefaultRistourneProduct();
		
		for (OrderProduct op : originInvoice.products().all()) {
			
			if(!ProductCategoryType.isExploitationProduct(op.category().type()))
				continue;
			
			if(op.saleAmount().netCommercial() < amount)
				throw new IllegalArgumentException(String.format("La réduction ne peut être appliquée sur l'article %s (net commercial < montant remise) !", op.name()));
				
			String descriptionArticle = String.format("Ristourne sur %s", op.name());
			List<Tax> taxes = op.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			
			invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), descriptionArticle, 1, amount, new RemiseNone(), taxes, op.product());
		}		
		
		return invoice;
	}

	@Override
	public Invoice addRistourne(LocalDate date, int percent) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.nature() == InvoiceNature.DOWN_PAYMENT)
			throw new IllegalArgumentException("Vous ne pouvez pas faire de ristourne sur une facture d'acompte !");
		
		if(percent > 100 || percent <= 0)
			throw new IllegalArgumentException("Vous devez spécifier un nombre compris entre 1 et 100 !");		
				
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Ristourne de %s sur %s", Integer.toString(percent) + " %", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.RISTOURNE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getDefaultRistourneProduct();
		Formular formular = module.company().currency().calculator().withExpression("{base} * {percent}")
										  .withParam("{percent}", percent / 100.0);
		
		for (OrderProduct op : originInvoice.products().all()) {
			
			if(!ProductCategoryType.isExploitationProduct(op.category().type()))
				continue;
			
			formular = formular.withParam("{base}", op.saleAmount().totalAmountHt());			
			double amount = formular.result();
				
			String descriptionArticle = String.format("Ristourne sur %s", op.name());
			List<Tax> taxes = op.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			
			invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), descriptionArticle, 1, amount, new RemiseNone(), taxes, op.product());
		}
				
		return invoice;
	}

	@Override
	public Invoice addRabais(LocalDate date) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.nature() == InvoiceNature.DOWN_PAYMENT)
			throw new IllegalArgumentException("Vous ne pouvez pas faire de rabais sur une facture d'acompte !");
		
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Rabais sur %s", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.RABAIS, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getDefaultRabaisProduct();
		
		for (OrderProduct op : originInvoice.products().all()) {
			
			if(!ProductCategoryType.isExploitationProduct(op.category().type()))
				continue;
			
			String descriptionArticle = String.format("Rabais sur %s", op.name());
			List<Tax> taxes = op.taxes().all().stream().map(m -> m).collect(Collectors.toList());
			
			invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), descriptionArticle, op.quantity(), op.unitPrice(), op.remise(), taxes, op.product());
		}		
		
		return invoice;
	}

	@Override
	public Invoice addEscompte(LocalDate date, int percent) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Escompte de %s sur %s", Integer.toString(percent) + " %", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.ESCOMPTE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());
		Product reductionProduct = module.products().getEscompteProduct();
		
		Formular formular = module.company().currency().calculator().withExpression("{base} * {percent}")
										  .withParam("{percent}", percent / 100.0)
										  .withParam("{base}", originInvoice.saleAmount().netCommercial());
										
		invoice.products().add(reductionProduct.category(), reductionProduct, reductionProduct.name(), description, 1, formular.result(), new RemiseNone(), Arrays.asList());		
		
		return invoice;
	}

	@Override
	public Invoice addCancelInvoice(LocalDate date) throws IOException {
		
		if(originInvoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture Doit corrigée par cet Avoir !");
		
		if(originInvoice.status() == InvoiceStatus.OPENED && originInvoice.totalAmountPaid() > 0)
			throw new IllegalArgumentException("Vous ne pouvez pas annuler une facture partiellement payée !");
		
		if(originInvoice.status() == InvoiceStatus.PAID)
			throw new IllegalArgumentException("Vous ne pouvez pas annuler une facture classée payée !");
		
		String origin = originInvoice.reference();
		String notes = originInvoice.notes();
		String description = String.format("Annulation de la %s", originInvoice.title());
		
		Invoice invoice = add(InvoiceNature.ANNULATION_FACTURE, date, origin, description, notes, originInvoice.paymentCondition(), originInvoice.modulePdv(), originInvoice.customer(), originInvoice.seller());

		for (OrderProduct op : originInvoice.products().all()) {
			invoice.products().add(op);
		}		
		
		return invoice;
	}

	@Override
	public Invoices of(Invoice originInvoice) throws IOException {
		
		if(originInvoice.isNone())
			return new InvoicesDb(base, module, new ModulePdvNone(), status, step, type, purchaseOrder, paymentCondition, new CustomerNone(), new SellerNone(), new InvoiceNone());
		else
			return new InvoicesDb(base, module, originInvoice.modulePdv(), status, step, type, new PurchaseOrderNone(), paymentCondition, originInvoice.customer(), originInvoice.seller(), originInvoice);
	}

	@Override
	public Invoices ofVenteDirecte() throws IOException {
		return new InvoicesDb(base, module, module.modulePointDeVenteDirecte(), status, step, type, purchaseOrder, paymentCondition, customer, seller, originInvoice);
	}

	@Override
	protected Invoice newOne(UUID id) {
		return new InvoiceDb(base, id, module);
	}

	@Override
	public Invoice none() {
		return new InvoiceNone();
	}
}
