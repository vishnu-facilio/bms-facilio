package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddUserMobileSettingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		UserMobileSetting userMobileSetting = (UserMobileSetting) context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
		if (userMobileSetting != null) 
		{
			if (userMobileSetting.getMobileInstanceId() != null && !"".equalsIgnoreCase(userMobileSetting.getMobileInstanceId().trim())) {
				AccountUtil.getUserBean().addUserMobileSetting(userMobileSetting);
			}
			else {
				throw new IllegalArgumentException("Mobile Instance ID cannot be null or empty");
			}
		}
		else 
		{
			throw new IllegalArgumentException("User Mobile Setting Object cannot be null");
		}
		return false;
	}

}
