package com.facilio.iam.accounts.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.service.FacilioService;
import org.json.simple.JSONObject;

public class IAMAppUtil {



	public static int deleteAppDomain(long id) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteAppDomain(id));
	}
	
	public static List<AppDomain> getAppDomain(AppDomainType type, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(type, orgId));
	}
	
	public static AppDomain getAppDomain(String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(appDomain));
	}

	public static JSONObject getAppDomainType(String serverName) throws Exception {
		AppDomain appDomain = getAppDomain(serverName);
		boolean isCustomDomain = AppDomain.DomainType.valueOf(appDomain.getDomainType()) == AppDomain.DomainType.CUSTOM;
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("isCustomDomain", isCustomDomain);
		if (isCustomDomain) {
			long orgId = appDomain.getOrgId();
			Organization org = IAMOrgUtil.getOrg(orgId);
			AccountSSO sso = IAMOrgUtil.getAccountSSO(org.getDomain());
			if (sso != null && sso.getIsActive()) {
				resultJSON.put("isSSOEnabled", true);
				String ssoEndpoint = SSOUtil.getSSOEndpoint(org.getDomain());
				resultJSON.put("ssoEndPoint", ssoEndpoint);
			}
			resultJSON.put("logo_url", org.getLogoUrl());
		}

		return resultJSON;
	}
	
	public static AppDomain getAppDomain(long appDomainId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(appDomainId));
	}

	public static List<AppDomain> getAppDomainForType(int domainType, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomainForType(domainType, orgId));
	}

	public static List<AppDomain> getPortalAppDomains() throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getPortalAppDomains());
	}
	
	public static void addAppDomains(List<AppDomain> appDomains) throws Exception {
		FacilioService.runAsService(() -> IAMUtil.getUserBean().addAppDomains(appDomains));
	}
	
	public static List<AppDomain> getAppDomainsForOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomainsForOrg(orgId));
	}
	
	public static int deleteAppDomains(List<Long> appDomainIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteAppDomains(appDomainIds));
	}
	
}
