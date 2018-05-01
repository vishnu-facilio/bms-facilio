package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.dto.Account;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;


public class AuthenticationUtil {

    public static CognitoUtil.CognitoUser getCognitoUser(HttpServletRequest request,boolean isPortaluser) throws Exception {
        String facilioToken = null;
        if(isPortaluser) {
        	facilioToken = LoginUtil.getUserCookie(request, "fc.idToken.facilioportal");
        } else {
        	facilioToken = LoginUtil.getUserCookie(request, "fc.idToken.facilio");
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


            CognitoUtil.CognitoUser cognitoUser = (facilioToken != null) ? CognitoUtil.verifiyFacilioToken(facilioToken, isPortaluser) : CognitoUtil.verifyIDToken(headerToken);
            return cognitoUser;
        }
        return  null;
    }

    public static boolean checkIfSameUser(Account currentAccount, CognitoUtil.CognitoUser cognitoUser){
        return currentAccount != null && cognitoUser != null && currentAccount.getUser() != null && (currentAccount.getUser().getEmail().equalsIgnoreCase(cognitoUser.getEmail()));
    }

}
