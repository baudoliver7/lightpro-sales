package com.sales.domains.api;

import java.util.Arrays;
import java.util.List;

public enum ProductCategoryType {
	
	NONE        		   (0, "Non défini", false),
	MARCHANDISE 	       (1, "Marchandise", true),
	PRODUIT_FINI		   (2, "Produit fini", true), 	
	PRODUIT_INTERMEDIAIRE  (3, "Produit intermédiaire", true),
	PRODUIT_RESIDUEL       (4, "Produit résiduel", true),
	SERVICE     		   (5, "Prestation de service", false),
	TRAVAUX     		   (6, "Travaux", false),
	ETUDES      		   (7, "Etudes", false),
	PORT_FRAIS_ACCESSOIRES (8, "Port et frais accessoires", false),
	REDUCTION_COMMERCIALE  (9, "Réduction commerciale", false),
	REDUCTION_FINANCIERE   (10, "Réduction financière", false),
	ACOMPTE_AVANCE         (11, "Acompte et avance", false);
	
	private final int id;
	private final String name;
	private final boolean isStockable;
	
	ProductCategoryType(final int id, final String name, final boolean isStockable){
		this.id = id;
		this.name = name;
		this.isStockable = isStockable;
	}
	
	public static ProductCategoryType get(int id){
		
		ProductCategoryType value = ProductCategoryType.NONE;
		for (ProductCategoryType item : ProductCategoryType.values()) {
			if(item.id() == id)
				value = item;
		}
		
		return value;
	}

	public int id(){
		return id;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean isStockable(){
		return isStockable;
	}
	
	public static boolean isExploitationProduct(ProductCategoryType type){
		return type == MARCHANDISE || type == PRODUIT_FINI || type == PRODUIT_RESIDUEL || type == PRODUIT_INTERMEDIAIRE || type == SERVICE || type == TRAVAUX || type == ETUDES;
	}
	
	public static List<ProductCategoryType> exploitationProducts(){
		return Arrays.asList(MARCHANDISE, PRODUIT_FINI, PRODUIT_RESIDUEL, PRODUIT_INTERMEDIAIRE, SERVICE, TRAVAUX, ETUDES);
	}
}
