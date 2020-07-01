package com.sales.domains.api;

import java.io.IOException;

import com.securities.api.Contact;

public interface Seller extends Contact {
	Team team() throws IOException;	
	void changeTeam(Team newTeam) throws IOException;
}
