package com.sales.domains.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.DomainMetadata;
import com.infrastructure.core.GuidKeyEntityBase;
import com.infrastructure.core.UseCode;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.CustomerMetadata;
import com.sales.domains.api.Customers;
import com.sales.domains.api.IntervalPricingMetadata;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceStep;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.ModulePdvs;
import com.sales.domains.api.OrderProductMetadata;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Payments;
import com.sales.domains.api.PricingMetadata;
import com.sales.domains.api.ProductCategories;
import com.sales.domains.api.ProductCategoryMetadata;
import com.sales.domains.api.ProductMetadata;
import com.sales.domains.api.ProductTaxMetadata;
import com.sales.domains.api.Products;
import com.sales.domains.api.ProvisionMetadata;
import com.sales.domains.api.Provisions;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.RRRSettingMetadata;
import com.sales.domains.api.Sales;
import com.sales.domains.api.SalesInterfacage;
import com.sales.domains.api.SellerMetadata;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Team;
import com.sales.domains.api.TeamMetadata;
import com.sales.domains.api.Teams;
import com.sales.interfacage.compta.api.TeamStockInterfaceMetadata;
import com.securities.api.Company;
import com.securities.api.Contacts;
import com.securities.api.Currency;
import com.securities.api.Feature;
import com.securities.api.FeatureSubscribed;
import com.securities.api.Features;
import com.securities.api.Indicators;
import com.securities.api.Log;
import com.securities.api.Membership;
import com.securities.api.MesureUnitType;
import com.securities.api.MesureUnits;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.api.NumberValueType;
import com.securities.api.PaymentModeStatus;
import com.securities.api.PaymentModeType;
import com.securities.api.PaymentModes;
import com.securities.api.Sequence;
import com.securities.api.TaxType;
import com.securities.api.Taxes;
import com.securities.api.Sequence.SequenceReserved;
import com.securities.api.Sequences;
import com.sales.domains.api.RRRSettings;
import com.sales.domains.api.Remise;

public final class SalesDb extends GuidKeyEntityBase<Sales> implements Sales {

	private final transient Base base;
	private final transient Module origin;
	
	public SalesDb(final Base base, final Module module){
		super(module.id());
		this.base = base;
		this.origin = module;
	}
	
	@Override
	public Products products() {
		return new ProductsDb(this.base, this, new ProductCategoryNone(), UseCode.NONE, StringUtils.EMPTY);
	}

	@Override
	public MesureUnits mesureUnits() throws IOException {
		return origin.company().moduleAdmin().mesureUnits();
	}

	@Override
	public Taxes taxes() throws IOException {
		return origin.company().moduleAdmin().taxes();
	}

	@Override
	public Company company() throws IOException {
		return origin.company();
	}

	@Override
	public Invoices invoices() {
		return new InvoicesDb(base, this, new ModulePdvNone(), InvoiceStatus.NONE, InvoiceStep.NONE, InvoiceType.NONE, new PurchaseOrderNone(), PaymentConditionStatus.NONE, new CustomerNone(), new SellerNone(), new InvoiceNone());
	}

	@Override
	public Membership membership() throws IOException {
		return company().moduleAdmin().membership();
	}

	@Override
	public Customers customers() {
		return new CustomersDb(base, this, UseCode.NONE);
	}

	@Override
	public Payments payments() {
		return new PaymentsDb(base, this, new InvoiceNone(), new ProvisionNone(), PaymentType.NONE, PaymentStatus.NONE, new ModulePdvNone(), new CustomerNone(), new PurchaseOrderNone());
	}

	@Override
	public String description() throws IOException {
		return origin.description();
	}

	@Override
	public Module install() throws IOException {
		
		Module module = origin.install();
		
		// 1 - créer les taxes
		Taxes taxes = taxes();
		
		if(taxes.find("Taxe sur la Valeur Ajoutée").isEmpty())
			taxes.add(TaxType.TVA_GROUP, "Taxe sur la Valeur Ajoutée", "TVA", 18, NumberValueType.PERCENT);
		
		// 2 - créer les modes de paiement par défaut
		PaymentModes paymentModes = paymentModes();
		
		if(paymentModes.find("Espèces").isEmpty())
			paymentModes.add("Espèces", PaymentModeType.CASH);
		
		if(paymentModes.find("Carte bancaire").isEmpty())
			paymentModes.add("Carte bancaire", PaymentModeType.BANK);
		
		if(paymentModes.find("Chèques").isEmpty())
			paymentModes.add("Chèques", PaymentModeType.BANK);
		
		if(paymentModes.find("Virement bancaire").isEmpty())
			paymentModes.add("Virement bancaire", PaymentModeType.BANK);
		
		// 3 - créer les unités de mesures par défaut
		MesureUnits mesureUnits = mesureUnits();
		
		if(mesureUnits.find("Article").isEmpty())
			mesureUnits.add("Art.", "Article", MesureUnitType.QUANTITY);
		
		// 4 - créer une équipe commerciale
		Teams teams = teams();
		Team team = teams.add("Equipe A", "EA");
		team.members().add(company().moduleAdmin().membership().defaultUser());		
		
		// 5 - créer les séquences
		Sequences sequences = company().moduleAdmin().sequences();
		sequences.reserved(SequenceReserved.PURCHASE_ORDER);
		sequences.reserved(SequenceReserved.INVOICE);
		sequences.reserved(SequenceReserved.PAYMENT);
		sequences.reserved(SequenceReserved.PROVISION);
		
		// 6 - générer les catégories de produits et les produits
		products().getDefaultRabaisProduct();
		products().getDefaultRemiseProduct();
		products().getDefaultRistourneProduct();
		products().getDownPaymentProduct();
		products().getEscompteProduct();

		return new SalesDb(base, module);
	}

	@Override
	public boolean isInstalled() {
		return origin.isInstalled();
	}

	@Override
	public boolean isSubscribed() {
		return origin.isSubscribed();
	}

	@Override
	public String name() throws IOException {
		return origin.name();
	}

	@Override
	public int order() throws IOException {
		return origin.order();
	}

	@Override
	public String shortName() throws IOException {
		return origin.shortName();
	}

	@Override
	public ModuleType type() throws IOException {
		return origin.type();
	}

	@Override
	public Module uninstall() throws IOException {
				
		// vérifier que les modules dépendants sont installés
		// 1 - module point de vente
		if(!company().modulesInstalled().of(ModuleType.PDV).isEmpty())
			throw new IllegalArgumentException("Le module Point de vente doit être désinstallé avant de continuer l'action !");
		
		// 1 - module hôtel
		if(!company().modulesInstalled().of(ModuleType.HOTEL).isEmpty())
			throw new IllegalArgumentException("Le module Hôtel doit être désinstallé avant de continuer l'action !");
		
		List<DomainMetadata> domains = 
				Arrays.asList(
					PaymentMetadata.create(),
					ProvisionMetadata.create(),										
					OrderProductTaxMetadata.create(),
					OrderProductMetadata.create(),
					InvoiceMetadata.create(),
					PurchaseOrderMetadata.create(),
					CustomerMetadata.create(),
					IntervalPricingMetadata.create(),
					PricingMetadata.create(),
					ProductTaxMetadata.create(),
					ProductMetadata.create(),
					RRRSettingMetadata.create(),
					ProductCategoryMetadata.create(),
					SellerMetadata.create(),
					TeamStockInterfaceMetadata.create(),
					TeamMetadata.create()
				);		
		
		for (DomainMetadata domainMetadata : domains) {
			base.deleteAll(domainMetadata); 
		}
		
		// supprimer les séquences
		Sequences sequences = company().moduleAdmin().sequences();
		Sequence purchaseSequence = sequences.reserved(SequenceReserved.PURCHASE_ORDER);
		Sequence invoiceSequence = sequences.reserved(SequenceReserved.INVOICE);
		Sequence paymentSequence = sequences.reserved(SequenceReserved.PAYMENT);
		Sequence provisionSequence = sequences.reserved(SequenceReserved.PROVISION);
		
		try {
			sequences.delete(purchaseSequence); 
		} catch (Exception ignore) {}
		
		try {
			sequences.delete(invoiceSequence); 
		} catch (Exception ignore) {}
		
		try {
			sequences.delete(paymentSequence); 
		} catch (Exception ignore) {}
		
		try {
			sequences.delete(provisionSequence); 
		} catch (Exception ignore) {}
		
		// finaliser
		Module module = origin.uninstall();
		
		return new SalesDb(base, module);
	}

	@Override
	public void activate(boolean active) throws IOException {
		origin.activate(active);
	}

	@Override
	public boolean isActive() {
		return origin.isActive();
	}

	@Override
	public Features featuresProposed() throws IOException {
		return origin.featuresProposed();
	}

	@Override
	public Teams teams() {
		return new TeamsDb(base, this);
	}

	@Override
	public Sellers sellers() {
		return new SellersDb(base, this, UseCode.NONE, new TeamNone());
	}

	@Override
	public PurchaseOrders purchases() {
		return new PurchaseOrdersDb(base, this, new ModulePdvNone(), PurchaseOrderStatus.NONE);
	}

	@Override
	public ProductCategories productCategories() {
		return new ProductCategoriesDb(base, this, UseCode.NONE, StringUtils.EMPTY);
	}

	@Override
	public PaymentModes paymentModes() throws IOException {
		return company().moduleAdmin().paymentModes().of(PaymentModeStatus.ENABLED);
	}

	@Override
	public ModulePdvs modulePdvs() throws IOException {
		return new ModulePdvsDb(base, this);
	}

	@Override
	public ModulePdv modulePointDeVenteDirecte() {
		return new ModuleVenteDirecte(base, this);
	}

	@Override
	public Contacts contacts() throws IOException {
		return company().moduleAdmin().contacts();
	}

	@Override
	public RRRSettings RRRSettings() throws IOException {
		return new RRRSettingsDb(base, this);
	}

	@Override
	public Remise remiseWithCurrency(double value, NumberValueType valueType) throws IOException {
		return remise(value, valueType, company().currency());
	}

	@Override
	public Remise remise(double value, NumberValueType valueType, Currency currency) throws IOException {
		return new RemiseImpl(value, valueType, currency);
	}

	@Override
	public Provisions provisions() {
		return new ProvisionsDb(base, this, new CustomerNone());
	}

	@Override
	public Features featuresAvailable() throws IOException {
		return origin.featuresAvailable();
	}

	@Override
	public Features featuresSubscribed() throws IOException {
		return origin.featuresSubscribed();
	}

	@Override
	public Indicators indicators() throws IOException {
		return origin.indicators();
	}

	@Override
	public Module subscribe() throws IOException {
		return origin.subscribe();
	}

	@Override
	public FeatureSubscribed subscribeTo(Feature feature) throws IOException {
		return origin.subscribeTo(feature);
	}

	@Override
	public Module unsubscribe() throws IOException {
		return origin.unsubscribe();
	}

	@Override
	public void unsubscribeTo(Feature feature) throws IOException {
		origin.unsubscribeTo(feature);
	}

	@Override
	public SalesInterfacage interfacage() {
		return new SalesInterfacageDb(base, this);
	}

	@Override
	public Log log() throws IOException {
		return origin.log();
	}
}
