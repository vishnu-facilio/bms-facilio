
package com.facilio.bmsconsole.interceptors;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AuthInterceptor.class.getName());
    private static final HashSet<String> ADMIN_IDS = new HashSet<>();

	static {
		loadIds();
	}

	private static void loadIds() {
		String email = AwsUtil.getConfig("admin.console");
		if (email == null || "".equals(email.trim())) {
			return;
		}
		String[] list = email.split(",");
		for(String id : list) {
			ADMIN_IDS.add(id.trim());
		}
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {

		try {
			HttpServletRequest request = ServletActionContext.getRequest();

			if(! (("/api/integ/faciliosubmit".equals(request.getRequestURI())) || ("/api/integ/apilogin".equals(request.getRequestURI())) || ("/auth/api/login".equals(request.getRequestURI())))) {
			if (!isRemoteScreenMode(request)) {
				CognitoUser cognitoUser = AuthenticationUtil.getCognitoUser(request, false);
				Account currentAccount = null;
				if (!AuthenticationUtil.checkIfSameUser(currentAccount, cognitoUser)) {
					try {
						currentAccount = LoginUtil.getAccount(cognitoUser, false);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Invalid users", e);
						currentAccount = null;
					}
				}
				if (AuthenticationUtil.checkIfSameUser(currentAccount, cognitoUser)) {
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());

					Parameter permission = ActionContext.getContext().getParameters().get("permission");
					Parameter moduleName = ActionContext.getContext().getParameters().get("moduleName");
					if (permission != null && permission.getValue() != null && moduleName != null && moduleName.getValue() != null && !isAuthorizedAccess(moduleName.getValue(), permission.getValue())) {
						return "unauthorized";
					}

					String lang = currentAccount.getUser().getLanguage();
					Locale localeObj = null;
					if (lang == null || lang.trim().isEmpty()) {
						localeObj = request.getLocale();
					} else {
						localeObj = new Locale(lang);
					}

					String timezone = currentAccount.getUser().getTimezone();
					TimeZone timezoneObj = null;
					if (timezone == null || timezone.trim().isEmpty()) {
						Calendar calendar = Calendar.getInstance(localeObj);
						timezoneObj = calendar.getTimeZone();
					} else {
						timezoneObj = TimeZone.getTimeZone(timezone);
					}
					ActionContext.getContext().getSession().put("TIMEZONE", timezoneObj);
				} else {
					String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
					if (authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
						return Action.LOGIN;
					}
				}

				if (request.getRequestURL().indexOf("/app/admin") != -1) {
					if (currentAccount != null) {
						String useremail = currentAccount.getUser().getEmail();
						if (!ADMIN_IDS.contains(useremail)) {
							LOGGER.log(Level.SEVERE, "you are not allowed to access this page from");
							return Action.LOGIN;
						}
					}
				}
			}
			else {
				boolean authStatus = handleRemoteScreenAuth(request);
				if (!authStatus) {
					LOGGER.log(Level.SEVERE, "you are not allowed to access this page from remote screen.");
					return Action.ERROR;
				}
			}
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "error in auth interceptor", e);
			return Action.LOGIN;
		}

		
		try {
			/* let us call action or next interceptor */
			String result = arg0.invoke();

			/* let us do some post-processing */
			//output = "Post-Processing";
			//System.out.println(output);

			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "error thrown from action class", e);
			throw e;
		}
	}

	private boolean isAuthorizedAccess(String moduleName, String permissions) throws Exception {
		
		if (permissions == null || "".equals(permissions.trim())) {
			return true;
		}

		if(AccountUtil.getCurrentUser() == null) {
		    return false;
        }
		Role role = AccountUtil.getCurrentUser().getRole();
		if(role.getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN) || role.getName().equals("Administrator")) {
			return true;
		} else {
			return role.hasPermission(moduleName, permissions);
		}
	}
	
	private boolean isRemoteScreenMode(HttpServletRequest request) {
		if (request.getHeader("X-Remote-Screen") != null && "true".equalsIgnoreCase(request.getHeader("X-Remote-Screen").trim())) {
			return true;
		}
		return false;
	}
	
	private boolean handleRemoteScreenAuth(HttpServletRequest request) {
		try {
			String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
			if (authRequired != null && "false".equalsIgnoreCase(authRequired.trim())) {
				return true;
			}
			
			String deviceToken = LoginUtil.getUserCookie(request, "fc.deviceToken");
			if (deviceToken != null && !"".equals(deviceToken)) {
				long connectedScreenId = Long.parseLong(CognitoUtil.validateJWT(deviceToken, "auth0").getSubject().split("_")[0]);
				RemoteScreenContext remoteScreen = ScreenUtil.getRemoteScreen(connectedScreenId);
				if (remoteScreen != null) {
					Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(remoteScreen.getOrgId()), AccountUtil.getOrgBean().getSuperAdmin(remoteScreen.getOrgId()));
					currentAccount.setRemoteScreen(remoteScreen);
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());
					
					return true;
				}
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while check authendication from remote-screen.", e);
		}
		return false;
	}
}