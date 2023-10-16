package com.facilio.iam.accounts.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomainLink;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class IAMOrgUtil {
	
	public static IAMAccount signUpOrg(org.json.simple.JSONObject jObj, Locale locale) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().signUpOrg(jObj, locale));
	}

	public static boolean rollBackSignedUpOrg(long orgId, long superAdminId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().rollbackSignUpOrg(orgId, superAdminId));
	}

	public static boolean rollBackSignedUpOrgv2(long orgId, long superAdminId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().rollbackSignUpOrgv2(orgId, superAdminId));
	}

	public static int rollbackDefaultJobs(long orgId) throws Exception{
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().rollbackDefaultJobs(orgId));
	}

	public static boolean updateOrg(long orgId, Organization org) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().updateOrgv2(orgId, org));
	}

	public static boolean deleteOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().deleteOrgv2(orgId));
	}

	public static Organization getOrg(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getOrgv2(orgId));
	}

	public static Organization getOrg(String orgDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getOrgv2(orgDomain));
	}

	public static List<Organization> getOrgs() throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getOrgs());
	}

	public static List<Organization> getOrgs(List<Long> orgIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getOrgs(orgIds));
	}

	public static void updateLoggerLevel(int level, long orgId) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().updateLoggerLevel(level, orgId));
	}

	private static final int DEFAULT_URL_TIMEOUT = 3600000;
	public static Organization createOrgFromProps(Map<String, Object> prop) throws Exception {
		Organization org = FieldUtil.getAsBeanFromMap(prop, Organization.class);
		if (org.getLogoId() > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			org.setLogoUrl(fs.newPreviewFileUrl("organization", org.getLogoId(), System.currentTimeMillis() + DEFAULT_URL_TIMEOUT));
			org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));
		}
		return org;
	}

	public static boolean addOrUpdateDomainSSO(DomainSSO domainSSO) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().addOrUpdateDomainSSO(domainSSO));
	}

	public static boolean updateDomainSSOStatus(String domain, boolean status) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().updateDomainSSOStatus(domain, status));
	}
	
	public static boolean addOrUpdateAccountSSO(long orgId, AccountSSO sso) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().addOrUpdateAccountSSO(orgId, sso));
	}

	public static DomainSSO getDomainSSODetails(String domain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getDomainSSODetails(domain));
	}

	public static DomainSSO getDomainSSODetails(long domainSSOId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getDomainSSODetails(domainSSOId));
	}

	public static DomainSSO getDomainSSODetails(long orgId, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType, AppDomain.DomainType domainType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getDomainSSODetails(orgId, appDomainType, groupType, domainType));
	}
	
	public static List<Map<String, Object>> getDomainSSODetails(long orgId, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getDomainSSODetails(orgId, appDomainType, groupType));
	}
	
	public static AccountSSO getAccountSSO(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getAccountSSO(orgId));
	}

	public static List<AccountSSO> getAccountSSO(List<Long> orgIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getAccountSSO(orgIds));
	}
	
	public static AccountSSO getAccountSSO(String orgDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().getAccountSSO(orgDomain));
	}

	public static boolean deleteDomainSSO(String domain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().deleteDomainSSO(domain));
	}
	
	public static boolean deleteAccountSSO(long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().deleteAccountSSO(orgId));
	}

	public static void enableTotp(long orgId, AppDomain.GroupType groupType) throws Exception{
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().enableTotp(orgId, groupType));
		
	}

	public static void disableTotp(long orgId, AppDomain.GroupType groupType) throws Exception{
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getOrgBean().disableTotp(orgId, groupType));
		
	}

	public static List<AppDomain> getCustomAppDomain(AppDomain.AppDomainType type, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getOrgBean().getCustomAppDomain(type, orgId));
	}

	public static boolean addOrUpdateDomainLink(AppDomainLink domainLink) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getOrgBean().addOrUpdateDomainLink(domainLink));
	}

	public static AppDomainLink getDomainLink(AppDomain appDomain, AppDomainLink.LinkType linkType) throws Exception {
		AppDomainLink domainLink = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getOrgBean().getDomainLink(appDomain.getDomain(), linkType));
		if (domainLink == null) {
			if (linkType == AppDomainLink.LinkType.PRIVACY_POLICY) {
				domainLink = new AppDomainLink();
				domainLink.setName("Privacy policy");
				domainLink.setLinkType(linkType);
				domainLink.setIsExternalURL(true);
				domainLink.setExternalURL(FacilioProperties.getPrivacyPolicyURL());
			} else if (linkType == AppDomainLink.LinkType.TERMS_OF_USE) {
				domainLink = new AppDomainLink();
				domainLink.setName("Terms of service");
				domainLink.setLinkType(linkType);
				domainLink.setIsExternalURL(true);
				domainLink.setExternalURL(FacilioProperties.getTermsOfServiceURL());
			} else if (linkType == AppDomainLink.LinkType.IOS_APP) {
				if (appDomain.getAppDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio iOS Apps");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getFacilioIosURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Occupants");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getServicePortalIosURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Tenants");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getTenantPortalIosURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Vendors");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getVendorPortalIosURL());
				}
			} else if (linkType == AppDomainLink.LinkType.ANDROID_APP) {
				if (appDomain.getAppDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio Android Apps");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getFacilioAndroidURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Occupants");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getServicePortalAndroidURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Tenants");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getTenantPortalAndroidURL());
				}
				else if (appDomain.getAppDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
					domainLink = new AppDomainLink();
					domainLink.setName("Facilio - Vendors");
					domainLink.setLinkType(linkType);
					domainLink.setIsExternalURL(true);
					domainLink.setExternalURL(FacilioProperties.getVendorPortalAndroidURL());
				}
			}
		}
		return domainLink;
	}

	public static boolean deleteDomainLink(String domain, AppDomainLink.LinkType linkType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getOrgBean().deleteDomainLink(domain, linkType));
	}
}
