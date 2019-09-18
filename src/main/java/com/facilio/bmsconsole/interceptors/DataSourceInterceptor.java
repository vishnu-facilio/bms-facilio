package com.facilio.bmsconsole.interceptors;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUtil;
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
		String portalDomain = "app";
		if(request.getAttribute("portalDomain") != null) {
			portalDomain = (String)request.getAttribute("portalDomain");
		}
		
		//portal related changes
		if(request.getAttribute("isPortal") != null && (boolean)request.getAttribute("isPortal")) {
			if(portalDomain != null) { 
                    String currentAccountSubDomain = ""; 
                    if (iamAccount != null && iamAccount.getOrg() != null) { 
                        currentAccountSubDomain = iamAccount.getOrg().getDomain(); 
                    } 
                    Organization org = null; 
                    if (portalDomain.equalsIgnoreCase(currentAccountSubDomain)) { 
                        org = iamAccount.getOrg(); 
                    } else { 
                        org = IAMOrgUtil.getOrg(portalDomain); 
                    } 
                    if (iamAccount == null && org != null) { 
                    	iamAccount = new IAMAccount(org, null);
                    	request.setAttribute("iamAccount", iamAccount);
                    }
            }
		}
		//remote screen handling
		else if(request.getAttribute("remoteScreen") != null && request.getAttribute("remoteScreen") instanceof RemoteScreenContext) {
			RemoteScreenContext remoteScreen = (RemoteScreenContext) request.getAttribute("remoteScreen");
			Organization org = IAMOrgUtil.getOrg(remoteScreen.getOrgId());
			if(org != null) {
				IAMAccount account = new IAMAccount(org, null);
				request.setAttribute("iamAccount", account);
			}
			
		}
		else {
			if (iamAccount != null) {
				String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
				if (currentOrgDomain == null) {
					currentOrgDomain = request.getHeader("X-Current-Org"); 
				}
				
				Organization organization = null;
				if (StringUtils.isNotBlank(currentOrgDomain)) {
					organization = IAMUtil.getOrgBean().getOrgv2(currentOrgDomain);
				}
				else {
					organization = IAMUtil.getUserBean().getDefaultOrgv2(iamAccount.getUser().getUid());
				}
				iamAccount.setOrg(organization);
			}
		}
		return invocation.invoke();
	}
	
	
}
