package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.point.PointEnum;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UnsubscribeInstanceIoTCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(SubscribeInstanceIoTCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.UNSUBSCRIBE_IDS);
		if (ids != null) {
			long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
			TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("subscribeStatus", PointEnum.SubscribeStatus.IN_PROGRESS.getIndex()));
			
			List<Map<String, Object>> instanceList =  TimeSeriesAPI.getUnmodeledInstances(ids);
			PublishData data = IoTMessageAPI.publishIotMessage(instanceList, IotCommandType.UNSUBSCRIBE);
			
			context.put(ContextNames.PUBLISH_DATA, data);
			
			ControllerAPI.updateControllerModifiedTime(controllerId);
		}
		return false;
	}

}
