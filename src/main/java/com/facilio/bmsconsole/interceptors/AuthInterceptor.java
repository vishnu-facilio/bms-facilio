package com.facilio.bmsconsole.interceptors;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.constants.FacilioConstants;
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
	private static String MOBILE_HOSTNAME = null;

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
			if(MOBILE_HOSTNAME==null)
			{
				MOBILE_HOSTNAME = (String)ActionContext.getContext().getApplication().get("M_DOMAINNAME");
			}
			String requestSubdomain = null;//serverName.replaceAll(HOSTNAME, "");
			
			if(serverName.endsWith(HOSTNAME))
			{
				requestSubdomain = serverName.replaceAll(HOSTNAME, "");
				 request.setAttribute("isMobile", false);
				 System.out.println("desktop");
			}
			else
			{
				requestSubdomain = serverName.replaceAll(MOBILE_HOSTNAME, "");
				request.setAttribute("isMobile", true);
				 System.out.println("mobile");

			}
			
			if (!requestSubdomain.equalsIgnoreCase(userInfo.getSubdomain())) {
				return "unauthorized";
			}

			// Step 4: Setting threadlocal variables
			UserInfo.setCurrentUser(userInfo);
			OrgInfo.setCurrentOrgInfo(OrgApi.getOrgInfo(userInfo.getOrgId()));

			// Step 5: Checking permission for current resource
			Parameter permission = ActionContext.getContext().getParameters().get("permission");
			if (permission != null && permission.getValue() != null && !isAuthorizedAccess(permission.getValue())) {
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

	private boolean isAuthorizedAccess(String permissions) throws Exception
	{
		Map<String, Long> perm = FacilioConstants.Role.DEFAULT_ROLES;
		
		if (permissions == null || "".equals(permissions.trim())) {
			System.out.println("WARNING: Configured permission is empty");
			return true;
		}
		
		RoleContext role = UserInfo.getCurrentUser().getRole();
		return role.hasPermission(permissions);
	}
}