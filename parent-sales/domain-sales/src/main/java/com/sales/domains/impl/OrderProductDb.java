package com.sales.domains.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.infrastructure.core.GuidKeyEntityDb;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductMetadata;
import com.sales.domains.api.SaleTaxes;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Remise;
import com.sales.domains.api.SaleAmount;
import com.securities.api.NumberValueType;
import com.securities.api.Tax;

public final class OrderProductDb extends GuidKeyEntityDb<OrderProduct, OrderProductMetadata> implements OrderProduct {
	
	private final transient Order order;
	
	public OrderProductDb(final Base base, final UUID id, Order order){
		super(base, id, "Produit de l'ordre introuvable !");
		this.order = order;
	}

	@Override
	public double quantity() throws IOException {
		return ds.get(dm.quantityKey());
	}

	@Override
	public double unitPrice() throws IOException {
		return ds.get(dm.unitPriceKey());
	}

	@Override
	public Order order() throws IOException {
		return order;
	}

	@Override
	public void update(String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes) throws IOException {	
				
		ProductSaleAmount amounts = new ProductSaleAmount(quantity, unitPrice, remise, taxes); 
		
		if(quantity > 0 && amounts.totalAmountHt() < 0)
			throw new IllegalArgumentException("Invalid reduction amount : it can't be upper than total amount ht !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.quantityKey(), quantity);	
		params.put(dm.unitPriceKey(), unitPrice);
		params.put(dm.totalAmountHtKey(), amounts.totalAmountHt());
		params.put(dm.reductionAmountKey(), amounts.reduceAmount());
		params.put(dm.netCommercialKey(), amounts.netCommercial());		
		params.put(dm.totalTaxAmountKey(), amounts.totalTaxAmount());
		params.put(dm.totalAmountTtcKey(), amounts.totalAmountTtc());		
		params.put(dm.descriptionKey(), description);
		params.put(dm.nameKey(), name);
		params.put(dm.reduceValueKey(), remise.value());
		params.put(dm.reduceValueTypeIdKey(), remise.valueType().id());
		
		ds.set(params);	
		
		// recalculer les taxes détaillées
		taxes().deleteAll();
		for (Tax tax : taxes) {
			taxes().add(tax);
		}		
		
		// recalculer les montants de l'ordre
		order().updateAmounts();
	}

	@Override
	public Product product() throws IOException {
		UUID productId = ds.get(dm.productIdKey());
		return new ProductDb(base, productId, order.module());
	}

	@Override
	public SaleTaxes taxes() throws IOException {
		return new OrderProductTaxesDb(base, this);
	}

	@Override
	public String description() throws IOException {
		return ds.get(dm.descriptionKey());
	}

	@Override
	public String name() throws IOException {
		return ds.get(dm.nameKey());
	}

	@Override
	public ProductCategory category() throws IOException {
		UUID categoryId = ds.get(dm.categoryIdKey());
		return new ProductCategoryDb(base, categoryId, order.module());
	}

	@Override
	public boolean deductible() throws IOException {
		return ds.get(dm.deductibleKey());
	}

	@Override
	public SaleAmount saleAmount() throws IOException {
		double amountHt = ds.get(dm.totalAmountHtKey());
		double reduceAmount = ds.get(dm.reductionAmountKey());
		double netCommercial = ds.get(dm.netCommercialKey());
		double taxAmount = ds.get(dm.totalTaxAmountKey());
		double amountTtc = ds.get(dm.totalAmountTtcKey());
		
		return new OrderSaleAmount(amountHt, reduceAmount, netCommercial, taxAmount, amountTtc);
	}

	@Override
	public Product originProduct() throws IOException {
		UUID productId = ds.get(dm.originProductId());
		
		if(productId == null)
			return new ProductNone();
		else
			return new ProductDb(base, productId, order.module());
	}

	@Override
	public Remise remise() throws IOException {
		
		double value = ds.get(dm.reduceValueKey());
		int valueTypeId = ds.get(dm.reduceValueTypeIdKey());
		
		NumberValueType valueType = NumberValueType.get(valueTypeId);
		
		return order.module().remiseWithCurrency(value, valueType);
	}
}
