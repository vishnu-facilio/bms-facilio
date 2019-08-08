package com.facilio.serviceportal.actions;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.AccountException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


public class PortalAuthInterceptor extends AbstractInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
			if(customdomains == null && context != null) {
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
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error in portal auth interceptor", e);
			return Action.LOGIN;
		}
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			AccountUtil.setReqUri(request.getRequestURI());
			AccountUtil.setRequestParams(request.getParameterMap());
			return arg0.invoke();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error thrown from action class", e);
			throw e;
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
				String domainName = request.getServerName();
				String orgdomain = null;
				logger.info("Getting portal auth info : " +domainName);
				if(customdomains != null) {
					logger.info("Matching...  "+ domainName );
					orgdomain = (String)customdomains.get(domainName);
					if(orgdomain != null) {
						domainName = orgdomain+"."+ portalDomain;
					}
					logger.info("Found a valid domain for custom domain for "+ domainName);
				}
				if(domainName != null) {
					String[] domainArray = domainName.split("\\.");
					if (domainArray.length > 2) {
						String subDomain = domainArray[0];

						Organization org = null;
						//org = AccountUtil.getOrgBean().getOrg(AccountUtil.getCurrentOrg().getDomain());
						org = AccountUtil.getOrgBean().getOrg(subDomain);
						AccountUtil.setCurrentAccount(org.getOrgId());
						PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(org.getOrgId(), false);
						org.setPortalId(portalInfo.getPortalId());
						if (org != null) {
							long portalId = org.getPortalId();
							logger.fine("Portal Domain ......"+portalId);
							User user = null;
							if (cognitoUser != null) {
								user = AccountUtil.getUserBean().getFacilioUser(cognitoUser.getEmail(), subDomain);
								if (user == null) {
									throw new AccountException("No such user present");
								}
							}
							currentAccount = new Account(org, user);
							AccountUtil.cleanCurrentAccount();
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
