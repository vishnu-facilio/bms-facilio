package com.facilio.util;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMUserUtil;


public class AuthenticationUtil {
	
	public static IAMAccount validateToken(HttpServletRequest request, boolean portalUser) throws AccountException {
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
			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioToken(facilioToken, false, overrideSessionCheck, null);
			return iamAccount;
        }
        return null;
	}
	
//    public static Account validateToken(HttpServletRequest request, boolean isPortaluser) {
//        String facilioToken = null;
//        if(isPortaluser) {
//        	facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
//        } else {
//        	facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
//        }
//        String headerToken = request.getHeader("Authorization");
//
//        if (facilioToken != null || headerToken != null) {
//
//            if (headerToken != null && headerToken.trim().length() > 0) {
//                if (headerToken.startsWith("Bearer facilio ")) {
//                    facilioToken = headerToken.replace("Bearer facilio ", "");
//                } else if(headerToken.startsWith("Bearer Facilio ")) { // added this check for altayer emsol data // Todo remove this later
//                    facilioToken = headerToken.replace("Bearer Facilio ", "");
//                } else {
//                    facilioToken = request.getHeader("Authorization").replace("Bearer ", "");
//                }
//            }
//            
//    		String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
//    		if (currentOrgDomain == null) {
//    			currentOrgDomain = request.getHeader("X-Current-Org"); 
//    		}
//
//            String overrideSessionCookie = FacilioCookie.getUserCookie(request, "fc.overrideSession");
//            boolean overrideSessionCheck = false;
//            if(overrideSessionCookie != null) {
//                if("true".equalsIgnoreCase(overrideSessionCookie)) {
//                    overrideSessionCheck = true;
//                }
//            }
//            try {
//            	IAMAccount iamAccount = IAMUserUtil.verifiyFacilioToken(facilioToken, isPortaluser, overrideSessionCheck, currentOrgDomain);
//            	if (iamAccount != null) {
//	            	User appUser = new User(iamAccount.getUser());
//	            	Account account = new Account(iamAccount.getOrg(), appUser);
//	            	return account;
//            	}
//            } 
//            catch (AccountException e) {
//            	return null;
//			}
//        }
//        return  null;
//    }
    
}
