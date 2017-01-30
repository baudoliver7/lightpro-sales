package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductMetadata;
import com.sales.domains.api.OrderProductTaxMetadata;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductAmounts;
import com.securities.api.Tax;

public class OrderProductsImpl implements OrderProducts {

	private transient final Base base;
	private final transient OrderProductMetadata dm;
	private final transient DomainsStore ds;
	private final transient Order order;
	
	public OrderProductsImpl(final Base base, final Order order){
		this.base = base;
		this.order = order;
		this.dm = OrderProductMetadata.create();
		this.ds = this.base.domainsStore(this.dm);	
	}

	
	@Override
	public List<OrderProduct> all() throws IOException {
		List<OrderProduct> values = new ArrayList<OrderProduct>();
		
		List<DomainStore> results = ds.getAllByKey(dm.orderIdKey(), order().id());
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}			
		
		return values;
	}

	@Override
	public OrderProduct get(UUID id) throws IOException {
		OrderProduct item = build(id);
		
		if(!item.isPresent())
			throw new NotFoundException("Le produit n'a pas été trouvé !");
		
		return item;
	}

	@Override
	public OrderProduct build(UUID id) {
		return new OrderProductImpl(base, id, order);
	}

	@Override
	public OrderProduct add(int quantity, double unitPrice, double reductionAmount, String description, Product product, boolean withTax) throws IOException {
	
		if(!product.isPresent())
			throw new IllegalArgumentException("Invalid product : it can't be empty !");
		
		ProductAmounts amounts = product.evaluatePrice(quantity, unitPrice, reductionAmount, order.orderDate(), withTax);
		
		if(reductionAmount > amounts.unitPrice())
			throw new IllegalArgumentException("Invalid reduction amount : it can't be upper than unit price !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.quantityKey(), quantity);	
		params.put(dm.unitPriceKey(), amounts.unitPrice());
		params.put(dm.unitPriceAppliedKey(), amounts.unitPriceApplied());
		params.put(dm.totalAmountHtKey(), amounts.totalAmountHt());
		params.put(dm.totalTaxAmountKey(), amounts.totalTaxAmount());
		params.put(dm.totalAmountTtcKey(), amounts.totalAmountTtc());
		params.put(dm.reductionAmountKey(), reductionAmount);
		params.put(dm.productIdKey(), product.id());
		params.put(dm.orderIdKey(), order.id());
		params.put(dm.descriptionKey(), description);
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		if(withTax){
			// ajouter les taxes détaillées
			OrderProductTaxMetadata optDm = OrderProductTaxMetadata.create();
			DomainsStore optDs = base.domainsStore(optDm);
			for (Tax tax : product.taxes().all()) {
				
				Map<String, Object> paramsOpt = new HashMap<String, Object>();
				paramsOpt.put(optDm.amountKey(), tax.evaluateAmount(amounts.totalTaxAmount()));	
				paramsOpt.put(optDm.taxIdKey(), tax.id());
				paramsOpt.put(optDm.orderProductIdKey(), id);
				
				optDs.set(UUID.randomUUID(), paramsOpt);
			}
		}		
		
		// recalculer les montants du bon de commande
		order.updateAmounts();
		
		return build(id);
	}

	@Override
	public void delete(OrderProduct item) throws IOException {
		if(!contains(item))
			return;
		
		item.taxes().deleteAll();
		ds.delete(item.id());
	}


	@Override
	public void deleteAll() throws IOException {
		for (OrderProduct product : all()) {
			delete(product);
		}
	}


	@Override
	public Order order() throws IOException {
		return order;
	}


	@Override
	public boolean contains(OrderProduct item) {
		try {
			return all().stream().anyMatch(m -> m.isEqual(item));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;		
	}
}
