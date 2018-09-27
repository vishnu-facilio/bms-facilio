package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.dto.Account;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;


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

            if (headerToken != null) {
                if (headerToken.startsWith("Bearer facilio ")) {
                    facilioToken = headerToken.replace("Bearer facilio ", "");
                } else {
                    headerToken = request.getHeader("Authorization").replace("Bearer ", "");
                }
            }

            String overrideSessionCookie = FacilioCookie.getUserCookie(request, "fc.overrideSession");
            boolean overrideSessionCheck = overrideSessionCookie != null && overrideSessionCookie.equalsIgnoreCase("true");
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
