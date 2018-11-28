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
		
		Map<String, Object> instanceMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		if (instanceMap != null) {
			String deviceName = (String) instanceMap.get("deviceName");
			long assetId = (long) instanceMap.get("assetId");
			String instanceName = (String) instanceMap.get("instanceName");
			long fieldId = (long) instanceMap.get("fieldId");
			TimeSeriesAPI.insertOrUpdateInstance(deviceName, assetId, controllerId, instanceName, fieldId);
		}
		else {
			// For older UI...remove once new ui is completed
			String deviceName = (String)context.get(FacilioConstants.ContextNames.DEVICE_DATA);
			long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
			Map<String,Long> instanceFieldMap = (Map<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId,controllerId, instanceFieldMap);
		}
		
		return false;
	}

}
