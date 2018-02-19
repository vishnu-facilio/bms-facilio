package com.facilio.bmsconsole.interceptors;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.util.AuthenticationUtil;
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
			System.out.println("intercept() : arg0 :"+arg0);

			HttpServletRequest request = ServletActionContext.getRequest();

			Account currentAccount = (Account) ActionContext.getContext().getSession().get("CURRENT_ACCOUNT");
			if (currentAccount == null) {
				try {
					CognitoUser cognitoUser = AuthenticationUtil.getCognitoUser(request);
					if(cognitoUser != null) {
						currentAccount = LoginUtil.getAccount(cognitoUser, true);
						ActionContext.getContext().getSession().put("CURRENT_ACCOUNT", currentAccount);
					}
				} catch (Exception e){
					e.printStackTrace();
					currentAccount = null;
				}
			}
			if(currentAccount != null) {
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(currentAccount);

				request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
				request.setAttribute("USERID", currentAccount.getUser().getOuid());

				Parameter permission = ActionContext.getContext().getParameters().get("permission");
				Parameter moduleName = ActionContext.getContext().getParameters().get("moduleName");
				if (permission != null && permission.getValue() != null && moduleName != null && moduleName.getValue() != null && !isAuthorizedAccess(moduleName.getValue() ,permission.getValue())) {
					return "unauthorized";
				}

				String lang = currentAccount.getUser().getLanguage();
				Locale localeObj = null;
				if (lang == null || lang.trim().isEmpty()) {
					localeObj = request.getLocale();
				} else {
					localeObj = new Locale(lang);
				}
				System.out.println("### LOCALE: " + localeObj);

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

	private boolean isAuthorizedAccess(String moduleName, String permissions) throws Exception {

		if (permissions == null || "".equals(permissions.trim())) {
			System.out.println("WARNING: Configured permission is empty");
			return true;
		}

		Role role = AccountUtil.getCurrentUser().getRole();
		if(role.getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN) || role.getName().equals("Administrator")) {
			return true;
		} else {
			return role.hasPermission(moduleName, permissions);
		}

	}
}