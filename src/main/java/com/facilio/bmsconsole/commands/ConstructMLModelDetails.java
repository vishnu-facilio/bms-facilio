package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructMLModelDetails extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(ConstructMLModelDetails.class.getName());
	private V3MLServiceContext mlServiceContext;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);

		if (!mlServiceContext.getServiceType().equals("default")) {
			return false;
		}
		try {
			List<List<Map<String, Object>>> models = new ArrayList<>();
			switch (mlServiceContext.getModelName()) {
				case "energyprediction": {
					long energyMeterID = mlServiceContext.getParentAssetId();
					EnergyMeterContext assetContext = getEnergyMeter(energyMeterID);
					List<Map<String, Object>> model = new ArrayList<>();
					model.add(getEnergyReading(assetContext.getId()));
					model.add(getTemperatureReading(assetContext.getSiteId()));
					models.add(model);
					break;
				}
				case "loadprediction": {
					long energyMeterID = mlServiceContext.getParentAssetId();
					EnergyMeterContext assetContext = getEnergyMeter(energyMeterID);
					List<Map<String, Object>> model = new ArrayList<>();
					model.add(getPowerReading(assetContext.getId()));
					model.add(getTemperatureReading(assetContext.getSiteId()));
					models.add(model);
					break;
				}
				case "energyanomaly": {
					List<Long> assetIds = MLServiceUtil.getAllAssetIds(mlServiceContext);
					for (long energyMeterID : assetIds) {
						EnergyMeterContext assetContext = getEnergyMeter(energyMeterID);
						List<Map<String, Object>> model = new ArrayList<>();
						model.add(getEnergyReading(assetContext.getId()));
						model.add(getTemperatureReading(assetContext.getSiteId()));
						models.add(model);
					}
					break;
				}
				default: {
					String errMsg = "Given modelname is not available";
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errMsg);
				}
			}
			mlServiceContext.setModelReadings(models);
			MLServiceUtil.updateMLStatus(mlServiceContext, "Default models construction completed successfully");
			LOGGER.info("Constructed Models successfully for id " + mlServiceContext.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "ConstructModel failed");
		}
		return false;
	}

	private EnergyMeterContext getEnergyMeter(long energyMeterID) throws Exception {
		EnergyMeterContext energyMeterContext = DeviceAPI.getEnergyMeter(energyMeterID);
		if(energyMeterContext == null) {
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, "Given asset id not found");
		}
		return energyMeterContext;
	}

	private Map<String, Object> getEnergyReading(long energyId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String, Object> energyReading = new HashMap<>();
		energyReading.put("parentId", energyId);
		energyReading.put("name", energyField.getName());
		energyReading.put("fieldId", energyField.getFieldId());
		return energyReading;

	}

	private Map<String, Object> getPowerReading(long energyId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField energyField = modBean.getField("totalDemand", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String, Object> energyReading = new HashMap<>();
		energyReading.put("parentId", energyId);
		energyReading.put("name", energyField.getName());
		energyReading.put("fieldId", energyField.getFieldId());
		return energyReading;

	}

	private Map<String, Object> getTemperatureReading(long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		Map<String, Object> temperatureReading = new HashMap<>();
		temperatureReading.put("parentId", siteId);
		temperatureReading.put("name", temperatureField.getName());
		temperatureReading.put("fieldId", temperatureField.getFieldId());
		return temperatureReading;

	}

}
