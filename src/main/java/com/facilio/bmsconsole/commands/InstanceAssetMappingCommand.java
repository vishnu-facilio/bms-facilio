package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class InstanceAssetMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		Integer unit= (Integer) context.get(FacilioConstants.ContextNames.UNIT_POINTS);
		Map<String, Object> instanceMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		String deviceName = (String) instanceMap.get("deviceName");
		long assetId = (long) instanceMap.get("resourceId");
		String instanceName = (String) instanceMap.get("instanceName");
		long fieldId = (long) instanceMap.get("fieldId");
		long categoryId = (long) instanceMap.get("categoryId");
		TimeSeriesAPI.insertOrUpdateInstance(deviceName, assetId, categoryId, controllerId, instanceName, fieldId, unit);
		
		return false;
	}

}
