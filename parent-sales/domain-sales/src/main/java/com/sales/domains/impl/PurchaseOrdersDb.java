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
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.PaymentConditionStatus;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.securities.api.Contact;
import com.securities.api.Sequence;
import com.securities.api.Sequence.SequenceReserved;

public final class PurchaseOrdersDb extends GuidKeyAdvancedQueryableDb<PurchaseOrder, PurchaseOrderMetadata> implements PurchaseOrders {

	private final transient Sales module;
	private final transient ModulePdv modulePdv;
	private final transient PurchaseOrderStatus status;
	
	public PurchaseOrdersDb(final Base base, final Sales module, final ModulePdv modulePdv, final PurchaseOrderStatus status){
		super(base, "Devis introuvable !");
		this.module = module;
		this.modulePdv = modulePdv;
		this.status = status;
	}
	
	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
		
		String statement = String.format("%s po "
				+ "WHERE po.%s ILIKE ? AND po.%s=?",
				dm.domainName(), 
				dm.referenceKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!modulePdv.isNone()){
			statement = String.format("%s AND po.%s=?", statement, dm.modulePdvIdKey());
			params.add(modulePdv.id());
		}
		
		if(status != PurchaseOrderStatus.NONE){
			statement = String.format("%s AND po.%s=?", statement, dm.statusIdKey());
			params.add(status.id());
		}
		
		HorodateMetadata hm = HorodateImpl.dm();
		String orderClause = String.format("ORDER BY po.%s DESC", hm.dateCreatedKey());;	
					
		String keyResult = String.format("po.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	public void delete(PurchaseOrder item) throws IOException {
		if(!contains(item))
			return;
		
		if(item.status() != PurchaseOrderStatus.CREATED)
			throw new IllegalArgumentException("Vous ne pouvez supprimer qu'un devis en mode de création !");
		
		item.products().deleteAll();
		ds.delete(item.id());		
	}

	@Override
	public PurchaseOrder add(LocalDate date, LocalDate expirationDate, PaymentConditionStatus paymentCondition, String cgv, String description, String notes, Contact customer, Contact seller, int livraisonDelayInDays) throws IOException {
		
		if (seller.isNone())
            throw new IllegalArgumentException("Vous devez indiquer un vendeur !");
		
		if(!module().sellers().contains(seller))
			throw new IllegalArgumentException("Vous devez être un vendeur pour effectuer cette action !");
		
		if(paymentCondition == PaymentConditionStatus.NONE)
			throw new IllegalArgumentException("Vous devez spécifier les conditions de règlement !");
		
		if (date == null)
            throw new IllegalArgumentException("Invalid date : it can't be empty!");
		
		if (expirationDate == null)
            throw new IllegalArgumentException("Invalid expiration date : it can't be empty!");
				
		if(modulePdv.isNone())
			throw new IllegalArgumentException("Le module de vente n'est pas spécifé !");
		
		if (customer.isNone()){
			customer = module.customers().defaultCustomer(); // mettre un client par défaut		
		}
		
		if(livraisonDelayInDays < 0)
			throw new IllegalArgumentException("Délai de livraison invalide : vous devez saisir un nombre positif !");
		
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
		
		params.put(dm.statusIdKey(), PurchaseOrderStatus.CREATED.id());	
		params.put(dm.referenceKey(), sequence().generate());
		params.put(dm.moduleIdKey(), module.id());
		params.put(dm.livraisonDelayDateKey(), livraisonDelayInDays);
		params.put(dm.modulePdvIdKey(), modulePdv.id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);	
		
		return build(id);
	}

	private Sequence sequence() throws IOException{
		return module.company().moduleAdmin().sequences().reserved(SequenceReserved.PURCHASE_ORDER);
	}

	@Override
	public Sales module() throws IOException {
		return module;
	}

	@Override
	public PurchaseOrders of(ModulePdv modulePdv) throws IOException {
		return new PurchaseOrdersDb(base, module, modulePdv, status);
	}

	@Override
	public PurchaseOrders of(PurchaseOrderStatus status) throws IOException {
		return new PurchaseOrdersDb(base, module, modulePdv, status);
	}

	@Override
	public PurchaseOrders ofVenteDirecte() throws IOException {
		return new PurchaseOrdersDb(base, module, module.modulePointDeVenteDirecte(), status);
	}

	@Override
	protected PurchaseOrder newOne(UUID id) {
		return new PurchaseOrderDb(base, id, module);
	}

	@Override
	public PurchaseOrder none() {
		return new PurchaseOrderNone();
	}
}
