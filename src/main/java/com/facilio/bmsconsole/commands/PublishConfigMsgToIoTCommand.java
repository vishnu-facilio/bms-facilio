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

public class PublishConfigMsgToIoTCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(PublishConfigMsgToIoTCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		boolean inUse = (boolean)context.get(FacilioConstants.ContextNames.CONFIGURE);
		List<Map<String, Object>> instances =  TimeSeriesAPI.getUnmodeledInstances(ids);
		PublishData data;
		if (inUse) {
			data = IoTMessageAPI.publishIotMessage(instances, IotCommandType.CONFIGURE);
		}
		else {
			data = IoTMessageAPI.publishIotMessage(instances, IotCommandType.UNCONFIGURE);
		}
		context.put(ContextNames.PUBLISH_DATA, data);
		
		TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("configureStatus", PointEnum.ConfigureStatus.IN_PROGRESS.getIndex()));
		
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		ControllerAPI.updateControllerModifiedTime(controllerId);
		
		return false;
	}
}
