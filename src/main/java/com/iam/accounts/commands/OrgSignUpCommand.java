package com.iam.accounts.commands;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.security.auth.login.AccountException;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.iam.accounts.util.IAMUserUtil;
import com.iam.accounts.util.IAMUtil;

public class OrgSignUpCommand extends FacilioCommand{

	
	private static Organization addOrg(org.json.simple.JSONObject signupInfo, Locale locale) throws Exception{
    	String companyName = (String) signupInfo.get("companyname");
		String orgDomain = (String) signupInfo.get("domainname");
		 
        Organization orgObj = IAMUtil.getOrgBean().getOrgv2(orgDomain);
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
		
		return IAMUtil.getOrgBean().createOrgv2(org);
    }

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj = (JSONObject)context.get("signUpObj");
		Locale locale = (Locale)context.get("locale");
		
		Organization org = addOrg(jObj, locale);
		if(org.getOrgId() > 0) {
    		IAMUser user = IAMUserUtil.addSuperAdmin(jObj, org.getOrgId());
    		IAMAccount iamAccount = IAMUtil.getCurrentAccount(org, user);
    		context.put("account", iamAccount);
    	}
		return false;
	}
    

}
