package com.facilio.accounts.commands;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class CreateOrgCommand implements Command{
	
	private static final Logger LOGGER = LogManager.getLogger(CreateOrgCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
	
		JSONObject signupInfo = (JSONObject) context.get(FacilioConstants.ContextNames.SIGNUP_INFO);
		LOGGER.debug("This is the sign up map :- "+signupInfo);
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
		
		long orgId = AccountUtil.getOrgBean().createOrgv2(org);
		context.put("orgId", orgId);
		return false;
	}

}
