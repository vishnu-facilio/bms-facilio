package com.facilio.serviceportal.actions;

import java.util.HashMap;
import java.util.logging.Logger;

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
	private static Logger logger = Logger.getLogger(PortalAuthInterceptor.class.getName());

	private HashMap customdomains = null;
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
			
			//System.out.println("PortalAuthInterceptor loaded"+RequestUtil.HOSTNAME);
			if(customdomains==null)
			{
			customdomains = (HashMap) context.getAttribute("customdomains");
			}
			logger.info("Custom domains loaded "+customdomains);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		//System.out.println("This is the interceptopr for service portal" );
		initHost();
		
		
		//String subdomian =  RequestUtil.getDomainName();
		
		try {

			String actionname = ActionContext.getContext().getName();
		//	System.out.println("inside if"+actionname);

			boolean bypassauth = actionname.equals("login") || actionname.equals("samllogin");
			intercept0();
			boolean validsession =false;
			if(bypassauth || validsession) {
			//	System.out.println("inside if");
				String result = arg0.invoke();
				return result;
			} else {
				
				// redirect to login page..
			//	System.out.println("inside else");
				String result = arg0.invoke();
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "login";
		}

	
	}

	public static String PORTALDOMAIN = null;
	private String intercept0(){
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

				System.out.println("Authenticating for "+ domainName);
				if(domainName!=null) {
				if (domainName.contains("http://")) {
					domainName = domainName.replace("http://", "");
				} else if (domainName.contains("https://")) {
					domainName = domainName.replace("https://", "");
				}
				}
				if(customdomains!=null)
				{
					logger.info("Matching...  "+ domainName );

				  String orgdomain = (String)customdomains.get(domainName);
				  if(orgdomain!=null)
				  {
					  domainName = orgdomain+"."+PORTALDOMAIN;
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
						System.out.println("___+_++_+______ Org"+org);
						if (org != null) {
							Long portalId = org.getPortalId();
							System.out.println("Portal Domain ......"+portalId);
							User user = null;
							if (cognitoUser != null) {
								user = AccountUtil.getUserBean().getPortalUser(cognitoUser.getEmail(), portalId);
							}
							currentAccount = new Account(org, user);
							AccountUtil.setCurrentAccount(currentAccount);
						}
					}
					else
					{
						System.out.println("Match failed ......");
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	    return "success";
    }

}
