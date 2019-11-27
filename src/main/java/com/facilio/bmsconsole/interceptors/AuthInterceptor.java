
package com.facilio.bmsconsole.interceptors;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.audit.AuditData;
import com.facilio.audit.DBAudit;
import com.facilio.audit.FacilioAudit;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.server.ServerInfo;
import com.opensymphony.xwork2.ActionProxy;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.iam.accounts.impl.IAMUserBeanImpl;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.service.FacilioService;
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
				} else {
                    request.setAttribute("iamAccount", iamAccount);
				}
			}
			else if (!isRemoteScreenMode(request)) {
				String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
				if(authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
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
				boolean authStatus = handleRemoteScreenAuth(request);//both tv mode and new device mode handled
				if (!authStatus) {
					LOGGER.log(Level.FATAL, "you are not allowed to access this page from remote screen.");
					return Action.ERROR;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "error in auth interceptor", e);
			return Action.LOGIN;
		}

		request.getAttribute("iamAccount");
		return arg0.invoke();
	}

	private boolean isRemoteScreenMode(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Remote-Screen");
		String deviceHeader = request.getHeader("X-Device");
		String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
		String deviceTokenNew = FacilioCookie.getUserCookie(request, "fc.deviceTokenNew");
		String facilioToken1 = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
		String facilioToken2 = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
		
		if ( remoteScreenHeader != null && "true".equalsIgnoreCase(remoteScreenHeader.trim())) {
			return true;
		}
		if ( deviceHeader != null && "true".equalsIgnoreCase(deviceHeader.trim())) {
			return true;
		}
		else if ((deviceToken != null && !"".equals(deviceToken))||(deviceTokenNew != null && !"".equals(deviceTokenNew))  && facilioToken1 == null && facilioToken2 == null) {
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
				
				long connectedScreenId = Long.parseLong(IAMUserBeanImpl.validateJWT(deviceToken, "auth0").getSubject().split(IAMUserBeanImpl.JWT_DELIMITER)[0]);
				RemoteScreenContext remoteScreen = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getRemoteScreen(connectedScreenId));
				if (remoteScreen != null) {
					request.setAttribute("remoteScreen", remoteScreen);
					return true;
				}
			}
			
			String deviceTokenNew = FacilioCookie.getUserCookie(request, "fc.deviceTokenNew");
			
			String headerToken = request.getHeader("Authorization");

	        if (deviceTokenNew != null || headerToken != null) {
	        	if (headerToken != null && headerToken.trim().length() > 0) {
	                if (headerToken.startsWith("Bearer device ")) {
	                	deviceTokenNew = headerToken.replace("Bearer device ", "");
	                } else {
	                	deviceTokenNew = request.getHeader("Authorization").replace("Bearer ", "");
	                }
	            }
	        }
			if (deviceTokenNew != null && !"".equals(deviceTokenNew)) {
				
				long connectedDeviceId = Long.parseLong(IAMUserBeanImpl.validateJWT(deviceTokenNew, "auth0").getSubject().split(IAMUserBeanImpl.JWT_DELIMITER)[0]);
				ConnectedDeviceContext deviceContext = FacilioService.runAsServiceWihReturn(() ->  DevicesUtil.getConnectedDevice(connectedDeviceId));
				if (deviceContext != null) {
					request.setAttribute("device", deviceContext);
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "Exception while check authentication from remote-screen.", e);
		}
		return false;
	}
	
}