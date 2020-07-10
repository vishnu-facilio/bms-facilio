package com.facilio.accounts.sso;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.xml.builder.XMLBuilder;

public class SSOUtil {

	public static String base64EncodeUrlSafe(String str) {
		return Base64.encodeBase64URLSafeString(str.getBytes());
	}
	
	public static String base64Decode(String encodedStr) {
		return new String(Base64.decodeBase64(encodedStr));
	}
	
	public static String getCurrentAppURL() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			if (request != null) {
				AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
				if (appDomain != null) {
					return "https://" + appDomain.getDomain();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return FacilioProperties.getClientAppUrl();
	}
	
	public static String getSPMetadataURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String metadataUrl = getCurrentAppURL() + "/sso/metadata/" + ssoKey;
		
		return metadataUrl;
	}
	
	public static String getSPAcsURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String acsUrl = getCurrentAppURL() + "/sso/acs/" + ssoKey;
		
		return acsUrl;
	}
	
	public static String getSPLogoutURL(AccountSSO sso) {
		
		String ssoKey = base64EncodeUrlSafe(sso.getOrgId() + "_" + sso.getId());
		
		String acsUrl = getCurrentAppURL() + "/app/logout";
		
		return acsUrl;
	}
	
	public static String getSPLoginURL() {
		
		String acsUrl = getCurrentAppURL() + "/app/login";
		
		return acsUrl;
	}
	
	public static String getDomainLoginURL(long orgId) throws Exception {
		String domainLoginURL = getCurrentAppURL() + "/domainlogin/" + IAMOrgUtil.getOrg(orgId).getDomain();
		return domainLoginURL;
	}
	
	public static String getSSOEndpoint(String domain) {
		
		String ssoEndpoint = getCurrentAppURL() + "/sso/" + domain;
		
		return ssoEndpoint;
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
}