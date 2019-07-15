package com.facilio.fw.auth;

import com.facilio.accounts.dto.AccountUser;
import com.facilio.accounts.dto.Organization;

public class AccountV2 {

	private Organization org;
	private AccountUser user;
	
	public AccountV2() {
		// TODO Auto-generated constructor stub
	}

	public AccountV2(Organization org, AccountUser user) {
		this.org = org;
		this.user = user;
	}
	
	
}
