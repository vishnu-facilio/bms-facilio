package com.facilio.bmsconsoleV3.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.tasker.FacilioTimer;

public class AddEmailConversationModules extends SignUpData {
	
	public void addData() throws Exception {
		
		FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "getVerifiedEmailAddressFromAWS", 50, 300, "facilio");
	}
}
