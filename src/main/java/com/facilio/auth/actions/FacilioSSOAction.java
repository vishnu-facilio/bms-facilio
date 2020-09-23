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

import com.facilio.auth.cookie.FacilioCookie;
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
		
		
		if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
			setResponseCode(1);
			String message = "Invalid SSO Access.";
			resultStream = new ByteArrayInputStream(message.getBytes());
			return ERROR;
		}
		
		
		String xmlString = SSOUtil.getSPMetadataXML(sso);
		
		this.downloadStream = new ByteArrayInputStream(xmlString.getBytes());
		
		return SUCCESS;
	}
	
	public String acs() throws Exception {
		
		String message = null;
		
		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HttpServletRequest request = ServletActionContext.getRequest();
		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
		
		if (getSsoToken() != null && isValidSSOToken(getSsoToken())) {
			
			FacilioAuthAction authAction = new FacilioAuthAction();
			authAction.setSsoToken(getSsoToken());
			
			authAction.ssoSignIn();
			
			JSONObject result = authAction.getResult();
			if (result.containsKey("url")) {
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
}
