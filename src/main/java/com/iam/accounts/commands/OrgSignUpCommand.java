package com.iam.accounts.commands;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.security.auth.login.AccountException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.iam.accounts.util.AuthUtill;
import com.iam.accounts.util.UserUtil;

public class OrgSignUpCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj = (JSONObject)context.get("signUpObj");
		Locale locale = (Locale)context.get("locale");
		
		Organization org = addOrg(jObj, locale);
    	//to be removed
    	AccountUtil.setCurrentAccount(org.getOrgId());
       if(org.getOrgId() > 0) {
    		User user = UserUtil.addSuperAdmin(jObj, org.getOrgId());
    		Account acc = AuthUtill.getCurrentAccount(org, user);
    		context.put("account", acc);
    	}
       
    	return false;
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
