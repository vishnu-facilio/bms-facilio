
package com.facilio.bmsconsole.interceptors;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.filters.AccessLogFilter;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.exceptions.SecurityPolicyException;
import com.facilio.util.RequestUtil;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.iam.accounts.impl.IAMUserBeanImpl;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
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
	@WithSpan
	public String intercept(ActionInvocation arg0) throws Exception {
		long time = System.currentTimeMillis();
		HttpServletRequest request = ServletActionContext.getRequest();
		String authorization = request.getHeader("Authorization");
		boolean isOauth2 = StringUtils.isNotEmpty(authorization) && StringUtils.startsWith(authorization, "Bearer oauth2");
		String apiKey = request.getHeader("x-api-key");
		boolean isApiKey = StringUtils.isNotEmpty(apiKey);
		if(!isApiKey && authorization!=null && StringUtils.startsWith(authorization, "Basic")) {		// odata request
			String encodedPass = authorization.replace("Basic ", "");
			String decodedKey = new String(Base64.getDecoder().decode(encodedPass), StandardCharsets.UTF_8);
			if (decodedKey.substring(0,decodedKey.length()-1).contains(":")) {
				apiKey = decodedKey.substring(decodedKey.indexOf(":") + 1);
			} else {
				apiKey = decodedKey.substring(0,decodedKey.length()-1);
			}
		}
		boolean isOdataReq = StringUtils.isNotEmpty(apiKey);
		if (isOauth2) {
			request.setAttribute("authMethod", "OAUTH2");
			return arg0.invoke();
		} else if (isApiKey) {
			request.setAttribute("authMethod", "APIKEY");
			return arg0.invoke();
		} else if (isOdataReq) {
			request.setAttribute("authMethod","APIKEY");
			return arg0.invoke();
		}
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
				AccountUtil.setAuthMethod(AccountUtil.AuthMethod.PERMALINK);
				if(iamAccount == null) {
					checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "Returning error because of no IAM account in permalink", request);
					return Action.ERROR;
				} else {
                    request.setAttribute("iamAccount", iamAccount);
					Span.current().setAttribute("enduser.id", String.valueOf(iamAccount.getUser().getId()));
				}
			}
			else if (!isRemoteScreenMode(request)) {
				AccountUtil.setAuthMethod(AccountUtil.AuthMethod.USER_PWD);
				String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
				if(authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
					IAMAccount iamAccount = null;
					try {
						 iamAccount = AuthenticationUtil.validateToken(request, false);
					} catch (SecurityPolicyException secEx) {
						SecurityPolicyException.ErrorCode errorCode = secEx.getErrorCode();
						if (errorCode == SecurityPolicyException.ErrorCode.WEB_SESSION_EXPIRY) {
							if(iamAccount != null){
								LOGGER.error("[session expiry] Web_session_expiry" +iamAccount.getOrg().getOrgId()+"_"+iamAccount.getUser().getUid());
							}
							if(request.getRequestURI().endsWith("application/fetchDetails")) {
								return "login";
							}
							return "sessionexpired";
						}
					} catch (Exception ex) {
						Throwable cause = ex.getCause();
						if (cause instanceof SecurityPolicyException) {
							SecurityPolicyException.ErrorCode errorCode = ((SecurityPolicyException) cause).getErrorCode();
							if (errorCode == SecurityPolicyException.ErrorCode.WEB_SESSION_EXPIRY) {
								if(iamAccount != null){
									LOGGER.error("[session expiry] " +iamAccount.getOrg().getOrgId()+"_"+iamAccount.getUser().getUid());
								}
								if(request.getRequestURI().endsWith("application/fetchDetails")) {
									return "login";
								}
								return "sessionexpired";
							}
						}
						throw ex;
					}

					if (iamAccount != null) {
						checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting IAM account user => {0}", iamAccount.getUser() == null ? -1 : iamAccount.getUser().getId()), request);
						request.setAttribute("iamAccount", iamAccount);
						Span.current().setAttribute("enduser.id", String.valueOf(iamAccount.getUser().getId()));
					}
					else {
						return handleLogin("account object missing", null);
					}
				}
			}
			else {
				AccountUtil.setAuthMethod(AccountUtil.AuthMethod.REMOTE_SCREEN);
				boolean authStatus = handleRemoteScreenAuth(request);//both tv mode and new device mode handled
				if (!authStatus) {
					LOGGER.log(Level.FATAL, "you are not allowed to access this page from remote screen.");
					return Action.ERROR;
				}
				checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "Catch all else of auth methods", request);
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "error in auth interceptor", e);
			return handleLogin("error in auth interceptor", e);
		}

		request.getAttribute("iamAccount");
		long timeTaken = System.currentTimeMillis() - time;
		logTimeTaken(this.getClass().getSimpleName(), timeTaken, request);
		AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), "Auth interceptor done", request);
		String resp = arg0.invoke();
		if (AccessLogFilter.isGetAvailableRequest(request)) {
			LOGGER.info("Auth interceptor done for url : "+request.getRequestURI()+AuthInterceptor.getResponseCode());
		}
		return resp;
	}

	public static String getResponseCode() {
		HttpServletResponse response = ServletActionContext.getResponse();
		return response == null ? ". Response is null" : ". Response status => "+String.valueOf(response.getStatus());
	}

	public static final void logTimeTaken(String className, long timeTaken, HttpServletRequest request) {
		if (timeTaken > 50 &&
				request != null &&
				StringUtils.isNotEmpty(request.getRequestURI()) &&
				(request.getRequestURI().endsWith("/workorders/close")
						|| request.getRequestURI().endsWith("/workorders/update"))
		) {
			LOGGER.info(MessageFormat.format("Time taken in {0} for uri {1} is {2}", className, request.getRequestURI(), timeTaken));
		}
	}
	
	private String handleLogin(String reason, Throwable ex)  {
		LOGGER.log(Level.INFO, reason, ex);
		return Action.LOGIN;
	}

	private boolean isRemoteScreenMode(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Remote-Screen");
		String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
		String deviceTokenNew = FacilioCookie.getUserCookie(request, "fc.deviceTokenNew");
		String facilioToken1 = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
		String facilioToken2 = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
		String headerToken = request.getHeader("Authorization");
		
		if ( remoteScreenHeader != null && "true".equalsIgnoreCase(remoteScreenHeader.trim())) {
			return true;
		}
		else if ((deviceToken != null && !"".equals(deviceToken))||(deviceTokenNew != null && !"".equals(deviceTokenNew))  && facilioToken1 == null && facilioToken2 == null) {
			return true;
		}
		else if (headerToken != null && headerToken.startsWith("Bearer device ")) {
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
				RemoteScreenContext remoteScreen = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() ->  ScreenUtil.getRemoteScreen(connectedScreenId));
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
				ConnectedDeviceContext deviceContext = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.getConnectedDevice(connectedDeviceId));
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

	public static void checkIfPuppeteerRequestAndLog(String className, String msg, HttpServletRequest request) {
		if (isPuppeteerRequest()) {
			LOGGER.info(MessageFormat.format("{0} => {1}. Request Uri : {2}", className, msg, request != null ? request.getRequestURI() : "null request"));
		}
	}

	public static boolean isPuppeteerRequest() {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		if (request != null) {
			String isExport = request.getHeader(RequestUtil.X_IS_EXPORT);
			return StringUtils.isNotEmpty(isExport);
		}
		return false;
	}
	
}