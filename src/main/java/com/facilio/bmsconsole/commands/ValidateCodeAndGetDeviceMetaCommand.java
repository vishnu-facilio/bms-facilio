package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class ValidateCodeAndGetDeviceMetaCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		

		if (context.get(FacilioConstants.ContextNames.DEVICE_CODE) == null) {
			System.out.println("no code present , no validation needed");
			return false;
			// no code present in request ,no need to validate
		} else {
			
			String code=(String)context.get(FacilioConstants.ContextNames.DEVICE_CODE);
			Map<String, Object> deviceCodeRow = FacilioService
					.runAsServiceWihReturn(() -> DevicesUtil.getValidDeviceCodeRow(code));

			if (deviceCodeRow == null) {
				return true;// code invalid
			}
			// context.put(FacilioConstants.ContextNames.DEVICE_INFO,deviceCodeRow.get("info"));
			DeviceContext deviceContext = (DeviceContext) context.get(FacilioConstants.ContextNames.RECORD);
			
			if (deviceCodeRow.get("info") != null) {
				deviceContext.setDeviceInfo((String) deviceCodeRow.get("info"));
			}
			
			return false;
		}

	}
}
