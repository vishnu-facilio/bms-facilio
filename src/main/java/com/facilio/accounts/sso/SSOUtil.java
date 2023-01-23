package com.facilio.accounts.sso;

import javax.servlet.http.HttpServletRequest;

import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;
import lombok.var;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.auth.SAMLServiceProvider;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.xml.builder.XMLBuilder;

import java.util.List;

public class SSOUtil {
	public static void main(String[] args) {
		SSOUtil.getDomainSSOEndpoint("bm1kcC5mYXppbGlvLmNvbQ");
	}

	public static String base64EncodeUrlSafe(String str) {
		return Base64.encodeBase64URLSafeString(str.getBytes());
	}
	
	public static String base64Decode(String encodedStr) {
		return new String(Base64.decodeBase64(encodedStr));
	}

	public static String getProtocol() {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		return RequestUtil.getProtocol(request);
	}

	public static String getLoginSuccessURL(boolean isWebView) {
		String loginSuccessURL = getCurrentAppURL();
		if (isWebView) {
			loginSuccessURL += "/auth/loginsuccess";
		}
		return loginSuccessURL;
	}
	
	public static String getCurrentAppURL() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			if (request != null) {
				AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
				if (appDomain != null) {
					StringBuilder appUrl = new StringBuilder(getProtocol())
											.append("://")
											.append(appDomain.getDomain());
					if (request.getServerPort() != 80) {
						appUrl.append(":").append(request.getServerPort());
					}
					return appUrl.toString();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return FacilioProperties.getClientAppUrl();
	}


	public static String getSPMetadataURL(DomainSSO domainSSO) throws Exception {
		var domainSSOKey = base64EncodeUrlSafe(domainSSO.getAppDomainId()+"_"+ domainSSO.getId());
		AppDomain appDomain = IAMAppUtil.getAppDomain(domainSSO.getAppDomainId());
		return getProtocol() + "://" + appDomain.getDomain() + "/dsso/metadata/" + domainSSOKey;
	}

	public static DomainSSO generateDummyDomainSSOEntry(String appDomainTypeStr) throws Exception {
		var appDomainType = AppDomain.AppDomainType.getByServiceName(appDomainTypeStr);
		List<AppDomain> appDomain = IAMAppUtil.getAppDomain(appDomainType, AccountUtil.getCurrentOrg().getOrgId());
		DomainSSO domainSSO = IAMOrgUtil.getDomainSSODetails(appDomain.get(0).getDomain());
		if (domainSSO == null) {
			domainSSO = new DomainSSO();
			domainSSO.setIsActive(false);
			domainSSO.setName("SAML");
			domainSSO.setAppDomainId(appDomain.get(0).getId());
			domainSSO.setCreatedTime(System.currentTimeMillis());
			domainSSO.setModifiedTime(System.currentTimeMillis());
			domainSSO.setCreatedBy(AccountUtil.getCurrentUser().getId());
			domainSSO.setModifiedBy(AccountUtil.getCurrentUser().getId());
			IAMOrgUtil.addOrUpdateDomainSSO(domainSSO);
		}
		return domainSSO;
	}

	public static AccountSSO generateDummyAccountSSOEntry() throws Exception {
		AccountSSO sso = IAMOrgUtil.getAccountSSO(AccountUtil.getCurrentOrg().getOrgId());
		if (sso == null) {
			sso = new AccountSSO();
			sso.setIsActive(false);
			sso.setName("SAML");
			sso.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			sso.setCreatedTime(System.currentTimeMillis());
			sso.setModifiedTime(System.currentTimeMillis());
			sso.setModifiedBy(AccountUtil.getCurrentUser().getId());
			sso.setCreatedBy(AccountUtil.getCurrentUser().getId());
			IAMOrgUtil.addOrUpdateAccountSSO(AccountUtil.getCurrentOrg().getOrgId(), sso);
		}
		return sso;
	}
	
	public static String getSPMetadataURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String metadataUrl = getCurrentAppURL() + "/sso/metadata/" + ssoKey;
		
		return metadataUrl;
	}

	public static String getSPAcsURL(DomainSSO domainSSO) throws Exception {
		String ssoKey = base64EncodeUrlSafe(domainSSO.getAppDomainId() + "_" + domainSSO.getId());
		AppDomain appDomain = IAMAppUtil.getAppDomain(domainSSO.getAppDomainId());
		return getProtocol() + "://" + appDomain.getDomain() + "/dsso/acs/" + ssoKey;
	}
	
	public static String getSPAcsURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String acsUrl = getCurrentAppURL() + "/sso/acs/" + ssoKey;
		
		return acsUrl;
	}

	public static String getSPLogoutURL() {
		return getCurrentAppURL() + "/app/logout";
	}

	public static String getSPLogoutURL(DomainSSO domainSSO) throws Exception {
		AppDomain appDomain = IAMAppUtil.getAppDomain(domainSSO.getAppDomainId());
		return getProtocol() + "://" + appDomain.getDomain() + "/app/logout";
	}
	
	public static String getSPLogoutURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String acsUrl = getCurrentAppURL() + "/app/logout";
		
		return acsUrl;
	}
	
	public static String getSPLoginURL(boolean isWebView) {
		String loginUrl = getCurrentAppURL() + "/app/login";
		if (isWebView) {
			loginUrl = getCurrentAppURL() + "/app/mobile/login";
		}
		return loginUrl;
	}
	
	public static String getDomainLoginURL(long orgId) throws Exception {
		String domainLoginURL = getCurrentAppURL() + "/domainlogin/" + IAMOrgUtil.getOrg(orgId).getDomain();
		return domainLoginURL;
	}

	public static String getDomainSSOEndpoint(String domain) {
		return getProtocol() + "://" + domain + "/dsso/" + base64EncodeUrlSafe(domain);
	}
	
	public static String getSSOEndpoint(String domain) {
		
		String ssoEndpoint = getCurrentAppURL() + "/sso/" + domain;
		
		return ssoEndpoint;
	}

	public static String getSPMetadataXML(DomainSSO domainSSO) throws Exception {
		String spEntityId = getSPMetadataURL(domainSSO);
		String spAcsUrl = getSPAcsURL(domainSSO);
		String spLogoutUrl = getSPLogoutURL(domainSSO);

		XMLBuilder builder = XMLBuilder.create("md:EntityDescriptor").attr("xmlns:md", "urn:oasis:names:tc:SAML:2.0:metadata").attr("entityID", spEntityId);

		XMLBuilder spsElm = builder.element("md:SPSSODescriptor").attr("AuthnRequestsSigned", "false").attr("WantAssertionsSigned", "true").attr("protocolSupportEnumeration", "urn:oasis:names:tc:SAML:2.0:protocol");

		spsElm.element("md:NameIDFormat").text("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress").p()
				.element("md:AssertionConsumerService").attr("Binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").attr("Location", spAcsUrl).attr("index", "0").attr("isDefault", "true").p()
				.element("md:SingleLogoutService").attr("Binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").attr("Location", spLogoutUrl).p();

		return builder.getAsXMLString();
	}
	
	public static String getSPMetadataXML(AccountSSO sso) throws Exception {
		
		String spEntityId = getSPMetadataURL(sso);
		String spAcsUrl = getSPAcsURL(sso);
		String spLogoutUrl = getSPLogoutURL(sso);
		
		XMLBuilder builder = XMLBuilder.create("md:EntityDescriptor").attr("xmlns:md", "urn:oasis:names:tc:SAML:2.0:metadata").attr("entityID", spEntityId);
		
		XMLBuilder spsElm = builder.element("md:SPSSODescriptor").attr("AuthnRequestsSigned", "false").attr("WantAssertionsSigned", "true").attr("protocolSupportEnumeration", "urn:oasis:names:tc:SAML:2.0:protocol");
		
		spsElm.element("md:NameIDFormat").text("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress").p()
			.element("md:AssertionConsumerService").attr("Binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").attr("Location", spAcsUrl).attr("index", "0").attr("isDefault", "true").p()
			.element("md:SingleLogoutService").attr("Binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").attr("Location", spLogoutUrl).p();
		
		return builder.getAsXMLString();
	}
	
	public static String getSSOLogoutRequestURL() throws Exception {
		
		if (AccountUtil.getCurrentOrg() != null) {
			
			long orgId = AccountUtil.getCurrentOrg().getId();
		
			AccountSSO sso = IAMOrgUtil.getAccountSSO(orgId);
			if (sso.getIsActive()) {
		
				SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();
			
				if (ssoConfig.getLogoutUrl() != null) {
				
					SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(sso), SSOUtil.getSPAcsURL(sso), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
					String logoutRequest = samlClient.getLogoutRequest();
			
					URIBuilder builder = new URIBuilder(ssoConfig.getLogoutUrl());
					builder.addParameter("SAMLRequest", logoutRequest);
			
					String logoutURL = builder.build().toURL().toString();
					return logoutURL;
				}
			}
		}
		return null;
	}
}