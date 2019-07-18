package com.iam.accounts.commands;

import org.apache.commons.chain.Chain;

import com.facilio.chain.FacilioChain;

public class AuthenticationTransactionFactory {

	private static Chain getDefaultChain() {
		return FacilioChain.getTransactionChain();
	}
	

}
