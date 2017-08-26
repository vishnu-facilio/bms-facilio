package com.facilio.serviceportal.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.fw.UserInfo;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PortalAuthInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		System.out.println("This is the interceptopr for service portal" );
		String serviceportaldomain = null;
		HttpServletRequest request = ServletActionContext.getRequest();

		try {
			String idToken = LoginUtil.getUserCookie(request, LoginUtil.IDTOKEN_COOKIE_NAME);

			CognitoUser cognitoUser = CognitoUtil.verifyIDToken(idToken);
			UserInfo userInfo = (UserInfo) ActionContext.getContext().getSession().get("PORTAL_USER_INFO");
			if (userInfo == null) {
				userInfo = LoginUtil.getUserInfo(serviceportaldomain, cognitoUser);
				ActionContext.getContext().getSession().put("USER_INFO", userInfo);
			}
			String result = arg0.invoke();
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}

}
