package com.facilio.auth.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facilio.accounts.sso.DomainSSO;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.actions.PeopleAction;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.sso.SamlSSOConfig;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.fw.auth.SAMLServiceProvider;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.opensymphony.xwork2.ActionContext;
import org.json.simple.parser.JSONParser;

public class FacilioSSOAction extends FacilioAction {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(FacilioSSOAction.class.getName());

	private String ssoToken;
	private String domain;
	private AccountSSO sso;
	private String relay;

	public String getSsoToken() {
		return ssoToken;
	}
	public void setSsoToken(String ssoToken) {
		this.ssoToken = ssoToken;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public AccountSSO getSso() {
		return sso;
	}
	public void setSso(AccountSSO sso) {
		this.sso = sso;
	}
	public String getRelay() {
		return relay;
	}
	public void setRelay(String relay) {
		this.relay = relay;
	}

	private InputStream resultStream;
	public InputStream getResultStream() {
		return resultStream;
	}


	public String domainLogin() throws Exception {
		if (getDomain() == null) {
			setResponseCode(1);
			String message = "Invalid domain or Single Sign-On is not enabled for this domain.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		DomainSSO domainSSO = IAMOrgUtil.getDomainSSODetails(SSOUtil.base64Decode(getDomain()));
		if (domainSSO == null || domainSSO.getIsActive() == null || !domainSSO.getIsActive()) {
			setResponseCode(1);
			LOGGER.log(Level.SEVERE, "Invalid domain or Single Sign-On is not enabled for this domain. " + (getDomain() == null ? " empty domain " : SSOUtil.base64Decode(getDomain())));
			String message = "Invalid domain or Single Sign-On is not enabled for this domain.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		SamlSSOConfig ssoConfig = (SamlSSOConfig) domainSSO.getSSOConfig();

		SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(domainSSO), SSOUtil.getSPAcsURL(domainSSO), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
		String samlRequest = samlClient.getSAMLRequest();

		String relayState = (getRelay() != null && !getRelay().trim().isEmpty()) ? getRelay() : null;

		URIBuilder builder = new URIBuilder(ssoConfig.getLoginUrl());
		builder.addParameter("SAMLRequest", samlRequest);
		if (relayState != null) {
			builder.addParameter("RelayState", relayState);
		}

		String ssoURL = builder.build().toURL().toString();

		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		response.sendRedirect(ssoURL);
		return SUCCESS;
	}

	public String login() throws Exception {
		if (getDomain() == null) {
			setResponseCode(1);
			String message = "Invalid domain or Single Sign-On is not enabled for this domain.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		AccountSSO sso = IAMOrgUtil.getAccountSSO(getDomain());
		if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
			setResponseCode(1);
			String message = "Invalid domain or Single Sign-On is not enabled for this domain.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();

		SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(sso), SSOUtil.getSPAcsURL(sso), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
		String samlRequest = samlClient.getSAMLRequest();

		String relayState = (getRelay() != null && !getRelay().trim().isEmpty()) ? getRelay() : null;

		URIBuilder builder = new URIBuilder(ssoConfig.getLoginUrl());
		builder.addParameter("SAMLRequest", samlRequest);
		if (relayState != null) {
			builder.addParameter("RelayState", relayState);
		}

		String ssoURL = builder.build().toURL().toString();

		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		response.sendRedirect(ssoURL);
		return SUCCESS;
	}

	private InputStream downloadStream;

	public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}

	@Getter
	@Setter
	private String appDomainType;

	public String downloadDomainMetaData() throws Exception {
		DomainSSO domainSSO = SSOUtil.generateDummyDomainSSOEntry(appDomainType);
		setSsoToken(SSOUtil.base64EncodeUrlSafe(AccountUtil.getCurrentOrg().getOrgId()+"_"+domainSSO.getId()));
		return domainMetaData();
	}

	public String domainMetaData() throws Exception {
		if (getSsoToken() == null || !isValidSSOToken(getSsoToken())) {
			setResponseCode(1);
			String message = "Invalid SSO Access.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		String str = SSOUtil.base64Decode(getSsoToken());
		long appDomainId = Long.parseLong(str.split("_")[0]);
		long dSsoId = Long.parseLong(str.split("_")[1]);

		DomainSSO domainSSODetails = IAMOrgUtil.getDomainSSODetails(dSsoId);

		if (domainSSODetails == null) {
			setResponseCode(1);
			String message = "Invalid SSO Access.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}


		String xmlString = SSOUtil.getSPMetadataXML(domainSSODetails);

		this.downloadStream = new ByteArrayInputStream(xmlString.getBytes());

		return SUCCESS;
	}

	public String downloadMetadata() throws Exception {
		AccountSSO accountSSO = SSOUtil.generateDummyAccountSSOEntry();
		setSsoToken(SSOUtil.base64EncodeUrlSafe(accountSSO.getOrgId()+"_"+accountSSO.getId()));
		return metadata();
	}

	public String metadata() throws Exception {

		if (getSsoToken() == null || !isValidSSOToken(getSsoToken())) {
			setResponseCode(1);
			String message = "Invalid SSO Access.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}

		String str = SSOUtil.base64Decode(getSsoToken());
		long orgId = Long.parseLong(str.split("_")[0]);
		long ssoId = Long.parseLong(str.split("_")[1]);

		AccountSSO sso = IAMOrgUtil.getAccountSSO(orgId);


		if (sso == null) {
			setResponseCode(1);
			String message = "Invalid SSO Access.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}


		String xmlString = SSOUtil.getSPMetadataXML(sso);

		this.downloadStream = new ByteArrayInputStream(xmlString.getBytes());

		return SUCCESS;
	}

	public String domainAcs() throws Exception {
		String message = null;

		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HttpServletRequest request = ServletActionContext.getRequest();
		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");

		if (getSsoToken() != null && isValidSSOToken(getSsoToken())) {
			if (isIdentityACS(request.getParameter("RelayState"))) {
				// redirecting the SAML ACS to identity server if the saml login initiated from identity server
				request.setAttribute("IdentityACSURL", SSOUtil.getCurrentAppURL() + "/identity/sso/acs/" + getSsoToken());
				request.setAttribute("SAMLResponse", request.getParameter("SAMLResponse"));
				request.setAttribute("RelayState", request.getParameter("RelayState"));
				return "identity-acs";
			}

			FacilioAuthAction authAction = new FacilioAuthAction();
			authAction.setSsoToken(getSsoToken());
			authAction.domainSSOSignIn();
			JSONObject result = authAction.getResult();
			if (result.containsKey("url")) {
				LOGGER.log(Level.SEVERE, "[redirect url] " + result.get("url"));
				response.sendRedirect((String) result.get("url"));
				return SUCCESS;
			}
			else {
				message = (String) result.get("message");
			}
		}
		else {
			message = "Invalid SSO Access.";
		}

		String loginUrl = SSOUtil.getSPLoginURL("true".equalsIgnoreCase(isWebView)) + "?ssoError=" + URLEncoder.encode(message, "UTF-8");
		response.sendRedirect(loginUrl);
		return SUCCESS;
	}

	public String acs() throws Exception {

		String message = null;

		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HttpServletRequest request = ServletActionContext.getRequest();
		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");

		if (getSsoToken() != null && isValidSSOToken(getSsoToken())) {
			if (isIdentityACS(request.getParameter("RelayState"))) {
				// redirecting the SAML ACS to identity server if the saml login initiated from identity server
				request.setAttribute("IdentityACSURL", SSOUtil.getCurrentAppURL() + "/identity/sso/acs/" + getSsoToken());
				request.setAttribute("SAMLResponse", request.getParameter("SAMLResponse"));
				request.setAttribute("RelayState", request.getParameter("RelayState"));
				return "identity-acs";
			}

			FacilioAuthAction authAction = new FacilioAuthAction();
			authAction.setSsoToken(getSsoToken());

			authAction.ssoSignIn();

			JSONObject result = authAction.getResult();
			if (result.containsKey("url")) {
				FacilioCookie.addLoggedInCookie(response);
				response.sendRedirect((String) result.get("url"));
				return SUCCESS;
			}
			else {
				message = (String) result.get("message");
			}
		}
		else {
			message = "Invalid SSO Access.";
		}

		String loginUrl = SSOUtil.getSPLoginURL("true".equalsIgnoreCase(isWebView)) + "?ssoError=" + URLEncoder.encode(message, "UTF-8");
		response.sendRedirect(loginUrl);
		return SUCCESS;
	}

	private boolean isValidSSOToken(String ssoToken) {
		try {
			String str = SSOUtil.base64Decode(ssoToken);
			long orgId = Long.parseLong(str.split("_")[0]);
			long ssoId = Long.parseLong(str.split("_")[1]);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isIdentityACS(String relayState) {
		try {
			if (StringUtils.isNotEmpty(relayState)) {
				String decodedStr = new String(Base64.decodeBase64(relayState));
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(decodedStr);
				if (jsonObject.containsKey("identity_acs")) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
