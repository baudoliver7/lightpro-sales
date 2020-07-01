package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.securities.api.PaymentMode;

public final class PaymentModeVm {
	
	public final UUID id;
	public final String name;
	public final int typeId;
	public final String type;
	public final boolean active;
	
	public PaymentModeVm(){
		throw new UnsupportedOperationException("#PaymentModeVm()");
	}
		
	public PaymentModeVm(PaymentMode origin){
		try {
			this.id = origin.id();
	        this.name = origin.name();
	        this.typeId = origin.type().id();
	        this.type = origin.type().toString();
	        this.active = origin.active();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
}
