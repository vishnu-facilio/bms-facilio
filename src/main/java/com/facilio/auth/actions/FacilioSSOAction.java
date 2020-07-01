package com.facilio.auth.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

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
	
	public String login() throws Exception {
		
		if (getSsoToken() == null && getDomain() == null) {
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return SUCCESS;
		}
		
		AccountSSO sso = null;
		
		if (getSsoToken() != null) {
			String str = SSOUtil.base64Decode(getSsoToken());
			long orgId = Long.parseLong(str.split("_")[0]);
			long ssoId = Long.parseLong(str.split("_")[1]);
			
			sso = IAMOrgUtil.getAccountSSO(orgId);
		}
		else {
			sso = IAMOrgUtil.getAccountSSO(getDomain());
		}
		
		if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return SUCCESS;
		}
		
		SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();
		
		SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(sso), SSOUtil.getSPAcsURL(sso), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
		String samlRequest = samlClient.getSAMLRequest();
		
		setResult("samlRequest", samlRequest);
		setResult("ssoURL", ssoConfig.getLoginUrl());
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
		
		if (getSsoToken() == null) {
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return ERROR;
		}
		
		String str = SSOUtil.base64Decode(getSsoToken());
		long orgId = Long.parseLong(str.split("_")[0]);
		long ssoId = Long.parseLong(str.split("_")[1]);
		
		AccountSSO sso = IAMOrgUtil.getAccountSSO(orgId);
		
		
		if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return ERROR;
		}
		
		
		String xmlString = SSOUtil.getSPMetadataXML(sso);
		
		this.downloadStream = new ByteArrayInputStream(xmlString.getBytes());
		
		return SUCCESS;
	}
	
	public String acs() throws Exception {
		
		FacilioAuthAction authAction = new FacilioAuthAction();
		authAction.setSsoToken(getSsoToken());
		
		authAction.ssoSignIn();
		
		HttpServletResponse response= (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		
		JSONObject result = authAction.getResult();
		if (result.containsKey("url")) {
			response.sendRedirect((String) result.get("url"));
		}
		else {
			String loginUrl = SSOUtil.getSPLoginURL() + "?ssoError=" + URLEncoder.encode((String) result.get("message"), "UTF-8");
			response.sendRedirect(loginUrl);
		}
		return SUCCESS;
	}
}
