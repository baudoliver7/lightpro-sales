package com.sales.domains.impl;

import java.io.IOException;

import com.infrastructure.core.GuidKeyEntityNone;
import com.sales.domains.api.Customers;
import com.sales.domains.api.Invoices;
import com.sales.domains.api.ModulePdv;
import com.sales.domains.api.ModulePdvs;
import com.sales.domains.api.Payments;
import com.sales.domains.api.ProductCategories;
import com.sales.domains.api.Products;
import com.sales.domains.api.Provisions;
import com.sales.domains.api.PurchaseOrders;
import com.sales.domains.api.Remise;
import com.sales.domains.api.Sales;
import com.sales.domains.api.SalesInterfacage;
import com.sales.domains.api.Sellers;
import com.sales.domains.api.Teams;
import com.securities.api.Company;
import com.securities.api.Contacts;
import com.securities.api.Currency;
import com.securities.api.Feature;
import com.securities.api.FeatureSubscribed;
import com.securities.api.Features;
import com.securities.api.Indicators;
import com.securities.api.Log;
import com.securities.api.Membership;
import com.securities.api.MesureUnits;
import com.securities.api.Module;
import com.securities.api.ModuleType;
import com.securities.api.NumberValueType;
import com.securities.api.PaymentModes;
import com.securities.api.Taxes;
import com.securities.impl.CompanyNone;

public final class SalesNone extends GuidKeyEntityNone<Sales>  implements Sales {

	@Override
	public void activate(boolean arg0) throws IOException {
		
	}

	@Override
	public Company company() throws IOException {
		return new CompanyNone();
	}

	@Override
	public String description() throws IOException {
		return null;
	}

	@Override
	public Features featuresProposed() throws IOException {

		return null;
	}

	@Override
	public boolean isActive() {

		return false;
	}

	@Override
	public boolean isInstalled() {

		return false;
	}

	@Override
	public boolean isSubscribed() {

		return false;
	}

	@Override
	public String name() throws IOException {

		return null;
	}

	@Override
	public int order() throws IOException {

		return 0;
	}

	@Override
	public String shortName() throws IOException {

		return null;
	}

	@Override
	public ModuleType type() throws IOException {

		return null;
	}

	@Override
	public ProductCategories productCategories() {

		return null;
	}

	@Override
	public Products products() {

		return null;
	}

	@Override
	public MesureUnits mesureUnits() throws IOException {

		return null;
	}

	@Override
	public Taxes taxes() throws IOException {

		return null;
	}

	@Override
	public PaymentModes paymentModes() throws IOException {

		return null;
	}

	@Override
	public PurchaseOrders purchases() {

		return null;
	}

	@Override
	public Invoices invoices() {

		return null;
	}

	@Override
	public Membership membership() throws IOException {

		return null;
	}

	@Override
	public Customers customers() {

		return null;
	}

	@Override
	public Sellers sellers() {

		return null;
	}

	@Override
	public Contacts contacts() throws IOException {
		return null;
	}

	@Override
	public Payments payments() {
		return null;
	}

	@Override
	public Provisions provisions() {
		return null;
	}

	@Override
	public Teams teams() {
		return null;
	}

	@Override
	public ModulePdvs modulePdvs() throws IOException {
		return null;
	}

	@Override
	public ModulePdv modulePointDeVenteDirecte() {
		return new ModulePdvNone();
	}

	@Override
	public com.sales.domains.api.RRRSettings RRRSettings() throws IOException {
		return null;
	}

	@Override
	public Remise remiseWithCurrency(double value, NumberValueType valueType) throws IOException {
		return new RemiseNone();
	}

	@Override
	public Features featuresAvailable() throws IOException {
		
		return null;
	}

	@Override
	public Features featuresSubscribed() throws IOException {
		
		return null;
	}

	@Override
	public Indicators indicators() throws IOException {
		
		return null;
	}

	@Override
	public Module install() throws IOException {
		
		return null;
	}

	@Override
	public Module subscribe() throws IOException {
		
		return null;
	}

	@Override
	public FeatureSubscribed subscribeTo(Feature arg0) throws IOException {
		
		return null;
	}

	@Override
	public Module uninstall() throws IOException {
		
		return null;
	}

	@Override
	public Module unsubscribe() throws IOException {
		
		return null;
	}

	@Override
	public void unsubscribeTo(Feature arg0) throws IOException {
		
		
	}

	@Override
	public SalesInterfacage interfacage() {
		return null;
	}

	@Override
	public Log log() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Remise remise(double value, NumberValueType valueType, Currency currency) throws IOException {
		throw new UnsupportedOperationException("Opération non supportée !");
	}
}
