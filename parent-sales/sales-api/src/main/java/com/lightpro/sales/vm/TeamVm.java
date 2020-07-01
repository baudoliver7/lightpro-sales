package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.UUID;

import com.sales.domains.api.Team;

public final class TeamVm {
	
	public final UUID id;
	public final String name;
	public final String shortName;
	public final long numberOfSellers;
	
	public TeamVm(){
		throw new UnsupportedOperationException("#TeamVm()");
	}
	
	public TeamVm(final Team origin) {
		try {
			this.id = origin.id();
	        this.name = origin.name();
	        this.shortName = origin.shortName();
	        this.numberOfSellers = origin.members().count();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
    }
}
