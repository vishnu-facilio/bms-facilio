package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddUserMobileSettingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		UserMobileSetting userMobileSetting = (UserMobileSetting) context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
		if (userMobileSetting != null) 
		{
			AccountUtil.getUserBean().addUserMobileSetting(userMobileSetting);
		}
		else 
		{
			throw new IllegalArgumentException("User Mobile Setting Object cannot be null");
		}
		return false;
	}

}
