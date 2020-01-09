package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.service.FacilioService;

public class SendEventToDeviceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			long orgId = AccountUtil.getCurrentOrg().getId();
			FacilioService.runAsService(() ->  DevicesUtil.reloadConf(orgId));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
