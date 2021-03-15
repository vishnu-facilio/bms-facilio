package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class ConnectDeviceCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(context.get(FacilioConstants.ContextNames.DEVICE_CODE)==null)
		{	
			return false;
		}
		String deviceCode=(String)context.get(FacilioConstants.ContextNames.DEVICE_CODE);
		
		DeviceContext device=(DeviceContext)context.get(FacilioConstants.ContextNames.RECORD);
		//if passcode present , mark that code as connected, code is always valid ,as it is validated in previous command
		//before marking as connected copy the code 
		System.out.println("connecting device"+deviceCode+"with"+device.getId());
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		long connectedDeviceId=FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.addConnectedDevice(device.getId(),orgId));
		FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.markCodeAsConnected(deviceCode,connectedDeviceId));
		
		device.setIsDeviceConnected(true);
		device.setConnectedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(device.getId()));
		
		
				
		return false;
	}

}
