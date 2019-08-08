package com.facilio.util;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMUserUtil;


public class AuthenticationUtil {
	
	public static IAMAccount validateToken(HttpServletRequest request, boolean portalUser, String portalDomain) throws AccountException {
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
            
			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioToken(facilioToken, overrideSessionCheck, null, portalDomain);
			return iamAccount;
        }
        return null;
	}
	

}