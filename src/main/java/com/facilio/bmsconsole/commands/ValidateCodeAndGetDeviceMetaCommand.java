package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.iam.accounts.impl.IamClient;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

@Log4j
public class ValidateCodeAndGetDeviceMetaCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		

		if (context.get(FacilioConstants.ContextNames.DEVICE_CODE) == null) {
			System.out.println("no code present , no validation needed");
			return false;
			// no code present in request ,no need to validate
		} else {
			
			String code=(String)context.get(FacilioConstants.ContextNames.DEVICE_CODE);
			Map<String, Object> deviceCodeRow = IamClient.getDeviceCodeInfo(code);

			if (deviceCodeRow == null) {
				LOGGER.error("Device code row not fetched.");
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
