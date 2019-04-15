package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class SubscribeInstanceIoTCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(SubscribeInstanceIoTCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Map<String, Object>> instances = (List<Map<String, Object>>)context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		List<Long> ids = new ArrayList<>();
		for(Map<String, Object> instance: instances) {
			long id = (long) instance.get("id");
			ids.add(id);
			
			instance.put("subscribed", true);
			TimeSeriesAPI.updateUnmodeledInstances(Collections.singletonList(id), instance);
		}
		
		List<Map<String, Object>> instanceList =  TimeSeriesAPI.getUnmodeledInstances(ids);
		IoTMessageAPI.publishIotMessage(instanceList, IotCommandType.SUBSCRIBE, null, data -> rollbackSubscribe(ids));
		
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		ControllerAPI.updateControllerModifiedTime(controllerId);
		
		return false;
	}
	
	public static void rollbackSubscribe (List<Long> ids) { //static because this is used in lambda
		try {
			Map<String, Object> prop = new HashMap<>();
			prop.put("subscribed", false);
			prop.put("thresholdJson", null);
			TimeSeriesAPI.updateUnmodeledInstances(ids, prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred while subscribing", e);
			CommonCommandUtil.emailException("SubscribeInstanceIoTCommand", "Error occurred while subscribing", e);
		}
	}

}
