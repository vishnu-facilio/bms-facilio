package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class MarkUnmodeledInstanceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> ids = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		Map<String, String> deviceNames = (Map<String, String>)context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		boolean inUse = (boolean)context.get(FacilioConstants.ContextNames.CONFIGURE);
		Map<String, Object> instance = new HashMap();
		instance.put("inUse", inUse);
		if (deviceNames != null) {
			// TODO implement update case
			for (Map.Entry<String, String> entry : deviceNames.entrySet()) {
				instance.put("device", entry.getValue());
				TimeSeriesAPI.updateInstances(Collections.singletonList(Long.parseLong(entry.getKey())), instance);
			}
		}
		else {
			TimeSeriesAPI.updateInstances(ids, instance);
		}
		
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		ControllerAPI.updateControllerModifiedTime(controllerId);
		
		return false;
	}

}
