
package com.facilio.bmsconsole.interceptors;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(AuthInterceptor.class.getName());

	@Override
	public void init() {
		super.init();
	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			if (getPermalinkToken(request) != null) {
				String token = getPermalinkToken(request);
				
				List<String> urlsToValidate = new ArrayList<>();
				
				String reqURL = request.getRequestURI();
				urlsToValidate.add(reqURL);
				
				String referrer = request.getHeader(HttpHeaders.REFERER);
				if (referrer != null && !"".equals(referrer.trim())) {
					URL url = new URL(referrer);
					urlsToValidate.add(url.getPath());
				}
				IAMAccount iamAccount = IAMUserUtil.getPermalinkAccount(token, urlsToValidate);
				if(iamAccount == null) {
					return Action.ERROR;
				}
				if (iamAccount != null) {
						request.setAttribute("iamAccount", iamAccount);
				}
			}
			else if (!isRemoteScreenMode(request)) {
				String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
				if(StringUtils.isEmpty(authRequired) || "true".equalsIgnoreCase(authRequired)) {
					IAMAccount iamAccount = AuthenticationUtil.validateToken(request, false, "app");
					if (iamAccount != null) {
						request.setAttribute("iamAccount", iamAccount);
					}
					else {
						return Action.LOGIN;
					}
				}
			}
			else {
				boolean authStatus = handleRemoteScreenAuth(request);
				if (!authStatus) {
					LOGGER.log(Level.FATAL, "you are not allowed to access this page from remote screen.");
					return Action.ERROR;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "error in auth interceptor", e);
			return Action.LOGIN;
		}

		return arg0.invoke();
	}
	
	private boolean isRemoteScreenMode(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Remote-Screen");
		String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
		
		String facilioToken1 = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
		String facilioToken2 = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
		
		if ( remoteScreenHeader != null && "true".equalsIgnoreCase(remoteScreenHeader.trim())) {
			return true;
		}
		else if (deviceToken != null && !"".equals(deviceToken) && facilioToken1 == null && facilioToken2 == null) {
			return true;
		}
		return false;
	}
	
	private String getPermalinkToken(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Permalink-Token");
		if (remoteScreenHeader != null && !"".equals(remoteScreenHeader.trim())) {
			return remoteScreenHeader;
		}
		return null;
	}
	
	private boolean handleRemoteScreenAuth(HttpServletRequest request) {
		try {
			String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
			if (authRequired != null && "false".equalsIgnoreCase(authRequired.trim())) {
				return true;
			}
			
			String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
			if (deviceToken != null && !"".equals(deviceToken)) {
				long connectedScreenId = Long.parseLong(IAMUserUtil.validateJWT(deviceToken, "auth0").getSubject().split(IAMUserUtil.JWT_DELIMITER)[0]);
				RemoteScreenContext remoteScreen = ScreenUtil.getRemoteScreen(connectedScreenId);
				if (remoteScreen != null) {
					// TODO - check here
					Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(remoteScreen.getOrgId()), null /*AccountUtil.getOrgBean().getSuperAdmin(remoteScreen.getOrgId())*/);
					currentAccount.setRemoteScreen(remoteScreen);
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());
					
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "Exception while check authentication from remote-screen.", e);
		}
		return false;
	}
	
}