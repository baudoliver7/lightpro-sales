package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.InvoiceReceipt;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.Provision;
import com.sales.domains.api.ProvisionMetadata;
import com.sales.domains.api.ProvisionStatus;
import com.sales.domains.api.ProvisionStep;
import com.sales.domains.api.Provisions;
import com.sales.domains.api.Sales;
import com.securities.api.Contact;
import com.securities.api.ContactMetadata;

public final class ProvisionsDb extends GuidKeyAdvancedQueryableDb<Provision, ProvisionMetadata> implements Provisions {

	private transient final Sales module;
	private transient final Contact customer;
	
	public ProvisionsDb(Base base, Sales module, Contact customer) {
		super(base, "Provision introuvable !");
		
		this.module = module;
		this.customer = customer;
	}

	@Override
	public Provision add(double amount, InvoiceReceipt payment) throws IOException {
		
		if(payment.isNone())
			throw new IllegalArgumentException("Vous devez indiquer le paiement à l'origine de la provision !");
		
		if(payment.type() == PaymentType.REMBOURSEMENT)
			throw new IllegalArgumentException("Vous ne pouvez faire de provision que sur des encaissements !");
		
		if(!payment.provision().isNone())
			throw new IllegalArgumentException("Ce paiement a déjà été effectué à partir d'une provision !");
		
		Contact customer = payment.invoice().customer();
		Contact defaultPerson = module.contacts().defaultPerson();
		
		if(customer.equals(defaultPerson))
			throw new IllegalArgumentException("Le client doit être identifié pour effectuer une provision !");
		
		if(payment.paidAmount() < amount)
			throw new IllegalArgumentException("Le montant de provision ne peut pas être supérieur au montant du paiement !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.amountKey(), amount);
		params.put(dm.customerIdKey(), customer.id());
		params.put(dm.referenceKey(), "Brouillon");
		params.put(dm.statusIdKey(), ProvisionStatus.DRAFT.id());
		params.put(dm.stepIdKey(), ProvisionStep.CREATING.id());

		UUID id = payment.id();
		ds.set(id, params);
		
		return build(id);
	}
	
	@Override
	public void delete(Provision item) throws IOException {
		if(contains(item)){
			
			if(item.status() != ProvisionStatus.DRAFT){
				throw new IllegalArgumentException("Vous ne pouvez supprimer qu'une provision en mode brouillon !");
			}
			
			ds.delete(item.id());
		}
	}

	@Override
	public double totalAvailableAmount() throws IOException {
		
		if(customer.isNone())
			throw new IllegalArgumentException("Vous devez spécifier le client !");
		
		double amount = 0;
		
		for (Provision provision : all()) {
			if(provision.status() != ProvisionStatus.DRAFT)
				amount += provision.availableAmount();
		}
		
		return amount;
	}

	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);

		ContactMetadata persDm = ContactMetadata.create();
		PaymentMetadata payDm = PaymentMetadata.create();
		
		String statement = String.format("%s pro "
				+ "JOIN %s pay ON pay.%s=pro.%s "
				+ "JOIN %s pers ON pers.%s=pro.%s "
				+ "WHERE pro.%s ILIKE ? AND pay.%s=? ", 
				dm.domainName(), 
				payDm.domainName(), payDm.keyName(), dm.keyName(),
				persDm.domainName(), persDm.keyName(), dm.customerIdKey(),
				dm.referenceKey(), payDm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!customer.isNone()){
			statement = String.format("%s AND pers.%s=?", statement, persDm.keyName());
			params.add(customer.id());
		}
		
		String orderClause;	
		HorodateMetadata hm = new HorodateMetadata();
		
		if(!customer.isNone())
			orderClause = String.format("ORDER BY pro.%s ASC", hm.dateCreatedKey());	
		else
			orderClause = String.format("ORDER BY pro.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("pro.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	@Override
	protected Provision newOne(UUID id) {
		return new ProvisionDb(base, id, module);
	}

	@Override
	public Provision none() {
		return new ProvisionNone();
	}

	@Override
	public Provisions of(Contact customer) {
		return new ProvisionsDb(base, module, customer);
	}
}
