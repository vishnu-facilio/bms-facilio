package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class DisconnectDeviceCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DeviceContext deviceContext=(DeviceContext)context.get(FacilioConstants.ContextNames.RECORD);
		long orgId=AccountUtil.getCurrentOrg().getId();
		FacilioService.runAsServiceWihReturn(() ->  DevicesUtil.disconnectDevice(deviceContext.getId(),orgId));		
		deviceContext.setIsDeviceConnected(false);
		return false;
	}

}
