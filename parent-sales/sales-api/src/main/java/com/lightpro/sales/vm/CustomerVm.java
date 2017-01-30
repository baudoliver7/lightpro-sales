package com.lightpro.sales.vm;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.sales.domains.api.Customer;

public class CustomerVm {
	
	private final transient Customer origin;
	
	public CustomerVm(){
		throw new UnsupportedOperationException("#CustomerVm()");
	}
	
	public CustomerVm(final Customer origin) {
        this.origin = origin;
    }
	
	@JsonGetter
	public UUID getId(){
		return origin.id();
	}
	
	@JsonGetter
	public String getFirstName() throws IOException {
		return origin.firstName();
	}
	
	@JsonGetter
	public String getLastName() throws IOException {
		return origin.lastName();
	}
	
	@JsonGetter
	public String getFullName() throws IOException {
		return origin.fullName();
	}
	
	@JsonGetter
	public String getSex() throws IOException{
		return origin.sex().name();
	}
	
	@JsonGetter
	public String getAddress() throws IOException {
		return origin.address();
	}
	
	@JsonGetter
	public Date getBirthDate() throws IOException {
		return origin.birthDate();
	}
	
	@JsonGetter
	public String getTel1() throws IOException {
		return origin.tel1();
	}
	
	@JsonGetter
	public String getTel2() throws IOException{
		return origin.tel2();
	}
	
	@JsonGetter
	public String getEmail() throws IOException {
		return origin.email();
	}
	
	@JsonGetter
	public String getPhoto() throws IOException {
		return origin.photo();
	}
}
