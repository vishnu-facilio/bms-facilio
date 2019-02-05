package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class UnsubscribeInstanceIoTCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(SubscribeInstanceIoTCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.UNSUBSCRIBE_IDS);
		if (ids != null) {
			long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
			TimeSeriesAPI.updateUnmodeledInstances(ids, Collections.singletonMap("subscribed", false));
			List<Map<String, Object>> instanceList =  TimeSeriesAPI.getSubscribedInstances(controllerId);
			if (instanceList != null && !instanceList.isEmpty()) {
				IoTMessageAPI.publishIotMessage(instanceList, IotCommandType.SUBSCRIBE, null, data -> rollbackUnSubscribe(ids));
			}
			else {
				IoTMessageAPI.publishIotMessage(controllerId, IotCommandType.UNSUBSCRIBE, null, data -> rollbackUnSubscribe(ids));
			}
			ControllerAPI.updateControllerModifiedTime(controllerId);
		}
		return false;
	}
	
	public static void rollbackUnSubscribe (List<Long> ids) {
		try {
			TimeSeriesAPI.updateUnmodeledInstances(ids, Collections.singletonMap("subscribed", true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred while unsubscribing" , e);
			CommonCommandUtil.emailException("UnsubscribeInstanceIoTCommand", "Error occurred while unsubscribing", e);
		}
	}

}
