package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.auth.cookie.FacilioCookie;
import com.iam.accounts.util.AuthUtill;


public class AuthenticationUtil {
	
    public static String validateToken(HttpServletRequest request,boolean isPortaluser) throws Exception {
        String facilioToken = null;
        if(isPortaluser) {
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
           // CognitoUtil.CognitoUser cognitoUser =  CognitoUtil.verifiyFacilioToken(facilioToken, isPortaluser, overrideSessionCheck);
            String email =  AuthUtill.verifiyFacilioToken(facilioToken, isPortaluser, overrideSessionCheck);
            
            return email;
        }
        return  null;
    }
    
}
