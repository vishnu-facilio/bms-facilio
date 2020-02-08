package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.util.IAMUserUtil;


public class AuthenticationUtil {
	
	public static IAMAccount validateToken(HttpServletRequest request, boolean portalUser, String portalDomain) throws Exception {
		String facilioToken = null;
		
		if(StringUtils.isEmpty(portalDomain)) {
			portalDomain = "app";
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
            
            AppType appType = null;
            if(!portalDomain.equals("app")) {
            	appType = AppType.SERVICE_PORTAL;
				if(request.getServerName() != null && request.getServerName().contains("faciliovendors.com")) {
					appType = AppType.VENDOR_PORTAL;
				}
				else if(request.getServerName() != null && request.getServerName().contains("faciliotenants.com")) {
					appType = AppType.TENANT_PORTAL;
				}
            }
            String userType = "web";
			String deviceType = request.getHeader("X-Device-Type");
			if (!StringUtils.isEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				userType = "mobile";
			}
			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioToken(facilioToken, overrideSessionCheck, null, portalDomain, userType,appType);
			return iamAccount;
        }
        return null;
	}
	

}