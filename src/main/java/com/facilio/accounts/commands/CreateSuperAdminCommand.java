package com.facilio.accounts.commands;

import java.util.Locale;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AccountUser;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;

public class CreateSuperAdminCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = (long) context.get("orgId");
		
		JSONObject signupInfo = (JSONObject) context.get(FacilioConstants.ContextNames.SIGNUP_INFO);
		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String serverName = (String) signupInfo.get("servername");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");
		
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
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setUserStatus(true);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPassword(password);
		user.setServerName(serverName);
		if(AwsUtil.isDevelopment()) {
			user.setUserVerified(true);
		}
		long ouid = AccountUtil.getUserBean().createUserv2(orgId, user);
		context.put("ouid", ouid);
		return false;
	}

}
