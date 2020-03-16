package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;

public class AppUserAPI {

	public static long addUserInApp(User user, AppDomain appDomain) throws Exception {
	   return AccountUtil.getUserBean().addToORGUsersApps(user, appDomain.getDomain());
	}
	
	public static int deleteUserFromApp(User user, AppDomain appDomain) throws Exception {
	   return AccountUtil.getUserBean().deleteUserFromApps(user, appDomain);
	}
}
