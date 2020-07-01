package com.sales.domains.impl;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.common.utilities.convert.TimeConvert;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.sales.domains.api.OrderProduct;
import com.sales.domains.api.PurchaseOrder;
import com.sales.domains.api.Sales;
import com.sales.domains.api.Team;
import com.sales.interfacage.compta.api.StocksInterface;
import com.sales.interfacage.compta.api.TeamStockInterface;
import com.sales.interfacage.compta.api.TeamStockInterfaceMetadata;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.stocks.domains.api.Article;
import com.stocks.domains.api.Articles;
import com.stocks.domains.api.Operation;
import com.stocks.domains.api.OperationType;
import com.stocks.domains.api.Stocks;
import com.stocks.domains.impl.OperationNone;
import com.stocks.domains.impl.StocksDb;

public final class StocksInterfaceDb implements StocksInterface {

	private final transient Base base;
	private final transient Sales sales;
	private transient Stocks stocks;
	
	public StocksInterfaceDb(final Base base, final Sales sales){
		this.base = base;
		this.sales = sales;
		
		initialize();
	}
	
	private void initialize()  {
		try {
			Module module;
			
			if(sales.company().modulesInstalled().of(ModuleType.STOCKS).isEmpty())
				module = sales.company().modulesProposed().get(ModuleType.STOCKS);				
			else
				module = sales.company().modulesInstalled().get(ModuleType.STOCKS);	
			
			stocks = new StocksDb(base, module);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkAvailability(){
		
		if(!available())
			throw new IllegalArgumentException("Le module de stock n'est pas installé !");
	}
	
	@Override
	public boolean available() {
		return stocks.isInstalled();
	}

	@Override
	public void linkTeamToStock(Team team, UUID warehouseId, UUID livraisonOptId) throws IOException {
		
		checkAvailability();
		
		if(team.isNone())
			throw new IllegalArgumentException("Vous devez spécifier une équipe commerciale !");
		
		TeamStockInterfaceMetadata dm = TeamStockInterfaceMetadata.create();
		DomainStore ds = base.domainsStore(dm).createDs(team.id());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(dm.warehouseIdKey(), warehouseId);
		params.put(dm.livraisonOpTypeIdKey(), livraisonOptId);
		
		ds.set(params);
	}

	@Override
	public void prepareDelivery(PurchaseOrder order) throws IOException {
		deliver(order, false);		
	}
	
	private void deliver(PurchaseOrder order, boolean execute) throws IOException {
		
		checkAvailability();
		
		Operation op = new OperationNone();
		
		List<OrderProduct> stockProducts = new ArrayList<OrderProduct>();
		List<Article> articles = new ArrayList<Article>();
		for (OrderProduct pd : order.products().all()) {
			
			String internalReference = pd.product().internalReference();
			if(!pd.category().type().isStockable())
				continue;
			
			if(StringUtils.isBlank(internalReference))
				continue;
			
			Articles articlesSameReference = stocks.articles().withInternalReference(internalReference);
			if(!articlesSameReference.isEmpty()){
				stockProducts.add(pd);
				articles.add(articlesSameReference.first());
			}
		}
		
		// déclencher une opération de livraison pour les articles stockables
		if(!stockProducts.isEmpty()){
			UUID livraisonOpTypeId = new TeamStockInterfaceDb(base, order.team().id(), sales).deliveryOperationId();
			OperationType livraisonOpType = stocks.operationTypes().build(livraisonOpTypeId);
			if(livraisonOpType.isNone()) {
				throw new IllegalArgumentException("Article du stock à livrer : l'opération de livraison doit être configurée pour l'équipe de ce vendeur !");
			}
			
			op = livraisonOpType.addOperation(order.id(), order.reference(), TimeConvert.toDate(order.livraisonDate(), ZoneId.systemDefault()), true, order.customer());
			
			for (int i = 0; i < stockProducts.size(); i++) {
				OrderProduct product = stockProducts.get(i);
				Article article = articles.get(i);
				
				op.addMovement(product.quantity(), article);
			}
			
			// le gestionnaire a à sa charge de valider et exécuter l'opération de livraison
		}
		
		if(execute){
			op.validate();
			op.execute();
		}
	}

	@Override
	public List<TeamStockInterface> teamStockInterfaces() throws IOException {
		
		checkAvailability();
		
		List<TeamStockInterface> list = new ArrayList<TeamStockInterface>();
		
		TeamStockInterfaceMetadata dm = TeamStockInterfaceMetadata.create();
		DomainsStore ds = base.domainsStore(dm);
				
		for (Team team : sales.teams().all()) {
			
			if(!ds.exists(team.id())){
				ds.set(team.id(), new HashMap<String, Object>());				
			}
			
			list.add(new TeamStockInterfaceDb(base, team.id(), sales));
		}
		
		return list;
	}

	@Override
	public void deliver(PurchaseOrder order) throws IOException {
		deliver(order, true);
	}
}
