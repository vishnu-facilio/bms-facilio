package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class AddLoadPredictionCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddLoadPredictionCommand.class.getName());
	@Override
	public boolean executeCommand(Context jc) throws Exception {
		// TODO Auto-generated method stub
		try
		{
			LOGGER.info("Inside Load Prediction Command");
			long energyMeterID=(long) jc.get("energyMeterID");
			LOGGER.info("Energy Meter Id"+energyMeterID);
			
			EnergyMeterContext emContext2 = DeviceAPI.getEnergyMeter(energyMeterID);
			if(emContext2 != null){
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Long categoryId=emContext2.getCategory().getId();
				AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
				long assetModuleID = assetCategory.getAssetModuleID();
				List<FacilioModule> modules = modBean.getAllSubModules(assetModuleID);
				boolean moduleExist = modules.stream().anyMatch(m-> m != null && m.getName().equalsIgnoreCase("LoadPredictionMLLogReadings"));
				if (!moduleExist) {
					MLAPI.addReading(categoryId,"LoadPredictionMLLogReadings", FieldFactory.getMLLogLoadPredictFields(),ModuleFactory.getMLLogReadingModule().getTableName(), ModuleType.PREDICTED_READING);
				}
				moduleExist = modules.stream().anyMatch(m-> m != null && m.getName().equalsIgnoreCase("LoadPredictionMLReadings"));
				if (!moduleExist) {
					MLAPI.addReading(categoryId,"LoadPredictionMLReadings", FieldFactory.getMLLoadPredictFields(),ModuleFactory.getMLReadingModule().getTableName());
				}
                LOGGER.info("After adding Reading");
				
				checkGamModel(energyMeterID,emContext2, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),(String) jc.get("modelPath"));
				LOGGER.info("After check Gam Model");
			}else{
				LOGGER.info("Energy Meter context is Null");
			}
			return false;
		}
		catch(Exception e)
		{
			LOGGER.error("Error while adding Load Prediction Job", e);
			throw e;
		}
	}
	
	private void checkGamModel(long ratioCheckMLID, EnergyMeterContext context,JSONObject mlModelVariables,JSONObject mlVariables,String modelPath) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("loadpredictionmllogreadings");
		FacilioModule readingModule = modBean.getModule("loadpredictionmlreadings");
		
		FacilioField energyField = modBean.getField("totalDemand", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		long mlID = MLAPI.addMLModel(modelPath,logReadingModule.getModuleId(),readingModule.getModuleId());
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		if (mlVariables != null) {
			for (Object entry : mlVariables.entrySet()) {
				Map.Entry en = (Map.Entry) entry;
				maxSamplingPeriodMap.put(en.getKey().toString(),Long.parseLong(((JSONObject) en.getValue()).get("maxSamplingPeriod").toString()));
				futureSamplingPeriodMap.put(en.getKey().toString(),Long.parseLong(((JSONObject) en.getValue()).get("futureSamplingPeriod").toString()));
			}
		}
		
		MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true);
		MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):172800000l,false);
		
		MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
		MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
		
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		
		for(Object entry:mlModelVariables.entrySet()){
			Map.Entry<String, String> en = (Map.Entry) entry;
			MLAPI.addMLModelVariables(mlID, en.getKey(), en.getValue());
		}
		
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);

		try {
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
		} catch (InterruptedException e) {
			Thread.sleep(1000);
		}
		LOGGER.info("checkGamModel Completed");
	}
	
}
