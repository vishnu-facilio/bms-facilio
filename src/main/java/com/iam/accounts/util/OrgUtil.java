package com.iam.accounts.util;

import java.util.Locale;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.dto.Account;
import com.facilio.chain.FacilioContext;
import com.iam.accounts.commands.AuthenticationTransactionFactory;

public class OrgUtil {

	public static Account signUpOrg(org.json.simple.JSONObject jObj, Locale locale) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put("signUpObj", jObj);
		context.put("locale", locale);
		Chain c = AuthenticationTransactionFactory.getOrgSignupChain();
		c.execute(context);
		if (context.get("account") != null) {
			Account acc = (Account) context.get("account");
			return acc;
		}
		return null;
	}

}
