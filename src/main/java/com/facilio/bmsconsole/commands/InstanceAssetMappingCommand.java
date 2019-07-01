package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class InstanceAssetMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
//		Integer unit= (Integer) context.get(FacilioConstants.ContextNames.UNIT_POINTS);
		List<Map<String, Object>> instances = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		Set<Long> assetIds = new HashSet<>();
		for (Map<String, Object> instanceMap: instances) {
			String deviceName = (String) instanceMap.get("device");
			long assetId = (long) instanceMap.get("resourceId");
			assetIds.add(assetId);
			String instanceName = (String) instanceMap.get("instance");
			long fieldId = (long) instanceMap.get("fieldId");
			long categoryId = (long) instanceMap.get("categoryId");
			TimeSeriesAPI.insertOrUpdateInstance(deviceName, assetId, categoryId, controllerId, instanceName, fieldId, null);
		}
		AssetsAPI.updateAssetConnectionStatus(assetIds, true);
		
		return false;
	}

}
