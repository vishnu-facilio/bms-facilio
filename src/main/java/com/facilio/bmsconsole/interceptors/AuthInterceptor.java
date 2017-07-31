package com.facilio.bmsconsole.interceptors;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.facilio.fw.auth.LoginUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	private static String HOSTNAME = null;
	@Override
	public void init() {

		super.init();
	}
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {

		String output = "Pre-Processing"; 
		System.out.println(output);

		try {
			// Step 1: Validating ID Token
			HttpServletRequest request = ServletActionContext.getRequest();

			String idToken = LoginUtil.getUserCookie(request, LoginUtil.IDTOKEN_COOKIE_NAME);

			CognitoUser cognitoUser = CognitoUtil.verifyIDToken(idToken);
			if (cognitoUser == null) {
				return Action.LOGIN;
			}

			// Step 2: Setting current user in session
			UserInfo userInfo = (UserInfo) ActionContext.getContext().getSession().get("USER_INFO");
			if (userInfo == null) {
				userInfo = LoginUtil.getUserInfo(cognitoUser);
				ActionContext.getContext().getSession().put("USER_INFO", userInfo);
			}

			// Step 3: Validating subdomain
			String serverName = request.getServerName();
			if (HOSTNAME == null) {
				HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
			}
			String requestSubdomain = serverName.replaceAll(HOSTNAME, "");
			if (!requestSubdomain.equalsIgnoreCase(userInfo.getSubdomain())) {
				return "unauthorized";
			}

			// Step 4: Setting threadlocal variables
			UserInfo.setCurrentUser(userInfo);
			OrgInfo.setCurrentOrgInfo(OrgApi.getOrgInfo(userInfo.getOrgId()));

			// Step 5: Checking permission for current resource
			String permission = ActionContext.getContext().getParameters().get("permission").getValue();
			if (!isAuthorizedAccess(permission)) {
				return "unauthorized";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return Action.LOGIN;
		}

		/* let us call action or next interceptor */
		String result = arg0.invoke();

		/* let us do some post-processing */
		output = "Post-Processing"; 
		System.out.println(output);

		return result;
	}

	private boolean isAuthorizedAccess(String permission) throws SQLException
	{
		System.out.println("Current Role:::" +UserInfo.getCurrentUser().getRole());
		// Temp code
		//		if(FacilioConstants.Role.ADMINISTRATOR.equalsIgnoreCase(UserInfo.getCurrentUser().getRole()))
		//		{
		//			return true;
		//		}
		//		return UserAPI.getRole(UserInfo.getCurrentUser().getRole()).hasPermission(FacilioConstants.Role.permissionsMap.get(permission));
		return true;
	}
}