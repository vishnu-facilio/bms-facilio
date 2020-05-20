package com.facilio.iam.accounts.util;

import java.util.List;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.service.FacilioService;

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
	
	public static AppDomain getAppDomain(long appDomainId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(appDomainId));
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
