package com.facilio.serviceportal.actions;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


public class PortalAuthInterceptor extends AbstractInterceptor {
	private static Logger logger = Logger.getLogger(PortalAuthInterceptor.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(PortalAuthInterceptor.class.getName());

	private static HashMap customdomains = null;

	@Override
	public void init() {
		super.init();
		initHost();
	}
	
	private void initHost() {
		try {
			ServletContext context = ServletActionContext.getServletContext();
			if(customdomains == null) {
				customdomains = (HashMap) context.getAttribute("customdomains");
			}
			logger.info("Custom domains loaded "+customdomains);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}

	}
	
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		try {
			intercept0();
			return arg0.invoke();
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			return "login";
		}
	}

	private static String portalDomain = null;

	public static void setPortalDomain(String portalDomainName) {
		portalDomain = portalDomainName;
	}

	public static String getPortalDomain(){
		return portalDomain;
	}

	private void intercept0() {
        HttpServletRequest request = ServletActionContext.getRequest();
		CognitoUtil.CognitoUser cognitoUser = null;
		Account currentAccount = null;
		try {
			AccountUtil.cleanCurrentAccount();
			cognitoUser = AuthenticationUtil.getCognitoUser(request,true);
			if (AuthenticationUtil.checkIfSameUser(currentAccount, cognitoUser)) {
				AccountUtil.setCurrentAccount(currentAccount);
			} else {
				String domainName = request.getHeader("Origin");
				logger.info("Getting portal auth info" +domainName);
				if(domainName != null) {
					if (domainName.contains("http://")) {
						domainName = domainName.replace("http://", "");
					} else if (domainName.contains("https://")) {
						domainName = domainName.replace("https://", "");
					}
				}
				if(customdomains != null) {
					logger.info("Matching...  "+ domainName );
					String orgdomain = (String)customdomains.get(domainName);
					if(orgdomain != null) {
						domainName = orgdomain+"."+ portalDomain;
					}
					logger.info("Found a valid domain for custom domain for "+ domainName);
				}
				if(domainName != null) {
					String[] domainArray = domainName.split("\\.");
					if (domainArray.length > 2) {
						String subDomain = domainArray[0];
						String currentAccountSubDomain = "";
						if (currentAccount != null && currentAccount.getOrg() != null) {
							currentAccountSubDomain = currentAccount.getOrg().getDomain();
						}
						Organization org = null;
						if (domainName.equalsIgnoreCase(currentAccountSubDomain)) {
							org = currentAccount.getOrg();
						} else {
							org = AccountUtil.getOrgBean().getPortalOrg(subDomain);
						}
						if (org != null) {
							long portalId = org.getPortalId();
							logger.fine("Portal Domain ......"+portalId);
							User user = null;
							if (cognitoUser != null) {
								user = AccountUtil.getUserBean().getPortalUser(cognitoUser.getEmail(), portalId);
							}
							currentAccount = new Account(org, user);
							AccountUtil.setCurrentAccount(currentAccount);
						}
					} else {
						System.out.println("Match failed ......");
					}
				}
			}
		} catch (Exception e){
			log.info("Exception occurred ", e);
		}
    }

}
