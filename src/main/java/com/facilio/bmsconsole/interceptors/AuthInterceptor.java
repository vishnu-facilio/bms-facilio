package com.facilio.bmsconsole.interceptors;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.fw.util.RequestUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
	
		try {
			System.out.println("AuthInterceptor");
			// Step 1: Validating ID Token
			HttpServletRequest request = ServletActionContext.getRequest();
			
//			String idToken = LoginUtil.getUserCookie(request, LoginUtil.IDTOKEN_COOKIE_NAME);
			String facilioToken = LoginUtil.getUserCookie(request, "fc.idToken.facilio");
			String headerToken = request.getHeader("Authorization");
			
			// skiping signup url from authorization check
			if (facilioToken != null || headerToken != null) {
				
				boolean isAPI = true;
				if (headerToken != null) {
					if(headerToken.startsWith("Bearer facilio "))
					{
						facilioToken = headerToken.replace("Bearer facilio ", "");
					}
					else
					{
					headerToken = request.getHeader("Authorization").replace("Bearer ", "");
					}
				}

				CognitoUser cognitoUser = (facilioToken != null) ? CognitoUtil.verifiyFacilioToken(facilioToken) : CognitoUtil.verifyIDToken(headerToken);
				if (cognitoUser == null) {
					return Action.LOGIN;
				}

				// Step 2: Setting current user in session
				Account currentAccount = (Account) ActionContext.getContext().getSession().get("CURRENT_ACCOUNT");
				if (currentAccount == null) {
					if (isAPI) {
						boolean addUserEntryIfNotExists = (request.getRequestURI().indexOf("/api/login") != -1 || request.getRequestURI().indexOf("/api/account") != -1);
						currentAccount = LoginUtil.getAccount(cognitoUser, addUserEntryIfNotExists);
						ActionContext.getContext().getSession().put("CURRENT_USER", currentAccount);
					}
					else {
						currentAccount = LoginUtil.getAccount(cognitoUser, true);
						ActionContext.getContext().getSession().put("CURRENT_USER", currentAccount);
					}
				}

				// Step 3: Validating subdomain
				String serverName = request.getServerName();
				if (RequestUtil.HOSTNAME == null) {
					RequestUtil.HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
				}
				if(RequestUtil.MOBILE_HOSTNAME==null)
				{
					RequestUtil.MOBILE_HOSTNAME = (String)ActionContext.getContext().getApplication().get("M_DOMAINNAME");
				}
				String requestSubdomain = null;//serverName.replaceAll(HOSTNAME, "");
				
				if(serverName.endsWith(RequestUtil.HOSTNAME))
				{
					requestSubdomain = serverName.replaceAll(RequestUtil.HOSTNAME, "");
					 request.setAttribute("isMobile", false);
					 //System.out.println("desktop");
				}
				else
				{
					requestSubdomain = serverName.replaceAll(RequestUtil.MOBILE_HOSTNAME, "");
					request.setAttribute("isMobile", true);
					 //System.out.println("mobile");

				}
				
				if (!isAPI && !requestSubdomain.equalsIgnoreCase(currentAccount.getOrg().getDomain())) {
					return "unauthorized";
				}

				// Step 4: Setting threadlocal variables
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(currentAccount);
				// Setting ORGID & USERID in attributes for using in access log
				request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
				request.setAttribute("USERID", currentAccount.getUser().getOuid());

				// Step 5: Checking permission for current resource
				Parameter permission = ActionContext.getContext().getParameters().get("permission");
				if (permission != null && permission.getValue() != null && !isAuthorizedAccess(permission.getValue())) {
					return "unauthorized";
				}
				
				// Step 6: Setting locale & timezone information in session
				String lang = currentAccount.getUser().getLanguage();
				Locale localeObj = null;
				if (lang == null || lang.trim().isEmpty()) {
					localeObj = request.getLocale();
				}
				else {
					localeObj = new Locale(lang);
				}
				System.out.println("### LOCALE: "+localeObj);
				
				String timezone = currentAccount.getUser().getTimezone();
				TimeZone timezoneObj = null;
				if (timezone == null || timezone.trim().isEmpty()) {
					Calendar calendar = Calendar.getInstance(localeObj);
					timezoneObj = calendar.getTimeZone();
				}
				else {
					timezoneObj = TimeZone.getTimeZone(timezone);
				}
				ActionContext.getContext().getSession().put("TIMEZONE", timezoneObj);
			}
			else {
				String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
				if (authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
					return Action.LOGIN;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return Action.LOGIN;
		}

		/* let us call action or next interceptor */
		String result = arg0.invoke();

		/* let us do some post-processing */
		//output = "Post-Processing"; 
		//System.out.println(output);

		return result;
	}

	private boolean isAuthorizedAccess(String permissions) throws Exception {
		
		if (permissions == null || "".equals(permissions.trim())) {
			System.out.println("WARNING: Configured permission is empty");
			return true;
		}
		
		Role role = AccountUtil.getCurrentUser().getRole();
		return role.hasPermission(role.getPermissions());
	}
}