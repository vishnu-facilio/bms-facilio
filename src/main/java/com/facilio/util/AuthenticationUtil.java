package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.iam.accounts.exceptions.AccountException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMUserUtil;

import java.util.Map;


public class AuthenticationUtil {

    public static IAMAccount validateProxyToken(HttpServletRequest request, IAMAccount proxyAccount) throws Exception {
        String proxyToken = FacilioCookie.getUserCookie(request, "fc.idToken.proxy");
        if (StringUtils.isEmpty(proxyToken)) {
            return null;
        }

        return IAMUserUtil.verifyProxyToken(proxyToken, proxyAccount);
    }
	
	public static IAMAccount validateToken(HttpServletRequest request, boolean portalUser) throws Exception {
		String facilioToken = null;
		
		if(portalUser) {
        	facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
        } else {
        	facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
        }
        String headerToken = request.getHeader("Authorization");

        if (facilioToken != null || headerToken != null) {

            if (headerToken != null && headerToken.trim().length() > 0) {
                if (headerToken.startsWith("Bearer facilio ")) {
                    facilioToken = headerToken.replace("Bearer facilio ", "");
                } else if(headerToken.startsWith("Bearer Facilio ")) { // added this check for altayer emsol data // Todo remove this later
                    facilioToken = headerToken.replace("Bearer Facilio ", "");
                } else {
                    facilioToken = request.getHeader("Authorization").replace("Bearer ", "");
                }
            }
            String overrideSessionCookie = FacilioCookie.getUserCookie(request, "fc.overrideSession");
            boolean overrideSessionCheck = false;
            if(overrideSessionCookie != null) {
                if("true".equalsIgnoreCase(overrideSessionCookie)) {
                    overrideSessionCheck = true;
                }
            }
            
            String userType = "web";
			String deviceType = request.getHeader("X-Device-Type");
			if (!StringUtils.isEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				userType = "mobile";
			}

			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioTokenv3(facilioToken, overrideSessionCheck, userType);
            String proxySessionToken = FacilioCookie.getUserCookie(request, "fc.idToken.proxy");
            String proxyUserToken = request.getHeader("X-Proxy-Token");
            if(proxyUserToken!=null && proxyUserToken.trim().length() > 0){
                proxySessionToken = request.getHeader("X-Proxy-Token").replace("Bearer ", "");
            } 
            if (StringUtils.isNotEmpty(proxySessionToken)) {
                Long userSessionId = iamAccount.getUserSessionId();
                if (userSessionId == null || userSessionId <= 0) {
                    throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED, "Session is missing");
                }

                Map<String, Object> userSession = IAMUserUtil.getUserSession(userSessionId);
                if (MapUtils.isEmpty(userSession)) {
                    throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED, "Invalid session");
                }

                Boolean isProxySession = (Boolean) userSession.get("isProxySession");
                if (isProxySession == null || !isProxySession) {
                    throw new AccountException(AccountException.ErrorCode.INVALID_PROXY_SESSION, "Invalid proxy session");
                }

                Map<String, Object> proxySession = IAMUserUtil.getProxySession(proxySessionToken);
                if (MapUtils.isEmpty(proxySession)) {
                    throw new AccountException(AccountException.ErrorCode.INVALID_PROXY_SESSION, "Invalid proxy session token");
                }

                if (!IAMUserUtil.isUserInProxyList((String) proxySession.get("email"))) {
                    throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED, "Not Authorized");
                }

                long proxiedSessionId = (long) proxySession.get("proxiedSessionId");
                if (proxiedSessionId != userSessionId) {
                    throw new AccountException(AccountException.ErrorCode.INVALID_PROXY_SESSION, "Proxy session and the user does not match");
                }

                iamAccount.getUser().setProxy((String) proxySession.get("email"));
            }
			return iamAccount;
        }
        return null;
	}
	
	

}