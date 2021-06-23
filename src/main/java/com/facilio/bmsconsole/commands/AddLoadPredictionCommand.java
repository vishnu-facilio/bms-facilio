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

public class AddLoadPredictionCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddLoadPredictionCommand.class.getName());
	@Override
	public boolean executeCommand(Context jc) throws Exception {
		// TODO Auto-generated method stub
		try
		{
			LOGGER.info("Inside Load Prediction Command");
			long energyMeterID=(long) jc.get("energyMeterID");
			
			EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
			if(assetContext != null){
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				FacilioModule module = modBean.getModule("loadpredictionmllogreadings");
				List<FacilioField> fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLLogLoadPredictFields();
				MLAPI.addReading(Collections.singletonList(energyMeterID),"LoadPredictionMLLogReadings", fields,ModuleFactory.getMLLogReadingModule().getTableName(), ModuleType.PREDICTED_READING,module);
				
				module = modBean.getModule("loadpredictionmlreadings");
				fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLLoadPredictFields();
				MLAPI.addReading(Collections.singletonList(energyMeterID),"LoadPredictionMLReadings", fields,ModuleFactory.getMLReadingModule().getTableName(),module);
				
				checkGamModel(energyMeterID,assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),(String) jc.get("modelPath"));
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
		
		FacilioField powerField = modBean.getField("totalDemand", FacilioConstants.ContextNames.ENERGY_DATA_READING);
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
					aggregationMap.put(en.getKey().toString(), ((JSONObject)en.getValue()).get("aggregation").toString());
				}
			}
		}
		
		MLAPI.addMLVariables(mlID,powerField.getModuleId(),powerField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(powerField.getName())? maxSamplingPeriodMap.get(powerField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(powerField.getName())? futureSamplingPeriodMap.get(powerField.getName()):0,true,aggregationMap.containsKey(powerField.getName())? aggregationMap.get(powerField.getName()):"SUM");
		MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):172800000l,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
		
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
