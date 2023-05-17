package com.facilio.iam.accounts.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;
import org.json.simple.JSONObject;

public class IAMAppUtil {



	public static int deleteAppDomain(long id) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().deleteAppDomain(id));
	}
	
	public static List<AppDomain> getAppDomain(AppDomainType type, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getAppDomain(type, orgId));
	}
	
	public static AppDomain getAppDomain(String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getAppDomain(appDomain));
	}

	public static boolean isSSoEnabled(String serverName, long orgId) throws Exception {
	   AppDomain appDomain = getAppDomain(serverName);
	   boolean isCustomDomain = AppDomain.DomainType.valueOf(appDomain.getDomainType()) == AppDomain.DomainType.CUSTOM;
	   if (isCustomDomain) {
				   orgId = appDomain.getOrgId();
		   }
	   Organization org = IAMOrgUtil.getOrg(orgId);
	   AccountSSO sso = IAMOrgUtil.getAccountSSO(org.getDomain());
	   return sso != null && sso.getIsActive();
	}

	public static JSONObject getAppDomainInfo(String serverName) throws Exception {
		AppDomain appDomain = getAppDomain(serverName);
		boolean isCustomDomain = AppDomain.DomainType.valueOf(appDomain.getDomainType()) == AppDomain.DomainType.CUSTOM;
		boolean isPortalDomain = AppDomainType.FACILIO != appDomain.getAppDomainTypeEnum();
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("isCustomDomain", isCustomDomain);
		resultJSON.put("servicePortalDomain", isPortalDomain);

		if (isCustomDomain && !isPortalDomain) {
			long orgId = appDomain.getOrgId();
			Organization org = IAMOrgUtil.getOrg(orgId);
			AccountSSO sso = IAMOrgUtil.getAccountSSO(org.getDomain());
			if (sso != null && sso.getIsActive()) {
				resultJSON.put("isSSOEnabled", true);
				String ssoEndpoint = SSOUtil.getSSOEndpoint(org.getDomain());
				resultJSON.put("ssoEndPoint", ssoEndpoint);
				String ssoLogoutRequestURL = SSOUtil.getSSOLogoutRequestURL();
				if (ssoLogoutRequestURL != null) {
					resultJSON.put("isSLOEnabled", true);
				} else {
					resultJSON.put("isSLOEnabled", false);
				}
			}
			resultJSON.put("logo_url", org.getLogoUrl());
		}

		if (isPortalDomain) {
			DomainSSO domainSSODetails = IAMOrgUtil.getDomainSSODetails(appDomain.getDomain());
			if (domainSSODetails != null && domainSSODetails.getIsActive()) {
				resultJSON.put("isPortal", true);
				resultJSON.put("isSSOEnabled", true);
				String ssoEndpoint = SSOUtil.getDomainSSOEndpoint(appDomain.getDomain());
				resultJSON.put("ssoEndPoint", ssoEndpoint);
				resultJSON.put("isSLOEnabled", false); // TODO handle SLO
				resultJSON.put("showSSOLink", domainSSODetails.getShowSSOLink() != null && domainSSODetails.getShowSSOLink());
			}
		}

		return resultJSON;
	}
	
	public static AppDomain getAppDomain(long appDomainId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getAppDomain(appDomainId));
	}

	public static List<AppDomain> getAppDomainForType(int domainType, Long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getAppDomainForType(domainType, orgId));
	}

	public static String getPortalDomainUrlForUser(String username, AppDomainType appDomainType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getPortalDomainUrlForUser(username,appDomainType));
	}

	public static List<AppDomain> getPortalAppDomains() throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getPortalAppDomains());
	}
	
	public static void addAppDomains(List<AppDomain> appDomains) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().addAppDomains(appDomains));
	}
	
	public static List<AppDomain> getAppDomainsForOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getAppDomainsForOrg(orgId));
	}
	
	public static int deleteAppDomains(List<Long> appDomainIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().deleteAppDomains(appDomainIds));
	}
	
}
