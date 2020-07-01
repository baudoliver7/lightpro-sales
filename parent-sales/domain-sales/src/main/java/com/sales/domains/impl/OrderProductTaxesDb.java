package com.sales.domains.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyQueryableDb;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.SaleTax;
import com.sales.domains.api.SaleTaxes;
import com.securities.api.Tax;

public final class OrderProductTaxesDb extends GuidKeyQueryableDb<SaleTax, OrderProductTaxMetadata> implements SaleTaxes {

	private final transient OrderProduct orderProduct;
	
	public OrderProductTaxesDb(final Base base, OrderProduct orderProduct){
		super(base, "Taxe du produit introuvable !");	
		this.orderProduct = orderProduct;
	}
	
	@Override
	public double amount() throws IOException {
		return 0;
	}

	@Override
	public List<SaleTax> all() throws IOException {
		List<Object> results = base.executeQuery(String.format("SELECT %s FROM %s WHERE %s=? order by %s ASC", dm.keyName(), dm.domainName(), dm.orderProductIdKey(), HorodateImpl.dm().dateCreatedKey()), Arrays.asList(orderProduct.id()));
		return results.stream().map(m -> build(UUIDConvert.fromObject(m))).collect(Collectors.toList());
	}

	@Override
	public SaleTax add(Tax tax) throws IOException {
		 
		if(tax.isNone())
			throw new IllegalArgumentException("La taxe n'a pas été trouvée !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.amountKey(), tax.evaluateAmount(orderProduct.saleAmount().netCommercial()));	
		params.put(dm.taxIdKey(), tax.id());
		params.put(dm.orderProductIdKey(), orderProduct.id());
		params.put(dm.nameKey(), tax.name());
		params.put(dm.shortNameKey(), tax.shortName());
		params.put(dm.valueKey(), tax.value());
		params.put(dm.valueTypeKey(), tax.valueType().id());
		params.put(dm.typeIdKey(), tax.type().id());
		
		UUID id = UUID.randomUUID();
		ds.set(id, params);
		
		return build(id);
	}

	@Override
	public void delete(Tax item) throws IOException {
		base.executeUpdate(String.format("DELETE FROM %s WHERE %s=? AND %s=?", dm.domainName(), dm.taxIdKey(), dm.orderProductIdKey()), Arrays.asList(item.id(), orderProduct.id()));
	}

	@Override
	public void deleteAll() throws IOException {
		base.executeUpdate(String.format("DELETE FROM %s WHERE %s=?", dm.domainName(), dm.orderProductIdKey()), Arrays.asList(orderProduct.id()));
	}
	
	@Override
	public String toString() {
		try {
			return new SaleTaxListFormatter(all().stream().map(m -> m).collect(Collectors.toList())).toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected SaleTax newOne(UUID id) {
		return new OrderProductTaxDb(base, id, orderProduct);
	}

	@Override
	public SaleTax none() {
		return new SaleTaxNone();
	}
}
