package com.facilio.bmsconsole.interceptors;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.facilio.aws.util.FacilioProperties;
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
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");
		//remote screen handling
		if(request.getAttribute("remoteScreen") != null && request.getAttribute("remoteScreen") instanceof RemoteScreenContext) {
			RemoteScreenContext remoteScreen = (RemoteScreenContext) request.getAttribute("remoteScreen");
			Organization org = IAMOrgUtil.getOrg(remoteScreen.getOrgId());
			if(org != null) {
				IAMAccount account = new IAMAccount(org, null);
				request.setAttribute("iamAccount", account);
			}
			
		}
		//connected device(new tv and kiosk) handling
		else if(request.getAttribute("device") != null && request.getAttribute("device") instanceof ConnectedDeviceContext) {
			ConnectedDeviceContext connectedDevice = (ConnectedDeviceContext) request.getAttribute("device");
			Organization org = IAMOrgUtil.getOrg(connectedDevice.getOrgId());
			if(org != null) {
				IAMAccount account = new IAMAccount(org, null);
				request.setAttribute("iamAccount", account);
			}
			
		}
		else {
			Organization organization = null;
			
			AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
			if (iamAccount != null) {
				String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
				if (currentOrgDomain == null) {
					currentOrgDomain = request.getHeader("X-Current-Org"); 
				}
				//the third check can be removed..It is added now for sutherland demo (894 is the custom domain id for org 343 in production)
				if(appDomain != null && appDomain.getOrgId() > 0 && (!FacilioProperties.isProduction() || appDomain.getId() != 894l)) {
					organization = IAMOrgUtil.getOrg(appDomain.getOrgId());
				}
				else if (StringUtils.isNotBlank(currentOrgDomain)) {
					organization = IAMUserUtil.getOrg(currentOrgDomain, iamAccount.getUser().getUid());
				}
				else {
					organization = IAMUserUtil.getDefaultOrg(iamAccount.getUser().getUid());
				}
				iamAccount.setOrg(organization);
			}
			else {
				organization = IAMOrgUtil.getOrg(appDomain.getOrgId());
              	if(organization != null) {
                	iamAccount = new IAMAccount(organization, null);
                	request.setAttribute("iamAccount", iamAccount);
              	}
			}
		}
		return invocation.invoke();
	}
	
	
}
