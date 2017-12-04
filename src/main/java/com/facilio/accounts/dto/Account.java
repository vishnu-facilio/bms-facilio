package com.facilio.accounts.dto;

public class Account {
	
	private Organization org;
	private User user;
	
	public Account(Organization org, User user) {
		this.org = org;
		this.user = user;
	}
	
	public Organization getOrg() {
		return this.org;
	}
	
	public void setOrg(Organization org) {
		this.org = org;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
