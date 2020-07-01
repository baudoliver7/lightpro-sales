package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.Invoice;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.InvoiceRefundReceipt;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.Payment;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentStep;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Payments;
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionRefundReceipt;
import com.sales.domains.api.ProvisionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderReceipt;
import com.sales.domains.api.Sales;
import com.securities.api.Contact;
import com.securities.api.PaymentMode;
import com.securities.api.PaymentModeType;

public final class PaymentsDb extends GuidKeyAdvancedQueryableDb<Payment, PaymentMetadata> implements Payments {

	private final transient Invoice invoice;
	private final transient Provision provision;
	private final transient Sales module;
	private final transient PaymentType type;
	private final transient PaymentStatus status;
	private final transient ModulePdv modulePdv;
	private final transient Contact customer;
	private final transient PurchaseOrder purchaseOrder;
	
	public PaymentsDb(final Base base, final Sales module, final Invoice invoice, final Provision provision, PaymentType type, PaymentStatus status, ModulePdv modulePdv, Contact customer, PurchaseOrder purchaseOrder){
		super(base, "Paiement introuvable !");
		this.invoice = invoice;
		this.module = module;
		this.type = type;
		this.status = status;
		this.modulePdv = modulePdv;
		this.provision = provision;
		this.customer = customer;
		this.purchaseOrder = purchaseOrder;
	}
	
	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);

		String statement = String.format("%s pay "
				+ "WHERE pay.%s ILIKE ? AND pay.%s=?", 
				dm.domainName(), 
				dm.referenceKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!modulePdv.isNone()){
			statement = String.format("%s AND pay.%s=?", statement, dm.modulePdvIdKey());
			params.add(modulePdv.id());
		}
		
		if(!customer.isNone()){
			statement = String.format("%s AND pay.%s=?", statement, dm.customerIdKey());
			params.add(customer.id());
		}
		
		if(status != PaymentStatus.NONE){
			statement = String.format("%s AND pay.%s=?", statement, dm.statusIdKey());
			params.add(status.id());
		}
		
		if(type != PaymentType.NONE){
			statement = String.format("%s AND pay.%s=?", statement, dm.typeIdKey());
			params.add(type.id());
		}
		
		if(!invoice.isNone()){
			statement = String.format("%s AND pay.%s=?", statement, dm.invoiceIdKey());
			params.add(invoice.id());
		}
		
		if(!provision.isNone()){
			statement = String.format("%s AND pay.%s=?", statement, dm.provisionIdKey());
			params.add(provision.id());
		}
		
		if(!purchaseOrder.isNone()){
			statement = String.format("%s AND pay.%s=?", statement, dm.purchaseOrderIdKey());
			params.add(purchaseOrder.id());
		}
				
		String orderClause;	
		HorodateMetadata hm = HorodateImpl.dm();
		
		if(!invoice.isNone() || !provision.isNone())
			orderClause = String.format("ORDER BY pay.%s ASC", hm.dateCreatedKey());	
		else
			orderClause = String.format("ORDER BY pay.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("pay.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	private Payment addPayment(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Provision provision, PaymentType type, Contact cashier) throws IOException {		
		
		if(modulePdv.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un point de vente !");
		
		if(cashier.isNone())
			throw new IllegalArgumentException("Aucun caissier n'a été spécifié !");
		else if(!module.sellers().contains(cashier))
			throw new IllegalArgumentException("Vous devez être un vendeur pour effectuer cette action !");
		
		if(customer.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un client !");
		
		if (paymentDate == null) {
            paymentDate = LocalDate.now(); // prendre la date d'aujourd'hui
        }
		
		if (StringUtils.isBlank(object)) {
            throw new IllegalArgumentException("Vous devez renseigner l'objet de paiement !");
        }
		
		if (paidAmount == 0) {
            throw new IllegalArgumentException("Vous devez renseigner le montant payé !");
        }
		
		if(type == PaymentType.NONE)
			throw new IllegalArgumentException("Vous devez spécifier un type de paiement !");
		
		if(mode.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un mode de paiement !");
		
		if(mode.type() == PaymentModeType.BANK && StringUtils.isBlank(transactionReference))
			throw new IllegalArgumentException(String.format("Vous devez renseigner la référence de la transaction !"));
			
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.paymentDateKey(), java.sql.Date.valueOf(paymentDate));	
		params.put(dm.objectKey(), object);
		params.put(dm.paidAmountKey(), paidAmount);
		params.put(dm.referenceKey(), "Brouillon");
		params.put(dm.modeIdKey(), mode.id());
		params.put(dm.transactionReferenceKey(), transactionReference);
		params.put(dm.invoiceIdKey(), invoice.id());
		params.put(dm.purchaseOrderIdKey(), purchaseOrder.id());
		params.put(dm.statusIdKey(), PaymentStatus.DRAFT.id());
		params.put(dm.stepKey(), PaymentStep.CREATING.id());
		params.put(dm.typeIdKey(), type.id());
		params.put(dm.modulePdvIdKey(), modulePdv.id());
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.cashierIdKey(), cashier.id());
		
		if(type == PaymentType.ENCAISSEMENT)
			params.put(dm.provisionIdKey(), provision.id());
		
		if(type == PaymentType.REMBOURSEMENT)
			params.put(dm.provisionIdKey(), this.provision.id());
				
		params.put(dm.moduleIdKey(), module.id());
				
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}
	
	@Override
	public InvoiceReceipt cashInvoice(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		
		if(invoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture à payer !");
				
		Payment payment = addPayment(paymentDate, object, paidAmount, mode, transactionReference, new ProvisionNone(), PaymentType.ENCAISSEMENT, cashier);
		return getInvoiceReceipt(payment.id());
	}

	@Override
	public InvoiceRefundReceipt refundInvoice(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		
		if(invoice.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la facture à payer !");
		
		if((invoice.type() == InvoiceType.FACTURE_AVOIR && invoice.saleAmount().totalAmountTtc() < paidAmount)
				|| (invoice.type() == InvoiceType.FACTURE_DOIT && invoice.totalAmountPaid() < paidAmount))
			throw new IllegalArgumentException("Le montant remboursé excède le montant payé !");
		
		Payment payment = addPayment(paymentDate, object, paidAmount, mode, transactionReference, new ProvisionNone(), PaymentType.REMBOURSEMENT, cashier);
		return getInvoiceRefundReceipt(payment.id());
	}

	@Override
	public Payments of(Provision provision) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, modulePdv, provision.customer(), purchaseOrder);
	}

	@Override
	public void delete(Payment item) throws IOException {
		if(contains(item) && item.status() == PaymentStatus.DRAFT){
			ds.delete(item.id());
		}
	}

	@Override
	public Payments of(Invoice invoice) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, invoice.modulePdv(), invoice.customer(), purchaseOrder);
	}

	@Override
	public Payments of(PaymentType type) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, modulePdv, customer, purchaseOrder);
	}

	@Override
	public Payments of(PaymentStatus status) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, modulePdv, customer, purchaseOrder);
	}

	@Override
	public Payments of(ModulePdv modulePdv) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, modulePdv, customer, purchaseOrder);
	}

	@Override
	public Payments ofVenteDirecte() throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, module.modulePointDeVenteDirecte(), customer, purchaseOrder);
	}

	@Override
	public Payments of(Contact customer) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, modulePdv, customer, purchaseOrder);
	}

	@Override
	public InvoiceReceipt cashInvoice(LocalDate paymentDate, String object, double paidAmount, Provision provision, Contact cashier) throws IOException {
		
		if(provision.isNone())
			throw new IllegalArgumentException("Vous devez spécifier une provision !");
		
		if(provision.status() == ProvisionStatus.CONSOMMEE)
			throw new IllegalArgumentException("La provision est déjà consommée !");
		
		if(invoice.isNone())
			throw new IllegalArgumentException("La facture à payer doit être spécifiée !");
		
		if(invoice.leftAmountToPay() < paidAmount)
			throw new IllegalArgumentException("Le montant du paiement est supérieur au reste à payer !");
		
		if(provision.availableAmount() < paidAmount)
			throw new IllegalArgumentException("Le montant de provision disponible n'est pas suffisant pour effectuer le paiement !");
		
		if(provision.availableAmount() < paidAmount)
			throw new IllegalArgumentException("Le montant versé est supérieur au montant de provision disponible !");
		
		PaymentMode mode = provision.originPayment().mode();
		String transactionReference = provision.originPayment().transactionReference();
		
		Payment payment = addPayment(paymentDate, object, paidAmount, mode, transactionReference, provision, PaymentType.ENCAISSEMENT, cashier);		
		
		return getInvoiceReceipt(payment.id());
	}

	@Override
	public ProvisionRefundReceipt refundProvision(LocalDate paymentDate, String object, double paidAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		
		if(provision.isNone())
			throw new IllegalArgumentException("Vous devez spécifier une provision !");
		
		if(provision.status() == ProvisionStatus.CONSOMMEE)
			throw new IllegalArgumentException("La provision est déjà consommée !");		

		if(provision.availableAmount() < paidAmount)
			throw new IllegalArgumentException("Le montant de provision disponible n'est pas suffisant pour effectuer le paiement !");
		
		Payment payment = addPayment(paymentDate, object, paidAmount, mode, transactionReference, provision, PaymentType.REMBOURSEMENT, cashier);
		return getProvisionRefundReceipt(payment.id());
	}

	@Override
	protected Payment newOne(UUID id) {
		Payment payment = invoiceReceipt(id);
		if(!payment.isNone())
			return payment;
		
		payment = invoiceRefundReceipt(id);
		if(!payment.isNone())
			return payment;
		
		payment = provisionRefundReceipt(id);
		if(!payment.isNone())
			return payment;
		
		return purchaseOrderReceipt(id);			
	}
	
	private Payment invoiceReceipt(UUID id){
		try {
			return new InvoiceReceiptDb(base, id, module);
		} catch (Exception e) {
			return new PaymentNone();
		}
	}
	
	private Payment invoiceRefundReceipt(UUID id){
		try {
			return new InvoiceRefundReceiptDb(base, id, module);
		} catch (Exception e) {
			return new PaymentNone();
		}
	}
	
	private Payment provisionRefundReceipt(UUID id){
		try {
			return new ProvisionRefundReceiptDb(base, id, module);
		} catch (Exception e) {
			return new PaymentNone();
		}
	}
	
	private Payment purchaseOrderReceipt(UUID id){
		try {
			return new PurchaseOrderReceiptDb(base, id, module);
		} catch (Exception e) {
			return new PaymentNone();
		}
	}

	@Override
	public Payment none() {
		return new PaymentNone();
	}

	@Override
	public Payments of(PurchaseOrder purchaseOrder) throws IOException {
		return new PaymentsDb(base, module, invoice, provision, type, status, purchaseOrder.modulePdv(), purchaseOrder.customer(), purchaseOrder);
	}

	@Override
	public PurchaseOrderReceipt cashPurchaseOrder(LocalDate paymentDate, String object, double receivedAmount, PaymentMode mode, String transactionReference, Contact cashier) throws IOException {
		
		if(purchaseOrder.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la commande à payer !");
		
		double montantTtc = purchaseOrder.saleAmount().totalAmountTtc();
		if(montantTtc > receivedAmount)
			throw new IllegalArgumentException("Le montant reçu est inférieur au montant à payer !");
		
		Payment receipt = addPayment(paymentDate, object, montantTtc, mode, transactionReference, new ProvisionNone(), PaymentType.ENCAISSEMENT, cashier);
		
		// enregistrer le montant reçu et la monnaie rendue
		DomainStore ds = this.ds.createDs(receipt.id());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.receivedAmountKey(), receivedAmount);
		params.put(dm.changeKey(), receivedAmount - montantTtc);
		
		ds.set(params);
		
		return getPurchaseOrderReceipt(receipt.id());
	}

	@Override
	public PurchaseOrderReceipt getPurchaseOrderReceipt(UUID id) throws IOException {
		return (PurchaseOrderReceipt)get(id);
	}

	@Override
	public InvoiceReceipt getInvoiceReceipt(UUID id) throws IOException {
		return (InvoiceReceipt)get(id);
	}

	@Override
	public InvoiceRefundReceipt getInvoiceRefundReceipt(UUID id) throws IOException {
		return (InvoiceRefundReceipt)get(id);
	}

	@Override
	public ProvisionRefundReceipt getProvisionRefundReceipt(UUID id) throws IOException {
		return (ProvisionRefundReceipt)get(id);
	}
}
