package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.AppDomain;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;

public class PortalInfoContext  {
	
	private long portalId = -1;
	private long orgId = -1;
	private int portalType = 0;
	private boolean signup_allowed = false;
	private boolean gmailLogin_allowed = false;
	private boolean is_public_create_allowed = false;
	private boolean is_anyDomain_allowed = false;
	private boolean captcha_enabled = false;
	private String customDomain;
	private String whiteListed_domains;
	private boolean saml_enabled = true;
	//private String login_url = "http://orgdomainname."+PortalAuthInterceptor.portaldomain+"/idplogin";
	private String logout_url = "http://orgdomainname."+PortalAuthInterceptor.getPortalDomain() +"/idplogout";
	private String password_url = "http://orgdomainname."+PortalAuthInterceptor.getPortalDomain() +"/idppassword";
	private long publicKey = -1;
	private File publicKeyFile;
	private String algorithm  = null;
	
	public String toString()
	{
		String objStr = "portalId : "+portalId+", orgId :"+orgId+", portalType :"+portalType+", signup_allowed :"+signup_allowed+", gmailLogin_allowed: "+gmailLogin_allowed+", is_public_create_allowed :"+is_public_create_allowed+", is_anyDomain_allowed : "+is_anyDomain_allowed+", whiteListed_domains :"+whiteListed_domains+", saml_enabled:"+saml_enabled+", login_url :"+getLogin_url()+", logout_url:"+logout_url+", password_url:"+password_url;		
		return objStr;
	}
	
	public long getPortalId() {
		return portalId;
	}
	public void setPortalId(long portalId) {
		this.portalId = portalId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public int getPortalType() {
		return portalType;
	}
	public void setPortalType(int portalType) {
		this.portalType = portalType;
	}
	public boolean isSignup_allowed() {
		return signup_allowed;
	}
	public void setSignup_allowed(boolean signup_allowed) {
		this.signup_allowed = signup_allowed;
	}
	public boolean isGmailLogin_allowed() {
		return gmailLogin_allowed;
	}
	public void setGmailLogin_allowed(boolean gmailLogin_allowed) {
		this.gmailLogin_allowed = gmailLogin_allowed;
	}
	public boolean is_public_create_allowed() {
		return is_public_create_allowed;
	}
	public boolean getIs_public_create_allowed()
	{
		return  is_public_create_allowed();
	}
	public void setIs_public_create_allowed(boolean is_public_create_allowed) {
		this.is_public_create_allowed = is_public_create_allowed;
	}
	public boolean is_anyDomain_allowed() {
		return is_anyDomain_allowed;
	}
	public boolean getIs_anyDomain_allowed() {
		return is_anyDomain_allowed;
	}
	public void setIs_anyDomain_allowed(boolean is_anyDomain_allowed) {
		this.is_anyDomain_allowed = is_anyDomain_allowed;
	}
	public boolean isCaptcha_enabled() {
		return captcha_enabled;
	}

	public void setCaptcha_enabled(boolean captcha_enabled) {
		this.captcha_enabled = captcha_enabled;
	}
	public String getWhiteListed_domains() {
		return whiteListed_domains;
	}
	public void setWhiteListed_domains(String whiteListed_domains) {
		this.whiteListed_domains = whiteListed_domains;
	}
	public boolean isSaml_enabled() {
		return saml_enabled;
	}
	public void setSaml_enabled(boolean saml_enabled) {
		this.saml_enabled = saml_enabled;
	}
	public String getLogin_url() {
		// TODO - Remove this after moving this to Generic Handling (getApplicationDetails() chain)
		Organization currentOrg = AccountUtil.getCurrentOrg();
		String login_url = FacilioProperties.getAppProtocol() + "://" + currentOrg.getDomain() + "." + PortalAuthInterceptor.getPortalDomain() + "/";
		if (currentOrg.getOrgType() == Organization.OrgType.SANDBOX.getIndex()) {
			try {
				Organization productionOrg = AccountUtil.getOrgBean().getOrg(currentOrg.getProductionOrgId());
				String sandboxOccupantAppDomain = FacilioProperties.getSandboxOccupantAppDomain();
				login_url = FacilioProperties.getAppProtocol() + "://" + productionOrg.getDomain() + "." + sandboxOccupantAppDomain + "/";
			} catch (Exception e) {

			}
		}
		return login_url;
	}
	
	public String getLogout_url() {
		return logout_url;
	}
	public void setLogout_url(String logout_url) {
		this.logout_url = logout_url;
	}
	public String getPassword_url() {
		return password_url;
	}
	public void setPassword_url(String password_url) {
		this.password_url = password_url;
	}

	public long getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(long publicKey) {
		this.publicKey = publicKey;
	}

	public File getPublicKeyFile() {
		return publicKeyFile;
	}

	public void setPublicKeyFile(File publicKeyFile) {
		this.publicKeyFile = publicKeyFile;
	}

	public String getCustomDomain() {
		return customDomain;
	}

	public void setCustomDomain(String customDomain) {
		this.customDomain = customDomain;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	
}
