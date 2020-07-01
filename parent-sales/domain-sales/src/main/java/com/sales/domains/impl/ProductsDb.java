package com.sales.domains.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.GuidKeyAdvancedQueryableDb;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.UseCode;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainsStore;
import com.infrastructure.datasource.QueryBuilder;
import com.sales.domains.api.InvoiceMetadata;
import com.sales.domains.api.InvoiceStatus;
import com.sales.domains.api.InvoiceType;
import com.sales.domains.api.PaymentMetadata;
import com.sales.domains.api.PaymentStatus;
import com.sales.domains.api.PaymentType;
import com.sales.domains.api.PricingMetadata;
import com.sales.domains.api.PricingMode;
import com.sales.domains.api.Product;
import com.sales.domains.api.ProductCategory;
import com.sales.domains.api.ProductCategoryMetadata;
import com.sales.domains.api.ProductMetadata;
import com.sales.domains.api.Products;
import com.sales.domains.api.PurchaseOrderMetadata;
import com.sales.domains.api.PurchaseOrderStatus;
import com.sales.domains.api.Sales;
import com.securities.api.MesureUnit;
import com.securities.api.NumberValueType;

public final class ProductsDb extends GuidKeyAdvancedQueryableDb<Product, ProductMetadata> implements Products {

	private final transient Sales module;
	private final transient ProductCategory category;
	private final transient UseCode useCode;
	private final transient String internalReference;
	
	public ProductsDb(final Base base, final Sales module, final ProductCategory category, UseCode useCode, String internalReference){
		super(base, "Produit introuvable !");
		this.module = module;
		this.category = category;
		this.useCode = useCode;
		this.internalReference = internalReference;
	}
	
	@Override
	protected QueryBuilder buildQuery(String filter) throws IOException {
		
		List<Object> params = new ArrayList<Object>();
		filter = StringUtils.defaultString(filter);
			
		ProductCategoryMetadata pdCatDm = ProductCategoryMetadata.create();
		String statement = String.format("%s pd "
				+ "JOIN %s cat on cat.%s=pd.%s "
				+ "WHERE (pd.%s ILIKE ? OR pd.%s ILIKE ? OR pd.%s ILIKE ? OR pd.%s ILIKE ?) AND pd.%s=?",			 
				dm.domainName(), 
				pdCatDm.domainName(), pdCatDm.keyName(), dm.categoryIdKey(),
				dm.nameKey(), dm.barCodeKey(), dm.descriptionKey(), dm.internalReferenceKey(), dm.moduleIdKey());
		
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		params.add(module.id());
		
		if(!StringUtils.isBlank(internalReference)){
			statement = String.format("%s AND pd.%s=?", statement, dm.internalReferenceKey());
			params.add(internalReference);
		}
		
		if(!category.isNone()) {
			statement = String.format("%s AND cat.%s=?", statement, pdCatDm.keyName());
			params.add(category.id());
		}
		
		if(useCode != UseCode.NONE){
			statement = String.format("%s AND cat.%s=?", statement, pdCatDm.useCodeKey());
			params.add(useCode.id());
		}
		
		HorodateMetadata hm = HorodateMetadata.create();
		String orderClause = String.format("ORDER BY pd.%s DESC", hm.dateCreatedKey());
				
		String keyResult = String.format("pd.%s", dm.keyName());
		return base.createQueryBuilder(ds, statement, params, keyResult, orderClause);
	}

	private Product addProduct(UUID id, String name, ProductCategory category, String internalReference, String barCode, String description, MesureUnit unit, String emballage, double quantity) throws IOException{
		
		if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Invalid name : it can't be empty!");
        }
		
		if (unit.isNone()) {
            throw new IllegalArgumentException("Invalid mesure unit : it can't be empty!");
        }
		
		if(StringUtils.isBlank(emballage))
			throw new IllegalArgumentException("Emballage invalide : il ne peut pas être vide !");
		
		if(quantity <= 0)
			throw new IllegalArgumentException("Quantité invalide : elle doit être supérieure à 0 !");
		
		if(category.isNone())
			throw new IllegalArgumentException("Vous devez spécifier la catégorie du produit !");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(!StringUtils.isBlank(internalReference)){
			internalReference = internalReference.trim();
			
			Products products = withInternalReference(internalReference);
			if(!products.isEmpty())
				throw new IllegalArgumentException("Cette référence interne existe déjà !");
			
			params.put(dm.internalReferenceKey(), internalReference);
		}
				
		params.put(dm.nameKey(), name);	
		params.put(dm.descriptionKey(), description);
		params.put(dm.mesureUnitIdKey(), unit.id());
		params.put(dm.barCodeKey(), barCode);
		params.put(dm.moduleIdKey(), module.id());
		params.put(dm.categoryIdKey(), category.id());
		params.put(dm.emballageKey(), emballage);
		params.put(dm.quantityKey(), quantity);
		
		ds.set(id, params);
		
		// créer le mode de tarification par défaut
		PricingMetadata pricingDm = PricingDb.dm();
		DomainsStore pricingDs = base.domainsStore(pricingDm);
		
		Map<String, Object> paramsPricing = new HashMap<String, Object>();
		paramsPricing.put(pricingDm.fixPriceKey(), 0);	
		paramsPricing.put(pricingDm.modeIdKey(), PricingMode.FIX.id());
		paramsPricing.put(pricingDm.reduceValueKey(), 0);
		paramsPricing.put(pricingDm.reduceValueTypeIdKey(), NumberValueType.PERCENT.id());
		
		pricingDs.set(id, paramsPricing);
		
		return newOne(id);
	}
	
	@Override
	public Product add(String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException {
		return addProduct(UUID.randomUUID(), name, category, internalReference, barCode, description, unit, emballage, quantity);			
	}

	@Override
	public void delete(Product item) throws IOException {
		if(contains(item)){
			item.taxes().deleteAll();
			PricingMetadata dm = new PricingMetadata();
			DomainsStore pricingDs = base.domainsStore(dm);
			pricingDs.delete(item.pricing().id());
			ds.delete(item.id());
		}		
	}
	
	@Override
	public Product getDownPaymentProduct() throws IOException {
		
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.barCodeKey(), dm.moduleIdKey());
		
		List<Object> values = ds.find(statement, Arrays.asList("ACPT", module.id()));
		Product product;
		if(values.isEmpty()) {
			product = add("Acompte", "ACPT", "ACPT", module.productCategories().getAcompteCategory(), "", module.mesureUnits().getArticleUnit(), "Facture acompte", 1);
			product.pricing().update(0, PricingMode.KNOWN_IN_SALING, 0, NumberValueType.AMOUNT); // prix déterminé à la vente
		} else
			product = get(UUIDConvert.fromObject(values.get(0)));
		
		return product;
	}

	@Override
	public double turnover(LocalDate start, LocalDate end) throws IOException {
		PaymentMetadata dm = PaymentMetadata.create();
		DomainsStore ds = base.domainsStore(dm);
		
		String statement = String.format("SELECT SUM(pay.%s) FROM %s pay "
										+ "WHERE pay.%s BETWEEN ? AND ? AND pay.%s=? AND pay.%s = ? AND pay.%s=? AND pay.%s IS NULL", 
										dm.paidAmountKey(), dm.domainName(), 
										dm.paymentDateKey(), dm.moduleIdKey(), dm.statusIdKey(), dm.typeIdKey(), dm.provisionIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(module.id());
		params.add(PaymentStatus.VALIDATED.id());
		params.add(PaymentType.ENCAISSEMENT.id());
		
		List<Object> results = ds.find(statement, params);
		return Double.parseDouble(results.get(0) == null ? "0": results.get(0).toString());
	}

	@Override
	public double invoicedAmount(LocalDate start, LocalDate end) throws IOException {
		return realInvoicedAmount(start, end) + ticketAmount(start, end);
	}
	
	private double realInvoicedAmount(LocalDate start, LocalDate end) throws IOException {
		InvoiceMetadata dm = InvoiceMetadata.create();
		DomainsStore ds = base.domainsStore(dm);
		
		String statement = String.format("SELECT SUM(inv.%s) FROM %s inv "
										+ "WHERE inv.%s BETWEEN ? AND ? AND inv.%s=? AND (inv.%s=? OR inv.%s=?) AND inv.%s=?", 
										dm.totalAmountTtcKey(), dm.domainName(), 										
										dm.orderDateKey(), dm.moduleIdKey(), dm.statusKey(), dm.statusKey(), dm.invoiceTypeIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(module.id());
		params.add(InvoiceStatus.OPENED.id());
		params.add(InvoiceStatus.PAID.id());
		params.add(InvoiceType.FACTURE_DOIT.id());
		
		List<Object> results = ds.find(statement, params);
		return Double.parseDouble(results.get(0) == null ? "0": results.get(0).toString());
	}
	
	private double ticketAmount(LocalDate start, LocalDate end) throws IOException {
		PurchaseOrderMetadata dm = PurchaseOrderMetadata.create();
		PaymentMetadata payDm = PaymentMetadata.create();
		DomainsStore ds = base.domainsStore(dm);
		
		String statement = String.format("SELECT SUM(po.%s) FROM %s po "
										+ "JOIN %s pay ON pay.%s=po.%s "
										+ "WHERE po.%s BETWEEN ? AND ? AND po.%s=? AND (po.%s=? OR po.%s=?)", 
										dm.totalAmountTtcKey(), dm.domainName(), 	
										payDm.domainName(), payDm.purchaseOrderIdKey(), dm.keyName(),
										dm.orderDateKey(), dm.moduleIdKey(), dm.statusIdKey(), dm.statusIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(module.id());
		params.add(PurchaseOrderStatus.ENTIRELY_INVOICED.id());
		params.add(PurchaseOrderStatus.PAID.id());
		
		List<Object> results = ds.find(statement, params);
		return Double.parseDouble(results.get(0) == null ? "0": results.get(0).toString());
	}

	@Override
	public Products of(ProductCategory category) throws IOException {
		return new ProductsDb(base, module, category, useCode, internalReference);
	}

	@Override
	public Product getDefaultRemiseProduct() throws IOException {
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.barCodeKey(), dm.moduleIdKey());
		
		List<Object> values = ds.find(statement, Arrays.asList("REM", module.id()));
		Product product;
		if(values.isEmpty()) {
			product = add("Remise", "REM", "REM", module.productCategories().getDefaultRemiseCategory(), "", module.mesureUnits().getArticleUnit(), "Réduction commerciale", 1);
			product.pricing().update(0, PricingMode.KNOWN_IN_SALING, 0, NumberValueType.AMOUNT); // prix déterminé à la vente
		} else
			product = get(UUIDConvert.fromObject(values.get(0)));
		
		return product;
	}

	@Override
	public Product getDefaultRabaisProduct() throws IOException {
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.barCodeKey(), dm.moduleIdKey());
		
		List<Object> values = ds.find(statement, Arrays.asList("RAB", module.id()));
		Product product;
		if(values.isEmpty()) {
			product = add("Rabais", "RAB", "RAB", module.productCategories().getDefaultRabaisCategory(), "", module.mesureUnits().getArticleUnit(), "Réduction commerciale", 1);
			product.pricing().update(0, PricingMode.KNOWN_IN_SALING, 0, NumberValueType.AMOUNT); // prix déterminé à la vente
		}else
			product =get(UUIDConvert.fromObject(values.get(0)));
		
		return product;		
	}

	@Override
	public Product getDefaultRistourneProduct() throws IOException {
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.barCodeKey(), dm.moduleIdKey());
		
		List<Object> values = ds.find(statement, Arrays.asList("RIS", module.id()));
		Product product;
		if(values.isEmpty()) {
			product = add("Ristourne", "RIS", "RIS", module.productCategories().getDefaultRistourneCategory(), "", module.mesureUnits().getArticleUnit(), "Réduction commerciale", 1);
			product.pricing().update(0, PricingMode.KNOWN_IN_SALING, 0, NumberValueType.AMOUNT); // prix déterminé à la vente
		}else
			product = get(UUIDConvert.fromObject(values.get(0)));
		
		return product;
	}

	@Override
	public Products of(UseCode useCode) throws IOException {
		return new ProductsDb(base, module, category, useCode, internalReference);
	}

	@Override
	public Product getEscompteProduct() throws IOException {
		String statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=?", dm.keyName(), dm.domainName(), dm.barCodeKey(), dm.moduleIdKey());
		
		List<Object> values = ds.find(statement, Arrays.asList("ESC", module.id()));
		Product product;
		if(values.isEmpty()) {
			product = add("Escompte", "ESC", "ESC", module.productCategories().getEscompteCategory(), "", module.mesureUnits().getArticleUnit(), "Réduction financière", 1);
			product.pricing().update(0, PricingMode.KNOWN_IN_SALING, 0, NumberValueType.AMOUNT); // prix déterminé à la vente
		}else{
			product = get(UUIDConvert.fromObject(values.get(0)));
		}
		
		return product; 
	}

	@Override
	protected Product newOne(UUID id) {
		return new ProductDb(base, id, module);
	}

	@Override
	public Product none() {
		return new ProductNone();
	}

	@Override
	public Product add(UUID id, String name, String internalReference, String barCode, ProductCategory category, String description, MesureUnit unit, String emballage, double quantity) throws IOException {
		return addProduct(id, name, category, internalReference, barCode, description, unit, emballage, quantity);
	}

	@Override
	public Products withInternalReference(String internalReference) throws IOException {
		return new ProductsDb(base, module, category, useCode, internalReference);
	}

	@Override
	public double returnAmount(LocalDate start, LocalDate end) throws IOException {
		PaymentMetadata dm = PaymentMetadata.create();
		DomainsStore ds = base.domainsStore(dm);
		
		String statement = String.format("SELECT SUM(pay.%s) FROM %s pay "
										+ "WHERE pay.%s BETWEEN ? AND ? AND pay.%s=? AND pay.%s = ? AND pay.%s=?", 
										dm.paidAmountKey(), dm.domainName(), 
										dm.paymentDateKey(), dm.moduleIdKey(), dm.statusIdKey(), dm.typeIdKey());
		
		List<Object> params = new ArrayList<Object>();
		params.add(java.sql.Date.valueOf(start));
		params.add(java.sql.Date.valueOf(end));
		params.add(module.id());
		params.add(PaymentStatus.VALIDATED.id());
		params.add(PaymentType.REMBOURSEMENT.id());
		
		List<Object> results = ds.find(statement, params);
		return Double.parseDouble(results.get(0) == null ? "0": results.get(0).toString());
	}
}
