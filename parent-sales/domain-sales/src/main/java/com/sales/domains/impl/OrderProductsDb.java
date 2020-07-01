package com.sales.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.Base.OrderDirection;
import com.infrastructure.datasource.DomainStore;
import com.sales.domains.api.Order;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.OrderProductMetadata;
import com.sales.domains.api.OrderProducts;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.Remise;
import com.sales.domains.api.SaleAmount;
import com.securities.api.Tax;

public final class OrderProductsDb extends GuidKeyQueryableDb<OrderProduct, OrderProductMetadata> implements OrderProducts {

	private final transient Order order;
	public OrderProductsDb(final Base base, final Order order){
		super(base, "Produit de l'ordre introuvable !");
		this.order = order;
	}
	
	@Override
	public List<OrderProduct> all() throws IOException {
		List<OrderProduct> values = new ArrayList<OrderProduct>();
		
		List<DomainStore> results = ds.getAllByKeyOrdered(dm.orderIdKey(), order().id(), HorodateMetadata.create().dateCreatedKey(), OrderDirection.ASC);
		for (DomainStore domainStore : results) {
			values.add(build(UUIDConvert.fromObject(domainStore.key()))); 
		}			
		
		return values;
	}

	private OrderProduct addOrderProduct(UUID id, ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, SaleAmount saleAmount, Remise remise, List<Tax> taxes, Product originProduct) throws IOException{
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.quantityKey(), quantity);	
		params.put(dm.unitPriceKey(), unitPrice);		
		params.put(dm.totalAmountHtKey(), saleAmount.totalAmountHt());
		params.put(dm.reductionAmountKey(), saleAmount.reduceAmount());
		params.put(dm.netCommercialKey(), saleAmount.netCommercial());
		params.put(dm.totalTaxAmountKey(), saleAmount.totalTaxAmount());
		params.put(dm.totalAmountTtcKey(), saleAmount.totalAmountTtc());		
		params.put(dm.productIdKey(), product.id());
		params.put(dm.orderIdKey(), order.id());
		params.put(dm.descriptionKey(), description);
		params.put(dm.nameKey(), name);
		params.put(dm.categoryIdKey(), category.id());
		params.put(dm.deductibleKey(), quantity < 0);
		params.put(dm.originProductId(), originProduct.id());
		params.put(dm.reduceValueKey(), remise.value());
		params.put(dm.reduceValueTypeIdKey(), remise.valueType().id());
				
		ds.set(id, params);
		
		OrderProduct orderProduct = build(id);
		for (Tax tax : taxes) {
			orderProduct.taxes().add(tax);
		}
		
		// recalculer les montants du bon de commande
		order.updateAmounts();
		
		return orderProduct;
	}
	
	@Override
	public OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException {	
		return add(UUID.randomUUID(), category, product, name, description, quantity, unitPrice, remise, taxes, originProduct);
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
	public OrderProduct add(OrderProduct product) throws IOException {
		return addOrderProduct(UUID.randomUUID(), product.category(), product.product(), product.name(), product.description(), product.quantity(), product.unitPrice(), product.saleAmount(), product.remise(), product.taxes().all().stream().map(m -> m).collect(Collectors.toList()), product.originProduct());
	}

	@Override
	public OrderProduct add(ProductCategory category, Product product, String name, String description, double quantity,
			double unitPrice, Remise remise, List<Tax> taxes) throws IOException {
		return add(category, product, name, description, quantity, unitPrice, remise, taxes, new ProductNone());
	}

	@Override
	protected OrderProduct newOne(UUID id) {
		return new OrderProductDb(base, id, order);
	}

	@Override
	public OrderProduct none() {
		return new OrderProductNone();
	}

	@Override
	public OrderProduct add(UUID id, ProductCategory category, Product product, String name, String description, double quantity, double unitPrice, Remise remise, List<Tax> taxes, Product originProduct) throws IOException {
		
		if(product.isNone())
			throw new IllegalArgumentException("Vous devez spécifier un produit !");
		
		SaleAmount amounts = new ProductSaleAmount(quantity, unitPrice, remise, taxes);
		
		if(amounts.totalAmountHt() < 0 && quantity > 0)
			throw new IllegalArgumentException("Montant de réduction invalide : il ne peut pas être plus élévé que le montant hors taxes !");
		
		SaleAmount saleAmount = new ProductSaleAmount(quantity, unitPrice, remise, taxes);		
		return addOrderProduct(id, category, product, name, description, quantity, unitPrice, saleAmount, remise, taxes,  originProduct);
	}
}
