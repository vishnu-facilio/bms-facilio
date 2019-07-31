package com.iam.accounts.util;

import java.util.Locale;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.dto.Organization;
import com.facilio.chain.FacilioContext;
import com.iam.accounts.commands.AuthenticationTransactionFactory;
import com.iam.accounts.dto.Account;

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
	
	public static boolean updateOrg(long orgId, Organization org) throws Exception {
		return IAMUtil.getOrgBean(orgId).updateOrgv2(orgId, org);
	}
	
	public static boolean deleteOrg(long orgId) throws Exception {
		return IAMUtil.getOrgBean(orgId).deleteOrgv2(orgId);
	}
	
	public static Organization getOrg(long orgId) throws Exception {
		return IAMUtil.getOrgBean(orgId).getOrgv2(orgId);
	}
	
	public static Organization getOrg(String orgDomain) throws Exception {
		return IAMUtil.getOrgBean().getOrgv2(orgDomain);
	}
	
	public static void updateLoggerLevel(int level, long orgId) throws Exception {
		IAMUtil.getOrgBean(orgId).updateLoggerLevel(level, orgId);
	}

}
