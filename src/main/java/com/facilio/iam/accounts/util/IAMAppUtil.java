package com.facilio.iam.accounts.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.service.FacilioService;

public class IAMAppUtil {

	public static long addAppDomain(String domain, int appDomainType, int groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().addAppDomain(domain, groupType, appDomainType));
	}
	
	public static int deleteAppDomain(long id) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteAppDomain(id));
	}
	
	public static AppDomain getAppDomain(AppDomainType type) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(type));
	}
	
	public static AppDomain getAppDomain(String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(appDomain));
	}
	
	public static AppDomain getAppDomain(long appDomainId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(appDomainId));
	}
	
}
