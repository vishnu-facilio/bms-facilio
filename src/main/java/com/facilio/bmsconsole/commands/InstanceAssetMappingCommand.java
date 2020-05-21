package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.tasker.FacilioTimer;
import com.facilio.timeseries.TimeSeriesAPI;

public class InstanceAssetMappingCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static List<FacilioContext> unmodelledHistoricalContexts;
	private static List<FacilioContext> modelledContexts;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		boolean skipValidation = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_VALIDATION, false);
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
			
			insertOrUpdateInstance(deviceName, assetId, categoryId, controllerId, instanceName, fieldId, null, skipValidation);
			
		}
		AssetsAPI.updateAssetConnectionStatus(assetIds, true);
		
		return false;
	}
	
	public static void insertOrUpdateInstance(String deviceName, long assetId, long categoryId, long controllerId, String instance, long fieldId ,Integer unit, boolean skipValidation) throws Exception {
		Map<String, Object> modeledData = TimeSeriesAPI.getMappedInstance(deviceName, instance, controllerId);
		if (modeledData == null) {
			TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId, categoryId, controllerId, instance, fieldId,unit);	
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DEVICE_DATA, deviceName);
			context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instance);
			context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
			context.put(FacilioConstants.ContextNames.FIELD_ID, fieldId);
			context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
			
			if (unmodelledHistoricalContexts == null) {
				unmodelledHistoricalContexts = new ArrayList<>();
			}
			unmodelledHistoricalContexts.add(context);
		}
		else {
			TimeSeriesAPI.updateInstanceAssetMapping(deviceName, assetId, categoryId, instance, fieldId, modeledData, unit, skipValidation);
			
			FacilioContext context = new FacilioContext();
			context.put(ContextNames.PREV_FIELD_ID, modeledData.get(ContextNames.FIELD_ID));
			context.put(ContextNames.PREV_PARENT_ID, modeledData.get("resourceId"));
			context.put(FacilioConstants.ContextNames.FIELD_ID, fieldId);
			context.put(FacilioConstants.ContextNames.PARENT_ID, assetId);
			
			if (modelledContexts == null) {
				modelledContexts = new ArrayList<>();
			}
			modelledContexts.add(context);
		}
	}
	
	@Override
	public boolean postExecute() throws Exception {
		if (CollectionUtils.isNotEmpty(unmodelledHistoricalContexts)) {
			// TODO handle multiple mappings in ProcessUnmodelledHistoricalData
			for(FacilioContext context: unmodelledHistoricalContexts) {
				FacilioTimer.scheduleInstantJob("ProcessUnmodelledHistoricalData", context);
			}
		}
		
		if (CollectionUtils.isNotEmpty(modelledContexts)) {
			// TODO handle multiple mappings in MigrateReadingData
			for(FacilioContext context: modelledContexts) {
				FacilioTimer.scheduleInstantJob("MigrateReadingData", context);
			}
		}
		
		return false;
	}

}
