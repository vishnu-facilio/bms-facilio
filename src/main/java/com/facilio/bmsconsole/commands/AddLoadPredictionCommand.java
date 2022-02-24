package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;

public class AddLoadPredictionCommand extends FacilioCommand {
	
	
	private static final Logger LOGGER = Logger.getLogger(AddLoadPredictionCommand.class.getName());
	@Override
	public boolean executeCommand(Context jc) throws Exception {
		// TODO Auto-generated method stub
		
		V3MLServiceContext mlServiceContext = (V3MLServiceContext) jc.get(FacilioConstants.ContextNames.ML_SERVICE_DATA);
		long mlServiceId = -1;
		if(mlServiceContext!=null) {
			mlServiceId = mlServiceContext.getId();
		}

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
				
				long mlID = updateLoadModel(assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),(String) jc.get("modelPath"), mlServiceId);
				if(mlServiceContext!=null) {
					mlServiceContext.setMlID(mlID);
				}
				scheduleJob(mlID, mlServiceContext);
				LOGGER.info("After updating load model");
				
			}else{
				String errMsg = energyMeterID+" energy meter is not available";
				if(mlServiceContext!=null) {
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errMsg);
				}
				LOGGER.info(errMsg);
			}
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String errMsg = "Error while adding Load Prediction Job";
			if(mlServiceContext!=null) {
				throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
			}
			LOGGER.error(errMsg, e);
			throw e;
		}
	}
	
	private void scheduleJob(long mlID, V3MLServiceContext mlServiceContext) throws Exception {
		boolean isHistoric = false;
		if(mlServiceContext!=null) {
			isHistoric = mlServiceContext.isHistoric();
		}
		try {
			if(!isHistoric) {
				ScheduleInfo info = new ScheduleInfo();
				info = FormulaFieldAPI.getSchedule(FacilioFrequency.DAILY);
				MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
//			} else {
//				FacilioTimer.scheduleOneTimeJobWithTimestampInSec(mlID, "DefaultMLJob",(executionTime/1000), "ml");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Load prediction default job creation failed");
		}
	}
	
	private long updateLoadModel(EnergyMeterContext context,JSONObject mlModelVariables,JSONObject mlVariables,String modelPath, long mlServiceId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("loadpredictionmllogreadings");
		FacilioModule readingModule = modBean.getModule("loadpredictionmlreadings");
		
		FacilioField powerField = modBean.getField("totalDemand", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		long mlID = MLAPI.addMLModel(modelPath,context.getName(), logReadingModule.getModuleId(),readingModule.getModuleId(), mlServiceId);
		
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
			Map.Entry<String, Object> en = (Map.Entry) entry;
			MLAPI.addMLModelVariables(mlID, en.getKey(), String.valueOf(en.getValue()));
		}
		
		return mlID;
	}
	
}
