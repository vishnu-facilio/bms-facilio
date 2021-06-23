package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;

public class AddEnergyPredictionCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddEnergyPredictionCommand.class.getName());

	@Override
	public boolean executeCommand(Context jc) throws Exception {
		
		try
		{
			LOGGER.info("Inside Energy Prediction Command");
			long energyMeterID=(long) jc.get("energyMeterID");
			EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
			if(assetContext != null){
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				FacilioModule module = modBean.getModule("energypredictionmllogreadings");
				List<FacilioField> fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLLogPredictCheckGamFields();
				MLAPI.addReading(Collections.singletonList(energyMeterID),"EnergyPredictionMLLogReadings",fields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING,module);
				
				module = modBean.getModule("energypredictionmlreadings");
				fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLPredictCheckGamFields();
				MLAPI.addReading(Collections.singletonList(energyMeterID),"EnergyPredictionMLReadings", fields,ModuleFactory.getMLReadingModule().getTableName(),module);
				
				checkGamModel(energyMeterID,assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),(String) jc.get("modelPath"));
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
	
	private void checkGamModel(long ratioCheckMLID, EnergyMeterContext context,JSONObject mlModelVariables,JSONObject mlVariables,String modelPath) throws Exception
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
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,String> aggregationMap = new HashMap<String, String>();
		
		if (mlVariables != null) {
			for(Object entry:mlVariables.entrySet()){
				Map.Entry en = (Map.Entry) entry;
				if(((JSONObject)en.getValue()).containsKey("maxSamplingPeriod")){
					maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("maxSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("futureSamplingPeriod")){
					futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("futureSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("aggregation")){
					aggregationMap.put(en.getKey().toString(), ((JSONObject)en.getValue()).get("aggregation").toString().toUpperCase());
				}
			}
		}
		
		MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true,aggregationMap.containsKey(energyField.getName())? aggregationMap.get(energyField.getName()):"SUM");
		MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false,aggregationMap.containsKey(markedField.getName())? aggregationMap.get(markedField.getName()):"SUM");
		MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):31536000000l,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):172800000l,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
		
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
		
	}


	
}
