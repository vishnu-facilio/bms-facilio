package com.facilio.serviceportal.actions;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.util.RequestUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PortalAuthInterceptor extends AbstractInterceptor {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
	
	}
	
	public void initHost()
	{
		try {
			ServletContext context = ServletActionContext.getServletContext();
			
			if (RequestUtil.HOSTNAME==null) {
			    String host = (String) context.getInitParameter("DOMAINNAME");
			    RequestUtil.HOSTNAME = host;
			}
			if (RequestUtil.MOBILE_HOSTNAME==null) {
				RequestUtil.MOBILE_HOSTNAME= (String) context.getInitParameter("M_DOMAINNAME");
			}
			
			System.out.println("PortalAuthInterceptor loaded"+RequestUtil.HOSTNAME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		System.out.println("This is the interceptopr for service portal" );
		initHost();
		
		
		String subdomian =  RequestUtil.getDomainName();
		
		try {

			String actionname = ActionContext.getContext().getName();
			System.out.println("inside if"+actionname);

			boolean bypassauth = actionname.equals("login") || actionname.equals("samllogin");
			intercept0();
			boolean validsession =false;
			if(bypassauth || validsession) {
				System.out.println("inside if");
				String result = arg0.invoke();
				return result;
			} else {
				
				// redirect to login page..
				System.out.println("inside else");
				String result = arg0.invoke();
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "login";
		}

	/*	try {
			String idToken = LoginUtil.getUserCookie(request, LoginUtil.IDTOKEN_COOKIE_NAME);

			CognitoUser cognitoUser = CognitoUtil.verifyIDToken(idToken);
			UserInfo userInfo = (UserInfo) ActionContext.getContext().getSession().get("PORTAL_USER_INFO");
			if (userInfo == null) {
				userInfo = LoginUtil.getUserInfo(serviceportaldomain, cognitoUser);
				ActionContext.getContext().getSession().put("PORTAL_USER_INFO", userInfo);
			}
			System.out.println("passing to action");

			String result = arg0.invoke();
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
		*/
	}

	private String intercept0(){
        HttpServletRequest request = ServletActionContext.getRequest();
		CognitoUtil.CognitoUser cognitoUser = null;
		Account currentAccount = null;
		try {
			cognitoUser = AuthenticationUtil.getCognitoUser(request);
			currentAccount = (Account) ActionContext.getContext().getSession().get("CURRENT_PORTAL_ACCOUNT");
			if (AuthenticationUtil.checkIfSameUser(currentAccount, cognitoUser)) {
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(currentAccount);
			} else {
				String domainName = request.getHeader("Origin");
				if(domainName != null) {
					if (domainName.contains("http://")) {
						domainName = domainName.replace("http://", "");
					} else if (domainName.contains("https://")) {
						domainName = domainName.replace("https://", "");
					}
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
							Long portalId = org.getPortalId();
							User user = null;
							if (cognitoUser != null) {
								user = AccountUtil.getUserBean().getPortalUser(cognitoUser.getEmail(), portalId);
							}
							currentAccount = new Account(org, user);
							AccountUtil.cleanCurrentAccount();
							AccountUtil.setCurrentAccount(currentAccount);
							ActionContext.getContext().getSession().put("CURRENT_PORTAL_ACCOUNT", currentAccount);
						}
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	    return "success";
    }

}
