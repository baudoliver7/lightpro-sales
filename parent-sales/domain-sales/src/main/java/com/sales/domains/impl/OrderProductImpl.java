package com.sales.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.Horodate;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductMetadata;
import com.sales.domains.api.OrderProductTaxMetadata;
import com.sales.domains.api.OrderProductTaxes;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductAmounts;
import com.securities.api.Tax;

public class OrderProductImpl implements OrderProduct {
	
	private final transient Base base;
	private final transient UUID id;
	private final transient OrderProductMetadata dm;
	private final transient DomainStore ds;
	private final transient Order order;
	
	public OrderProductImpl(final Base base, final UUID id, Order order){
		this.base = base;
		this.id = id;
		this.order = order;
		this.dm = OrderProductMetadata.create();
		this.ds = this.base.domainsStore(this.dm).createDs(id);
	}

	@Override
	public Horodate horodate() {
		return new HorodateImpl(ds);
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public boolean isPresent() {
		return base.domainsStore(dm).exists(id);
	}

	@Override
	public int quantity() throws IOException {
		return ds.get(dm.quantityKey());
	}

	@Override
	public double unitPrice() throws IOException {
		return ds.get(dm.unitPriceKey());
	}

	@Override
	public double reductionAmount() throws IOException {
		return ds.get(dm.reductionAmountKey());
	}

	@Override
	public double totalAmountHt() throws IOException {
		return ds.get(dm.totalAmountHtKey());
	}

	@Override
	public double totalTaxAmount() throws IOException {
		return ds.get(dm.totalTaxAmountKey());
	}

	@Override
	public double totalAmountTtc() throws IOException {
		return ds.get(dm.totalAmountTtcKey());
	}

	@Override
	public Order order() throws IOException {
		return order;
	}

	@Override
	public void update(int quantity, double unitPrice, double reductionAmount, String description, Product product) throws IOException {	
		
		if(!product.isPresent())
			throw new IllegalArgumentException("Invalid product : it can't be empty !");
		
		boolean withTax = taxes().all().size() > 0;
		ProductAmounts amounts = product.evaluatePrice(quantity, unitPrice, reductionAmount, order().orderDate(), withTax);
		
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
		params.put(dm.descriptionKey(), description);
		
		ds.set(params);	
		
		if(withTax){
			// recalculer les taxes détaillées
			taxes().deleteAll();
			
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
		order().updateAmounts();
	}

	@Override
	public Product product() throws IOException {
		UUID productId = ds.get(dm.productIdKey());
		return new ProductImpl(base, productId);
	}

	@Override
	public OrderProductTaxes taxes() throws IOException {
		return new OrderProductTaxesImpl(base, this);
	}

	@Override
	public double unitPriceApplied() throws IOException {
		return ds.get(dm.unitPriceAppliedKey());
	}
	
	@Override
	public boolean isEqual(OrderProduct item) {
		return this.id().equals(item.id());
	}

	@Override
	public boolean isNotEqual(OrderProduct item) {
		return !isEqual(item);
	}

	@Override
	public void modifyUnitPrice(double amount) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}
}
