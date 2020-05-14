package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.point.PointEnum;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.timeseries.TimeSeriesAPI;

public class SubscribeInstanceIoTCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(SubscribeInstanceIoTCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String, Object>> instances = (List<Map<String, Object>>)context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		List<Long> ids = new ArrayList<>();
		for(Map<String, Object> instance: instances) {
			long id = (long) instance.get("id");
			ids.add(id);
			Map<String, Object> newInstance =new HashMap<>();
			newInstance.put("subscribeStatus", PointEnum.SubscribeStatus.IN_PROGRESS.getIndex());
			if (instance.get("thresholdJson") != null) {
				newInstance.put("thresholdJson", instance.get("thresholdJson"));
			}
			TimeSeriesAPI.updateInstances(Collections.singletonList(id), newInstance);
		}
		
		List<Map<String, Object>> instanceList =  TimeSeriesAPI.getUnmodeledInstances(ids);
		PublishData data = IoTMessageAPI.publishIotMessage(instanceList, IotCommandType.SUBSCRIBE);
		
		context.put(ContextNames.PUBLISH_DATA, data);
		
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		ControllerAPI.updateControllerModifiedTime(controllerId);
		
		return false;
	}
	
	public static void rollbackSubscribe (List<Long> ids) { //static because this is used in lambda
		try {
			Map<String, Object> prop = new HashMap<>();
			prop.put("subscribed", false);
			prop.put("thresholdJson", null);
			TimeSeriesAPI.updateInstances(ids, prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred while subscribing", e);
			CommonCommandUtil.emailException("SubscribeInstanceIoTCommand", "Error occurred while subscribing", e);
		}
	}

}
