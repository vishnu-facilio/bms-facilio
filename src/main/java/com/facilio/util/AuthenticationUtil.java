package com.facilio.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMUserUtil;


public class AuthenticationUtil {
	
	public static IAMAccount validateToken(HttpServletRequest request, boolean portalUser, String appDomain) throws Exception {
		String facilioToken = null;
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
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
			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioTokenv3(facilioToken, overrideSessionCheck, null, appDomain, userType);
			return iamAccount;
        }
        return null;
	}
	
	public static void addDomainCookie(String domain, HttpServletResponse response) {
		Cookie cookie = new Cookie("fc.currentOrg", domain);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10); // Make the cookie 10 year
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}
	

}