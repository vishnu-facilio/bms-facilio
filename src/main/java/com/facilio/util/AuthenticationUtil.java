package com.facilio.util;

import com.facilio.accounts.dto.Account;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.fw.auth.CognitoUtil;

import javax.servlet.http.HttpServletRequest;


public class AuthenticationUtil {
	
    public static CognitoUtil.CognitoUser getCognitoUser(HttpServletRequest request,boolean isPortaluser) throws Exception {
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
            CognitoUtil.CognitoUser cognitoUser =  CognitoUtil.verifiyFacilioToken(facilioToken, isPortaluser, overrideSessionCheck);
            return cognitoUser;
        }
        return  null;
    }

    public static boolean checkIfSameUser(Account currentAccount, CognitoUtil.CognitoUser cognitoUser){
    	if( currentAccount != null && cognitoUser != null && currentAccount.getUser() != null) {
    		System.out.println(currentAccount.getUser().getEmail() + " mobile "+ currentAccount.getUser().getMobile() + " cognito "+ cognitoUser.getEmail());
    		//return (currentAccount.getUser().getEmail().equalsIgnoreCase(cognitoUser.getEmail()));
    		return true;
    	}
    	return false;
    }

}
