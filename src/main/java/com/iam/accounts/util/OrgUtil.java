package com.iam.accounts.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;

public class OrgUtil {

    public static Account signUpOrg(org.json.simple.JSONObject jObj) throws Exception {
    	Long orgId = addOrg(jObj);
    	if(orgId > 0) {
    		long ouId = UserUtil.addSuperAdmin(jObj, orgId);
    	}
    	return orgId;
    }
    private static Organization addOrg(org.json.simple.JSONObject signupInfo) throws Exception{
    	String companyName = (String) signupInfo.get("companyname");
		String orgDomain = (String) signupInfo.get("domainname");
		String timezone = (String) signupInfo.get("timezone");
		HttpServletRequest request = ServletActionContext.getRequest();
		Locale locale = request.getLocale();
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
