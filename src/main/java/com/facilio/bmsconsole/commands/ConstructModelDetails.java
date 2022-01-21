package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLServiceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class ConstructModelDetails extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(InitMLServiceCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);

		try {
			List<Long> assetIds = mlServiceContext.getAssetList();

			List<List<Map<String, Object>>> models = new ArrayList<List<Map<String,Object>>>();
			switch(mlServiceContext.getModelName()){
			case "energyprediction":{
				long energyMeterID = assetIds.get(0);
				EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
				List<Map<String, Object>> model = new ArrayList<Map<String,Object>>();
				model.add(getEnergyReading(assetContext.getId()));
				model.add(getTemperatureReading(assetContext.getSiteId()));
				models.add(model);
				break;
			}
			case "loadprediction":{
				long energyMeterID = assetIds.get(0);
				EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
				List<Map<String, Object>> model = new ArrayList<Map<String,Object>>();
				model.add(getPowerReading(assetContext.getId()));
				model.add(getTemperatureReading(assetContext.getSiteId()));
				models.add(model);
				break;
			}
			case "energyanomaly":{
				for(long energyMeterID : assetIds) {
					EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
					List<Map<String, Object>> model = new ArrayList<Map<String,Object>>();
					model.add(getEnergyReading(assetContext.getId()));
					model.add(getTemperatureReading(assetContext.getSiteId()));
					models.add(model);
				}
				break;
			}
			default :{
				String errMsg = "Given modelname is not available";
				LOGGER.fatal(errMsg);
				mlServiceContext.updateStatus(errMsg);
				return true;
			}
			}
			mlServiceContext.setModels(models);
			mlServiceContext.updateReadingVariables();
			updateModels(mlServiceContext);
			LOGGER.info("Constructed Models successfully for usecase id "+mlServiceContext.getUseCaseId());

		}catch(Exception e) {
			e.printStackTrace();
			String errMsg = "Failed in predefined model constructions";
			if(mlServiceContext!=null) {
				mlServiceContext.updateStatus(errMsg);
			}
			LOGGER.info("Failed in ConstructModelDetails for usecase id "+mlServiceContext.getUseCaseId());
			return true;
		}
		return false;

	}

	private void updateModels(MLServiceContext mlServiceContext) throws Exception {
		Map<String, Object> row = new HashMap<>();
		row.put("mlModelMeta", mlServiceContext.getRequestMeta().toString());
		MLServiceAPI.updateMLService(mlServiceContext.getUseCaseId(), row);
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
