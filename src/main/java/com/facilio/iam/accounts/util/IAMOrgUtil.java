package com.facilio.iam.accounts.util;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.chain.FacilioContext;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.iam.accounts.commands.AuthenticationTransactionFactory;
import com.facilio.modules.FieldUtil;

public class IAMOrgUtil {

	public static IAMAccount signUpOrg(org.json.simple.JSONObject jObj, Locale locale) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put("signUpObj", jObj);
		context.put("locale", locale);
		Chain c = AuthenticationTransactionFactory.getOrgSignupChain();
		c.execute(context);
		if (context.get("account") != null) {
			IAMAccount acc = (IAMAccount) context.get("account");
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
	
	public static Organization createOrgFromProps(Map<String, Object> prop) throws Exception {
		Organization org = FieldUtil.getAsBeanFromMap(prop, Organization.class);
		if (org.getLogoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(org.getId());
			org.setLogoUrl(fs.getPrivateUrl(org.getLogoId(), false));
			org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));
		}
		return org;
	}
	

}
