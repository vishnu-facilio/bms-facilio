package com.facilio.bmsconsole.commands;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class CreateAccountCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		JSONObject signupInfo = (JSONObject) context.get(FacilioConstants.ContextNames.SIGNUP_INFO);
		
		System.out.println("This is the map :- "+signupInfo);

		try {
			String name = (String) signupInfo.get("name");
			String email = (String) signupInfo.get("email");
			String phone = (String) signupInfo.get("phone");
			String companyName = (String) signupInfo.get("companyname");
			String orgDomain = (String) signupInfo.get("domainname");
			String timezone = (String) signupInfo.get("timezone");
			String password = (String) signupInfo.get("password");
			
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
			
			Organization org = new Organization();
			org.setName(companyName);
			org.setDomain(orgDomain);
			org.setCountry(locale.getCountry());
			org.setTimezone(timezone);
			org.setCreatedTime(System.currentTimeMillis());
			
			long orgId = AccountUtil.getOrgBean().createOrg(org);
			Role superAdminRole = AccountUtil.getRoleBean().getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN);
			
			User user = new User();
			user.setName(name);
			user.setEmail(email);
			user.setUserVerified(false);
			user.setTimezone(timezone);
			user.setLanguage(locale.getLanguage());
			user.setCountry(locale.getCountry());
			if (phone != null) {
				user.setPhone(phone);
			}
			user.setRoleId(superAdminRole.getRoleId());
			user.setInviteAcceptStatus(true);
			user.setDefaultOrg(true);
			user.setUserStatus(true);
			user.setInvitedTime(System.currentTimeMillis());
			user.setPassword(password);
			long ouid = AccountUtil.getUserBean().createUser(orgId, user);
			
			context.put("orgId", orgId);
			context.put("ouid", ouid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
