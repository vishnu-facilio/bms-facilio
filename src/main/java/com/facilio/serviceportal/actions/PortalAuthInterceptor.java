package com.facilio.serviceportal.actions;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.fw.util.RequestUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PortalAuthInterceptor extends AbstractInterceptor {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		ServletContext context = ServletActionContext.getServletContext();
		
		String host = (String) context.getInitParameter("DOMAINNAME");
		if(RequestUtil.HOSTNAME==null)
		{
		 RequestUtil.HOSTNAME = host;
		}
		if(RequestUtil.MOBILE_HOSTNAME==null)
		{
			RequestUtil.MOBILE_HOSTNAME= (String) context.getInitParameter("M_DOMAINNAME");
		}
		
		System.out.println("PortalAuthInterceptor loaded"+RequestUtil.HOSTNAME);

	}
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		System.out.println("This is the interceptopr for service portal" );
		//String serviceportaldomain = null;
		//HttpServletRequest request = ServletActionContext.getRequest();
		
		String subdomian =  RequestUtil.getDomainName();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", subdomian);

		ServicePortalInfo  sinfo = modBean.getServicePortalInfo();
		if(sinfo==null)
		{
			// invalid portal
		}
		
		String actionname = ActionContext.getContext().getName();
		System.out.println("inside if"+actionname);

		boolean bypassauth = actionname.equals("login") || actionname.equals("samllogin");
		
		boolean validsession =false;
		if(bypassauth || validsession)
		{
			System.out.println("inside if");
			String result = arg0.invoke();
			return result;
		}
		else
		{
			
			// redirect to login page..
			System.out.println("inside else");

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

}
