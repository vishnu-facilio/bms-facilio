package com.facilio.bmsconsole.interceptors;

import java.text.MessageFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


public class DataSourceInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 1L;
	private static HashMap customdomains = null; 
	 
	private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(DataSourceInterceptor.class);


	@Override
	public void init() {
		super.init();
	}

	@Override
	@WithSpan
	public String intercept(ActionInvocation invocation) throws Exception {
		long time = System.currentTimeMillis();
		HttpServletRequest request = ServletActionContext.getRequest();
		Object authMethod =  request.getAttribute("authMethod");
		if (authMethod != null) {
			return invocation.invoke();
		}
		IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");

		//sandbox handling
		if (SandboxAPI.isSandboxSubDomain(request.getServerName())) {
			Organization organization = null;
			if (iamAccount != null) {
				String sandboxName = (String) request.getAttribute(RequestUtil.ORG_SUBDOMAIN);
				String reqUri = FilenameUtils.normalize(request.getRequestURI(), true);

				if (StringUtils.isNotEmpty(sandboxName)) {
					organization = IAMUserUtil.getOrg(sandboxName, iamAccount.getUser().getUid(), Organization.OrgType.SANDBOX);
				} else if (reqUri.equals("/api/v2/application/fetchDetails")) {
					organization = SandboxAPI.getAccessibleSandboxOrg(iamAccount.getUser().getUid());
					sandboxName = organization != null ? organization.getDomain() : null;
				}

				FacilioUtil.throwIllegalArgumentException(organization == null, "Invalid Sandbox details passed");

				//validate sandbox status
				AccountUtil.setCurrentAccount(organization.getProductionOrgId());
				SandboxConfigContext sandboxConfig = SandboxAPI.getSandboxByDomainName(sandboxName);
				AccountUtil.cleanCurrentAccount();

				FacilioUtil.throwIllegalArgumentException(sandboxConfig == null, "Invalid Sandbox details passed");
				FacilioUtil.throwIllegalArgumentException(!sandboxConfig.getStatusEnum().equals(SandboxConfigContext.SandboxStatus.ACTIVE), "Sandbox creation in process");

				iamAccount.setOrg(organization);
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account org in normal auth => {0}", iamAccount.getOrg() == null ? -1 : iamAccount.getOrg().getId()), request);
			}
		}
		//remote screen handling
		else if(request.getAttribute("remoteScreen") != null && request.getAttribute("remoteScreen") instanceof RemoteScreenContext) {
			RemoteScreenContext remoteScreen = (RemoteScreenContext) request.getAttribute("remoteScreen");
			Organization org = IAMOrgUtil.getOrg(remoteScreen.getOrgId());
			if(org != null) {
				IAMAccount account = new IAMAccount(org, null);
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account org in remoteScreen => {0}", account.getOrg() == null ? -1 : account.getOrg().getId()), request);
				request.setAttribute("iamAccount", account);
			}
			AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "ORG is null in remote screen", request);
		}
		//connected device(new tv and kiosk) handling
		else if(request.getAttribute("device") != null && request.getAttribute("device") instanceof ConnectedDeviceContext) {
			ConnectedDeviceContext connectedDevice = (ConnectedDeviceContext) request.getAttribute("device");
			Organization org = IAMOrgUtil.getOrg(connectedDevice.getOrgId());
			if(org != null) {
				IAMAccount account = new IAMAccount(org, null);
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account org in deevice auth => {0}", account.getOrg() == null ? -1 : account.getOrg().getId()), request);
				request.setAttribute("iamAccount", account);
			}
			AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "ORG is null in device auth", request);
		}
		else {
			Organization organization = null;

			AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
			boolean isSandboxDomain = SandboxAPI.isSandboxSubDomain(request.getServerName());
			AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("App name => {0}", appDomain == null ? "null app domain" : appDomain.getDomain() ), request);
			if (iamAccount != null) {
				String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
				if (currentOrgDomain == null) {
					currentOrgDomain = request.getHeader("X-Current-Org"); 
				}
				//the third check can be removed..It is added now for sutherland demo (894 is the custom domain id for org 343 in production). That's added to allow multiple orgs with that custom domain
				if(appDomain != null && appDomain.getOrgId() > 0 && !isSandboxDomain && (!FacilioProperties.isProduction() || appDomain.getId() != 894l)) {
					organization = IAMOrgUtil.getOrg(appDomain.getOrgId());
				}
				else if (StringUtils.isNotBlank(currentOrgDomain)) {
					organization = IAMUserUtil.getOrg(currentOrgDomain, iamAccount.getUser().getUid());
				}
				else {
					organization = IAMUserUtil.getDefaultOrg(iamAccount.getUser().getUid(), isSandboxDomain ? Organization.OrgType.SANDBOX : Organization.OrgType.PRODUCTION);
				}
				iamAccount.setOrg(organization);
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account org in normal auth => {0}", iamAccount.getOrg() == null ? -1 : iamAccount.getOrg().getId()), request);
			}
			else {
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "Else of app domain with no iam account", request);
				organization = IAMOrgUtil.getOrg(appDomain.getOrgId());
              	if(organization != null) {
                	iamAccount = new IAMAccount(organization, null);
                	request.setAttribute("iamAccount", iamAccount);
					AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account org from app domain => {0}", iamAccount.getOrg() == null ? -1 : iamAccount.getOrg().getId()), request);
              	}
				AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "ORG is null in app domain auth", request);
			}
		}

		if (iamAccount != null && iamAccount.getOrg() != null && iamAccount.getUser() != null) {
			Span.current().setAttribute("enduser.orgid", String.valueOf(iamAccount.getOrg().getOrgId()));
			AppDomain appdomainObj = null;
			try {
				if (!SandboxAPI.isSandboxSubDomain(request.getServerName())) {
					appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sessionExpired = IAMUserUtil.isSessionExpired(iamAccount.getUser().getUid(), iamAccount.getOrg().getOrgId(), iamAccount.getUserSessionId(), appdomainObj);
			if(sessionExpired.equals("sessiontimeout")){
				return "sessiontimeout";
			}
			if (sessionExpired.equals("sessionexpired")) {
				LOGGER.error("[session expiry] " +iamAccount.getOrg().getOrgId()+"_"+iamAccount.getUser().getUid());
				if(request.getRequestURI().endsWith("application/fetchDetails")) {
					return "login";
				}
				return "sessionexpired";
			}
		}

		long timeTaken = System.currentTimeMillis() - time;
		AuthInterceptor.logTimeTaken(this.getClass().getSimpleName(), timeTaken, request);
		AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "DateSource interceptor done", request);
		return invocation.invoke();
	}
	
}
