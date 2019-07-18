package com.iam.accounts.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.security.auth.login.AccountException;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;

public class OrgUtil {

    public static Account signUpOrg(org.json.simple.JSONObject jObj, Locale locale) throws Exception {
    	try {
	    	Organization org = addOrg(jObj, locale);
	    	//remove
	    	AccountUtil.setCurrentAccount(org.getOrgId());
	    	if(org.getOrgId() > 0) {
	    		User user = UserUtil.addSuperAdmin(jObj, org.getOrgId());
	    		return AuthUtill.getCurrentAccount(org, user);
	    	}
    	} finally {
    		AccountUtil.cleanCurrentAccount();
    	}
    	return null;
    	
    }
    private static Organization addOrg(org.json.simple.JSONObject signupInfo, Locale locale) throws Exception{
    	String companyName = (String) signupInfo.get("companyname");
		String orgDomain = (String) signupInfo.get("domainname");
		 
        Organization orgObj = AuthUtill.getOrgBean().getOrgv2(orgDomain);
        if(orgObj != null) {
            throw new AccountException("Org Domain Name already exists");
        }

		String timezone = (String) signupInfo.get("timezone");
		if (locale == null) {
			locale = Locale.US;
		}
		if (timezone == null) {
			Calendar calendar = Calendar.getInstance(locale);
			TimeZone timezoneObj = calendar.getTimeZone();
			if (timezoneObj == null) {
				timezone = "America/Los_Angeles";
			}
			else {
				timezone = timezoneObj.getID();
			}
		}
		signupInfo.put("locale", locale);
		
		Organization org = new Organization();
		org.setName(companyName);
		org.setDomain(orgDomain);
		org.setCountry(locale.getCountry());
		org.setTimezone(timezone);
		org.setCreatedTime(System.currentTimeMillis());
		
		return AuthUtill.getOrgBean().createOrgv2(org);
    }
    

}
