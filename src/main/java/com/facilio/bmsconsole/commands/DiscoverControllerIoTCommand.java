package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class DiscoverControllerIoTCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		PublishData data = IoTMessageAPI.publishIotMessage(controllerId, IotCommandType.DISCOVER);
		context.put(ContextNames.PUBLISH_DATA, data);
		return false;
	}

}
