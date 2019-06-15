package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PublishConfigMsgToIoTCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(PublishConfigMsgToIoTCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		boolean inUse = (boolean)context.get(FacilioConstants.ContextNames.CONFIGURE);
		if (inUse) {
			List<Map<String, Object>> instances =  TimeSeriesAPI.getUnmodeledInstances(ids);
			IoTMessageAPI.publishIotMessage(instances, IotCommandType.CONFIGURE, null, data -> markInstancesAsNotInUse(ids));
		}
		else {
			// TODO
		}
		
		return false;
	}
	
	public static void markInstancesAsNotInUse (List<Long> ids) { //static because this is used in lambda
		try {
			TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("inUse", false));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred while marking instances as not in use", e);
			CommonCommandUtil.emailException("PublishConfigMsgToIoTCommand", "Error occurred while marking instances as not in use", e);
		}
	}
}
