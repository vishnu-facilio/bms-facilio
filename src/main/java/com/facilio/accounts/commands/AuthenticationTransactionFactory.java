package com.facilio.accounts.commands;

import org.apache.commons.chain.Chain;

import com.facilio.chain.FacilioChain;

public class AuthenticationTransactionFactory {

	private static Chain getDefaultChain() {
		return FacilioChain.getTransactionChain();
    }
	
	public static Chain getOrgSignupChain() {
		Chain c = getDefaultChain();
		c.addCommand(new CreateOrgCommand());
		c.addCommand(new CreateSuperAdminCommand());
		return c;
	}
}

