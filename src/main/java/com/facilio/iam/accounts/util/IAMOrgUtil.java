package com.facilio.iam.accounts.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.service.FacilioService;

public class IAMOrgUtil {

	public static IAMAccount signUpOrg(org.json.simple.JSONObject jObj, Locale locale) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().signUpOrg(jObj, locale));
	}
	
	public static boolean rollBackSignedUpOrg(long orgId, long superAdminId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().rollbackSignUpOrg(orgId, superAdminId));
	}
	
	public static boolean updateOrg(long orgId, Organization org) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().updateOrgv2(orgId, org));
	}
	
	public static boolean deleteOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().deleteOrgv2(orgId));
	}
	
	public static Organization getOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().getOrgv2(orgId));
	}
	
	public static Organization getOrg(String orgDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().getOrgv2(orgDomain));
	}
	
	public static List<Organization> getOrgs() throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getOrgBean().getOrgs());
	}
	
	public static void updateLoggerLevel(int level, long orgId) throws Exception {
		FacilioService.runAsService(() -> IAMUtil.getOrgBean().updateLoggerLevel(level, orgId));
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
