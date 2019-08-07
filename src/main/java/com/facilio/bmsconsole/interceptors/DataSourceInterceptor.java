package com.facilio.bmsconsole.interceptors;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DataSourceInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 1L;
	private static HashMap customdomains = null; 
	 
	private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(DataSourceInterceptor.class);


	@Override
	public void init() {
		super.init();
		initHost();
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");
		
		//portal related changes
		if(request.getAttribute("isPortal") != null && (boolean)request.getAttribute("isPortal")) {
			String domainName = request.getServerName();
			if(customdomains != null) { 
                String orgdomain = (String)customdomains.get(domainName); 
                if(orgdomain != null) { 
                    domainName = orgdomain+"."+ portalDomain; 
                } 
                LOGGER.log(Level.ALL, "Found a valid domain for custom domain for "+ domainName); 
            } 
            if(domainName != null) { 
                String[] domainArray = domainName.split("\\."); 
                if (domainArray.length > 2) { 
                    String subDomain = domainArray[0]; 
                    String currentAccountSubDomain = ""; 
                    if (iamAccount != null && iamAccount.getOrg() != null) { 
                        currentAccountSubDomain = iamAccount.getOrg().getDomain(); 
                    } 
                    Organization org = null; 
                    if (subDomain.equalsIgnoreCase(currentAccountSubDomain)) { 
                        org = iamAccount.getOrg(); 
                    } else { 
                        org = IAMOrgUtil.getOrg(subDomain); 
                    } 
                    if (iamAccount == null && org != null) { 
                    	iamAccount = new IAMAccount(org, null);
                    	request.setAttribute("iamAccount", iamAccount);
                    }
               }
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
	
	private static String portalDomain = null; 
	 
    public static void setPortalDomain(String portalDomainName) { 
        portalDomain = portalDomainName; 
    } 
 
    public static String getPortalDomain(){ 
        return portalDomain; 
    } 
 
    private void initHost() { 
        try { 
            ServletContext context = ServletActionContext.getServletContext(); 
            if(customdomains == null && context != null) { 
                customdomains = (HashMap) context.getAttribute("customdomains"); 
            } 
        } catch (Exception e) { 
            LOGGER.debug(e.getMessage());
        } 
 
    }

}
