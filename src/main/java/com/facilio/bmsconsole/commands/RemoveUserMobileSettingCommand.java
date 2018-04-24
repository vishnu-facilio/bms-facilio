package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class RemoveUserMobileSettingCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		
		String mobileInstanceId = (String) context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
		if (mobileInstanceId != null) 
		{
			AccountUtil.getUserBean().removeUserMobileSetting(mobileInstanceId);
		}
		else 
		{
			throw new IllegalArgumentException("Mobile Instance Id cannot be null");
		}
		return false;
	}

}
