package com.facilio.util;

import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;

import javax.servlet.http.HttpServletRequest;


public class AuthenticationUtil {

    public static CognitoUtil.CognitoUser getCognitoUser(HttpServletRequest request) throws Exception {
        String facilioToken = LoginUtil.getUserCookie(request, "fc.idToken.facilio");
        String headerToken = request.getHeader("Authorization");

        if (facilioToken != null || headerToken != null) {

            if (headerToken != null) {
                if (headerToken.startsWith("Bearer facilio ")) {
                    facilioToken = headerToken.replace("Bearer facilio ", "");
                } else {
                    headerToken = request.getHeader("Authorization").replace("Bearer ", "");
                }
            }

            System.out.println("auth util : The header authtoken is " + headerToken);
            System.out.println("auth util : The facilioToken authtoken is " + facilioToken);

            CognitoUtil.CognitoUser cognitoUser = (facilioToken != null) ? CognitoUtil.verifiyFacilioToken(facilioToken) : CognitoUtil.verifyIDToken(headerToken);
            return cognitoUser;
        }
        return  null;
    }
}
