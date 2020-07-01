package com.sales.domains.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.compta.domains.api.Compta;
import com.compta.domains.api.Journal;
import com.compta.domains.api.Piece;
import com.compta.domains.api.PieceArticle;
import com.compta.domains.api.PieceStatus;
import com.compta.domains.api.PieceType;
import com.compta.domains.api.Tiers;
import com.compta.domains.api.Trame;
import com.compta.domains.impl.ComptaDb;
import com.compta.interfacage.sales.api.InvoiceInterface;
import com.compta.interfacage.sales.api.ModulePdvInterface;
import com.compta.interfacage.sales.api.PaymentInterface;
import com.compta.interfacage.sales.api.PaymentModeInterface;
import com.compta.interfacage.sales.api.PdvPaymentModeInterface;
import com.compta.interfacage.sales.api.ProductCategoryInterface;
import com.compta.interfacage.sales.api.ProductCategoryInterfaceMetadata;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceNature;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.InvoiceRefundReceipt;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceStep;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionStatus;
import com.sales.domains.api.ProvisionStep;
import com.sales.domains.api.PurchaseOrderReceipt;
import com.sales.domains.api.SaleTax;
import com.sales.domains.api.Sales;
import com.sales.interfacage.compta.api.ComptaInterface;
import com.securities.api.Module;
import com.securities.api.ModuleType;

public final class ComptaInterfaceDb implements ComptaInterface {

	private transient final Sales sales;
	private transient final Base base;
	private transient Compta compta;
	
	public ComptaInterfaceDb(final Base base, final Sales sales){
		
		this.sales = sales;
		this.base = base;
		
		initialize();
	}
	
	private void initialize()  {
		try {
			Module module;
			
			if(!sales.company().modulesInstalled().contains(ModuleType.COMPTA))
				module = sales.company().modulesProposed().get(ModuleType.COMPTA);
			else
				module = sales.company().modulesInstalled().get(ModuleType.COMPTA);
			
			compta = new ComptaDb(base, module);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkAvailability(){
		
		if(!available())
			throw new IllegalArgumentException("Le module de comptabilité n'est pas installé !");
	}
	
	private void sendFactureAvoir(Invoice invoice, boolean silentMode) throws IOException {
		sendInvoice(invoice, true, silentMode);
	}
	
	private void sendInvoiceDoit(Invoice invoice, boolean silentMode) throws IOException {
		sendInvoice(invoice, false, silentMode);									
	}
	
	private void sendInvoice(Invoice invoice, boolean factureAvoir, boolean silentMode) throws IOException {
		
		InvoiceInterface invoiceInterface = compta.interfacage().salesInterface().invoiceInterface();		
		ProductCategory acompteCategory = sales.productCategories().getAcompteCategory();
		
		// récupérer la liste des interfaces catégories
		Tiers tiers = compta.tiers().build(invoice.customer().id());
		Piece piece = compta.pieces().of(invoiceInterface.factureClient())
				.add(invoice.orderDate(), invoice.reference(), invoice.origin(), invoice.notes(), sales, invoice.dueDate(), tiers);
		
		for (OrderProduct orderProduct : invoice.products().all()) {
			
			// Sélectionner la catégorie de l'article
			ProductCategory category = orderProduct.product().category();
			ProductCategoryInterface categoryInterface = invoiceInterface.productCategories().get(category.id());
			
			final Journal journalVente;
			final Trame factureDoitTrame;
			
			if(category.equals(acompteCategory))
			{
				if(categoryInterface.journalVente().isNone() || categoryInterface.factureDoitTrame().isNone())					
				{
					throw new IllegalArgumentException("Comptabilité : veuillez configurer SVP la catégorie acompte !");
				}
				
				journalVente = categoryInterface.journalVente();			
				factureDoitTrame = categoryInterface.factureDoitTrame();
			}else{
				// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
				journalVente = !categoryInterface.journalVente().isNone() ? categoryInterface.journalVente() : invoiceInterface.journalVente();			
				factureDoitTrame = !categoryInterface.factureDoitTrame().isNone() ? categoryInterface.factureDoitTrame() : invoiceInterface.factureDoitTrame();
			}			
					
			// Collecte des paramètres du modèle
			Map<String, Double> params = new HashMap<String, Double>();
			
			// saisir le montant total ht comme montant de base
			params.put("{base}", Math.abs(orderProduct.saleAmount().totalAmountHt()));
			
			// ajouter les taxes
			for (SaleTax tax : orderProduct.taxes().all()) {
				params.put(String.format("{%s}", tax.shortName().toLowerCase()), tax.decimalValue());
				params.put(String.format("{%s-amount}", tax.shortName().toLowerCase()), tax.amount());
			}
			
			// générer l'article
			PieceArticle article = factureAvoir ?  factureDoitTrame.generateReverse(journalVente, params) : factureDoitTrame.generate(journalVente, params);
			piece.articles().add(article);
		}
		
		piece.validate();
		
		invoice.changeStep(InvoiceStep.SEND_TO_ACCOUNTING);		
	}	
	
	@Override
	public void send(Invoice invoice, boolean silentMode) throws IOException {
		
		try {
			checkAvailability();
			
			if(invoice.nature() == InvoiceNature.DOWN_PAYMENT)
			{
				throw new IllegalArgumentException("Les factures d'acompte ne sont pas comptabilisées !");
			}
			
			InvoiceInterface invoiceInterface = compta.interfacage().salesInterface().invoiceInterface();
			
			if(invoiceInterface.journalVente().isNone() || invoiceInterface.factureClient().isNone() || invoiceInterface.factureDoitTrame().isNone())
			{
				throw new IllegalArgumentException("Configuration par défaut de l'interfaçage des factures non réalisée  ! Veuillez, SVP, réaliser cette tâche avant de continuer !");
			}
				
			checkIfIsValidPiece(invoice.status() != InvoiceStatus.DRAFT);
			
			deleteIfAlreadyAccounted(invoice.reference(), invoice.step() == InvoiceStep.SEND_TO_ACCOUNTING);
			
			if(invoice.nature() == InvoiceNature.FINAL_INVOICE)
				sendInvoiceDoit(invoice, silentMode);
			
			if(invoice.type() == InvoiceType.FACTURE_AVOIR)
				sendFactureAvoir(invoice, silentMode);
			
		} catch (IllegalArgumentException e) {
			if(silentMode)
				sales.log().warning(e.getMessage());
			else
				throw e;	
		} catch(Exception e){
			if(silentMode)
				sales.log().error(e.getMessage(), ExceptionUtils.getStackTrace(e));
			else
				throw e;
		}				
	}
	
	@Override
	public void send(Payment payment, boolean silentMode) throws IOException {
		try {
			switch (payment.originType()) {
				case PURCHASE_ORDER:
					send((PurchaseOrderReceipt)payment, silentMode);				
					break;
				case INVOICE:
					if(payment.type() == PaymentType.ENCAISSEMENT)
						send((InvoiceReceipt)payment, silentMode);
					else
						send((InvoiceRefundReceipt)payment, silentMode);				
					break;
				case PROVISION:
					throw new IllegalArgumentException("Les paiements issus d'une provision ne sont pas traités automatiquement. Veuillez SVP, effectuer le traiement manuellement.");
				default:
					throw new IllegalArgumentException("Ce type de paiement n'est pas pris en charge !");
			}
		} catch (IllegalArgumentException e) {
			if(silentMode)
				sales.log().warning(e.getMessage());
			else
				throw e;	
		} catch(Exception e){
			if(silentMode)
				sales.log().error(e.getMessage(), ExceptionUtils.getStackTrace(e));
			else
				throw e;
		}						
	}

	@Override
	public void removeProductCategoryInterface(ProductCategory productCategory) throws IOException {
		
		checkAvailability();
		
		ProductCategoryInterfaceMetadata dm =  ProductCategoryInterfaceMetadata.create();
		String statement = String.format("DELETE FROM %s WHERE %s=?", dm.domainName(), dm.keyName());
		base.executeUpdate(statement, Arrays.asList(productCategory.id()));
	}

	@Override
	public void send(Provision provision, boolean silentMode) throws IOException {
		
		try {
			checkAvailability();
			
			checkIfIsValidPiece(provision.status() != ProvisionStatus.DRAFT);
			
			deleteIfAlreadyAccounted(provision.reference(), provision.step() == ProvisionStep.SEND_TO_ACCOUNTING);
			
			Journal journalEncaissement;
			Trame provisionTrame;
			PieceType pieceType;
			
			Tiers tiers = compta.tiers().build(provision.customer().id());
			
			Payment payment = provision.originPayment();
			if(payment.modulePdv().equals(sales.modulePointDeVenteDirecte())){
				PaymentInterface paymentInterface = compta.interfacage().salesInterface().paymentInterface();		
				
				// récupérer la liste des interfaces catégories 
				PaymentModeInterface paymentModeInterface = paymentInterface.paymentModes().get(payment.mode().id());
				// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
				journalEncaissement  = paymentModeInterface.journalEncaissement();	
				provisionTrame  = paymentModeInterface.provision();

				pieceType = paymentModeInterface.reglement();
			} else {
				ModulePdvInterface pdvInterface = compta.interfacage().salesInterface().pdvInterface().modulePdvInterfaces().get(payment.modulePdv().id());		
				
				// récupérer la liste des interfaces catégories
				PdvPaymentModeInterface paymentModeInterface = pdvInterface.paymentModes().get(payment.mode().id());
				// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
				journalEncaissement  = paymentModeInterface.journalEncaissement();			
				provisionTrame  = paymentModeInterface.provision();
				
				pieceType = paymentModeInterface.reglement();
			}
								
			if(journalEncaissement.isNone() || provisionTrame.isNone() || pieceType.isNone())
			{
				throw new IllegalArgumentException("Comptabilité : vous devez configurer l'interface du mode de paiement !");
			}
			
			Piece piece = compta.pieces().of(pieceType)
					.add(provision.provisionDate(), provision.reference(), payment.reference(), payment.object(), sales, null, tiers);
								
			// Collecte des paramètres du modèle
			Map<String, Double> params = new HashMap<String, Double>();
			
			// saisir le montant total ttc comme montant de base
			params.put("{base}", provision.amount());
			
			// générer l'article
			PieceArticle article = provisionTrame.generate(journalEncaissement, params);
			piece.articles().add(article);
			
			piece.validate();
			
			provision.changeStep(ProvisionStep.SEND_TO_ACCOUNTING);
		} catch (IllegalArgumentException e) {
			if(silentMode)
				sales.log().warning(e.getMessage());
			else
				throw e;
		} catch (Exception e) {
			if(silentMode)
				sales.log().error(e.getMessage(), ExceptionUtils.getStackTrace(e));
			else
				throw e;
		}
	}

	private void deleteIfAlreadyAccounted(String reference, boolean sent) throws IOException{
		if(sent) // Renvoi à la comptabilité
		{
			List<Piece> pieces = compta.pieces().of(sales).withReference(reference).all();
			
			if(!pieces.isEmpty())
			{
				if(pieces.get(0).status() == PieceStatus.ACCOUNTED)
				{
					throw new IllegalArgumentException("La pièce comptable a déjà été comptabilisée !");
				}
				else
					compta.pieces().delete(pieces.get(0));
			}
		}
	}
	
	private void checkIfIsValidPiece(boolean isValid){
		if(!isValid)
		{
			throw new IllegalArgumentException("Vous ne pouvez envoyer à la comptabilité que des paiements validés !");
		}
	}
	
	private void send(PurchaseOrderReceipt receipt, boolean silentMode) throws IOException {		

		checkAvailability();
		
		checkIfIsValidPiece(receipt.status() == PaymentStatus.VALIDATED);
		
		deleteIfAlreadyAccounted(receipt.reference(), receipt.step() == PaymentStep.SEND_TO_ACCOUNTING);
		
		Journal journalEncaissement;
		Trame reglementTrame;
		PieceType pieceType;
		
		Tiers tiers = compta.tiers().build(receipt.customer().id());		
					
		if(receipt.modulePdv().equals(sales.modulePointDeVenteDirecte())){
			PaymentInterface paymentInterface = compta.interfacage().salesInterface().paymentInterface();		
			
			// récupérer la liste des interfaces catégories 
			PaymentModeInterface paymentModeInterface = paymentInterface.paymentModes().get(receipt.mode().id());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();	
			
			reglementTrame  = paymentModeInterface.reglementDefinitif();			

			pieceType = paymentModeInterface.reglement();
		}else{
			ModulePdvInterface pdvInterface = compta.interfacage().salesInterface().pdvInterface().modulePdvInterfaces().get(receipt.modulePdv().id());		
			
			// récupérer la liste des interfaces catégories
			PdvPaymentModeInterface paymentModeInterface = pdvInterface.paymentModes().get(receipt.mode());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();
			
			reglementTrame  = paymentModeInterface.reglementDefinitif();
			
			pieceType = paymentModeInterface.reglement();
		}
		
		if(journalEncaissement.isNone() || reglementTrame.isNone() || pieceType.isNone())
		{
			throw new IllegalArgumentException("Comptabilité : vous devez configurer l'interface du mode de paiement !");
		}
		
		Piece piece = compta.pieces().of(pieceType)
				.add(receipt.paymentDate(), receipt.reference(), receipt.order().reference(), receipt.object(), sales, null, tiers);
						
		// enregistrer chaque ligne produit
		for (OrderProduct product : receipt.order().products().all()) {
			
			// Collecte des paramètres du modèle
			Map<String, Double> params = new HashMap<String, Double>();
			
			// saisir le montant total ttc comme montant de base
			params.put("{base}", product.saleAmount().totalAmountTtc());
			
			// saisir toutes les taxes
			for (SaleTax tax : product.taxes().all()) {
				params.put(String.format("{%s}", tax.shortName().toLowerCase()), tax.decimalValue());
				params.put(String.format("{%s-amount}", tax.shortName().toLowerCase()), tax.amount());
			}
			
			// générer l'article
			PieceArticle article = reglementTrame.generate(journalEncaissement, params);
			piece.articles().add(article);
		}		
		
		piece.validate();
		
		receipt.changeStep(PaymentStep.SEND_TO_ACCOUNTING);
	}
	
	private void send(InvoiceReceipt receipt, boolean silentMode) throws IOException{
		
		checkAvailability();
		
		checkIfIsValidPiece(receipt.status() == PaymentStatus.VALIDATED);
		
		deleteIfAlreadyAccounted(receipt.reference(), receipt.step() == PaymentStep.SEND_TO_ACCOUNTING);
		
		Journal journalEncaissement;
		Trame reglementTrame;
		PieceType pieceType;
		
		Tiers tiers = compta.tiers().build(receipt.customer().id());
		
		if(receipt.modulePdv().equals(sales.modulePointDeVenteDirecte())){
			PaymentInterface paymentInterface = compta.interfacage().salesInterface().paymentInterface();		
			
			// récupérer la liste des interfaces catégories 
			PaymentModeInterface paymentModeInterface = paymentInterface.paymentModes().get(receipt.mode().id());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();	
			
			Invoice invoice = sales.invoices().get(receipt.originId());
			if(invoice.nature() == InvoiceNature.DOWN_PAYMENT)
				reglementTrame  = paymentModeInterface.acompteOrAvance();
			else
				reglementTrame  = paymentModeInterface.reglementDefinitif();			

			pieceType = paymentModeInterface.reglement();
		}else{
			ModulePdvInterface pdvInterface = compta.interfacage().salesInterface().pdvInterface().modulePdvInterfaces().get(receipt.modulePdv().id());		
			
			// récupérer la liste des interfaces catégories
			PdvPaymentModeInterface paymentModeInterface = pdvInterface.paymentModes().get(receipt.mode());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();			
			
			Invoice invoice = sales.invoices().get(receipt.originId());
			if(invoice.nature() == InvoiceNature.DOWN_PAYMENT)
				reglementTrame  = paymentModeInterface.acompteOrAvance();
			else
				reglementTrame  = paymentModeInterface.reglementDefinitif();			
			
			pieceType = paymentModeInterface.reglement();
		}
							
		if(journalEncaissement.isNone() || reglementTrame.isNone() || pieceType.isNone())
		{
			throw new IllegalArgumentException("Comptabilité : vous devez configurer l'interface du mode de paiement !");
		}
		
		Piece piece = compta.pieces().of(pieceType)
				.add(receipt.paymentDate(), receipt.reference(), receipt.invoice().reference(), receipt.object(), sales, null, tiers);
							
		// Collecte des paramètres du modèle
		Map<String, Double> params = new HashMap<String, Double>();
		
		// saisir le montant total ht comme montant de base
		params.put("{base}", receipt.paidAmount());
		
		// saisir tous les montants des taxes de la facture si soldée
		Invoice invoice = receipt.invoice();
		if(invoice.solde() == 0) {
			for (SaleTax tax : invoice.taxes().all()) {
				params.put(String.format("{%s}", tax.shortName().toLowerCase()), tax.decimalValue());
				params.put(String.format("{%s-amount}", tax.shortName().toLowerCase()), tax.amount());
			}
		}
							
		// générer l'article
		PieceArticle article = reglementTrame.generate(journalEncaissement, params);
		piece.articles().add(article);
		
		piece.validate();
		
		receipt.changeStep(PaymentStep.SEND_TO_ACCOUNTING);
	}
	
	private void send(InvoiceRefundReceipt receipt, boolean silentMode) throws IOException{
		
		checkAvailability();
		
		checkIfIsValidPiece(receipt.status() == PaymentStatus.VALIDATED);
		
		deleteIfAlreadyAccounted(receipt.reference(), receipt.step() == PaymentStep.SEND_TO_ACCOUNTING);
		
		Journal journalEncaissement;
		Trame reglementTrame;
		PieceType pieceType;
		
		Tiers tiers = compta.tiers().build(receipt.customer().id());
		
		if(receipt.modulePdv().equals(sales.modulePointDeVenteDirecte())){
			PaymentInterface paymentInterface = compta.interfacage().salesInterface().paymentInterface();		
			
			// récupérer la liste des interfaces catégories 
			PaymentModeInterface paymentModeInterface = paymentInterface.paymentModes().get(receipt.mode().id());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();	
			
			Invoice invoice = sales.invoices().get(receipt.originId());
			if(invoice.nature() == InvoiceNature.DOWN_PAYMENT)
				reglementTrame  = paymentModeInterface.acompteOrAvance();
			else
				reglementTrame  = paymentModeInterface.reglementDefinitif();			

			pieceType = paymentModeInterface.reglement();
		}else{
			ModulePdvInterface pdvInterface = compta.interfacage().salesInterface().pdvInterface().modulePdvInterfaces().get(receipt.modulePdv().id());		
			
			// récupérer la liste des interfaces catégories
			PdvPaymentModeInterface paymentModeInterface = pdvInterface.paymentModes().get(receipt.mode());
			// vérifier que la catégorie à été configuré (autrement prendre les valeurs par défaut si configuré)	
			journalEncaissement  = paymentModeInterface.journalEncaissement();			
			
			Invoice invoice = sales.invoices().get(receipt.originId());
			if(invoice.nature() == InvoiceNature.DOWN_PAYMENT)
				reglementTrame  = paymentModeInterface.acompteOrAvance();
			else
				reglementTrame  = paymentModeInterface.reglementDefinitif();			
			
			pieceType = paymentModeInterface.reglement();
		}
							
		if(journalEncaissement.isNone() || reglementTrame.isNone() || pieceType.isNone())
		{
			throw new IllegalArgumentException("Comptabilité : vous devez configurer l'interface du mode de paiement !");
		}
		
		Piece piece = compta.pieces().of(pieceType)
				.add(receipt.paymentDate(), receipt.reference(), receipt.invoice().reference(), receipt.object(), sales, null, tiers);
							
		// Collecte des paramètres du modèle
		Map<String, Double> params = new HashMap<String, Double>();
		
		// saisir le montant total ht comme montant de base
		params.put("{base}", receipt.paidAmount());
		
		// générer l'article
		PieceArticle article = reglementTrame.generateReverse(journalEncaissement, params);
		piece.articles().add(article);
		
		piece.validate();
		
		receipt.changeStep(PaymentStep.SEND_TO_ACCOUNTING);
	}

	@Override
	public boolean available() {
		return compta.isInstalled();
	}
}
