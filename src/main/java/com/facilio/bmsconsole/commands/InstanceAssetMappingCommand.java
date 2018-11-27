package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class InstanceAssetMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String deviceName = (String)context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
		Map<String,Long> instanceFieldMap = (Map<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		
		TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId,controllerId, instanceFieldMap);
		
		return false;
	}

}
