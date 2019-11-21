package com.facilio.bmsconsole.commands;

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
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class AddEnergyPredictionCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddEnergyPredictionCommand.class.getName());

	@Override
	public boolean executeCommand(Context jc) throws Exception {
		
		try
		{
			LOGGER.info("Inside Execute Command");
			long energyMeterID=(long) jc.get("energyMeterID");
			LOGGER.info("Inside Energy Meter Command"+energyMeterID);
			EnergyMeterContext emContext2 = DeviceAPI.getEnergyMeter(energyMeterID);
			LOGGER.info("Energy meter context :"+emContext2);
			if(emContext2 != null){
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Long categoryId=emContext2.getCategory().getId();
				AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
				long assetModuleID = assetCategory.getAssetModuleID();
				List<FacilioModule> modules = modBean.getAllSubModules(assetModuleID);
				boolean moduleExist = modules.stream().anyMatch(m-> m != null && m.getName().equalsIgnoreCase("EnergyPredictionMLLogReadings"));
				if(!moduleExist){
    				MLAPI.addReading(categoryId,"EnergyPredictionMLLogReadings",FieldFactory.getMLLogPredictCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING);
                }
                moduleExist = modules.stream().anyMatch(m-> m != null && m.getName().equalsIgnoreCase("EnergyPredictionMLReadings"));
				if (!moduleExist) {
					MLAPI.addReading(categoryId,"EnergyPredictionMLReadings", FieldFactory.getMLPredictCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
				}
				
				LOGGER.info("After adding Reading");
				
				checkGamModel(energyMeterID,emContext2, (JSONObject) jc.get("mlModelVariables"),(String) jc.get("modelPath"));
				LOGGER.info("After check Gam Model");
			}else{
				LOGGER.info("Energy Meter context is Null");
			}
			return false;
		}
		catch(Exception e)
		{
			LOGGER.error("Error while adding Energy Prediction Job", e);
			throw e;
		}
		
	}
	
	private void checkGamModel(long ratioCheckMLID, EnergyMeterContext context,JSONObject mlModelVariables,String modelPath) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("energypredictionmllogreadings");
		FacilioModule readingModule = modBean.getModule("energypredictionmlreadings");
		
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
	
		long mlID = MLAPI.addMLModel(modelPath,logReadingModule.getModuleId(),readingModule.getModuleId());
		MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),7776000000l,0,true);
		MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),7776000000l,0,false);
		MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),31536000000l,172800000l,false);
		
		MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
		MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
    	
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		mlModelVariables.entrySet().forEach(entry -> {
			try {
				Map.Entry<String, String> en = (Map.Entry) entry;
				MLAPI.addMLModelVariables(mlID, en.getKey(), en.getValue());
			} catch (Exception e) {
				LOGGER.fatal("Error when adding ML Model variables for EnergyPrediction Job " + mlID);
			}
		});

		
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);

		try {
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
		} catch (InterruptedException e) {
			Thread.sleep(1000);
		}
		
	}


	
}
