package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateImportPointsDataCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = LogManager.getLogger(UpdateImportPointsDataCommand.class.getName());
	private static List<FacilioContext> unmodelledHistoricalContexts;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<Map<String, Object>> finalList = (List<Map<String, Object>>) context.get("POINTS_LIST");
		long controllerId = (long) context.get("CONTROLLER_ID");

		try {
			// FacilioTransactionManager.INSTANCE.getTransactionManager().begin();

			for (Map<String, Object> prop : finalList) {
				if (prop.isEmpty()) {
					continue;
				}

				String deviceName = (String) prop.get("Device");
				String instanceName = (String) prop.get("Instance");
				long assetId = Long.parseLong((String) prop.get("Assets"));
				long categoryId = Long.parseLong((String) prop.get("Asset Category"));
				long fieldId = Long.parseLong((String) prop.get("Reading")) ;
				TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId, categoryId, controllerId, instanceName,
						fieldId, null);

				FacilioContext contextImport = new FacilioContext();
				contextImport.put(FacilioConstants.ContextNames.DEVICE_DATA, deviceName);
				contextImport.put(FacilioConstants.ContextNames.INSTANCE_INFO, instanceName);
				contextImport.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
				contextImport.put(FacilioConstants.ContextNames.FIELD_ID, fieldId);
				contextImport.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);

				if (unmodelledHistoricalContexts == null) {
					unmodelledHistoricalContexts = new ArrayList<>();
				}
				unmodelledHistoricalContexts.add(contextImport);

				// FacilioTransactionManager.INSTANCE.getTransactionManager().commit();

			}
		} catch (Exception e) {
			LOGGER.error("Exception occured While updating Points Table   :", e);
			// FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
			throw e;
		}

		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub

		if (CollectionUtils.isNotEmpty(unmodelledHistoricalContexts)) {
			// TODO handle multiple mappings in ProcessUnmodelledHistoricalData
			for (FacilioContext context : unmodelledHistoricalContexts) {
				FacilioTimer.scheduleInstantJob("ProcessUnmodelledHistoricalData", context);
			}
		}

		return false;
	}
}
