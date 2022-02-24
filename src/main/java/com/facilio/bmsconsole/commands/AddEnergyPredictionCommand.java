package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEnergyPredictionCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(AddEnergyPredictionCommand.class.getName());
	private static final long DAYS_IN_MILLISECONDS = 24*60*60*1000L;

	@Override
	public boolean executeCommand(Context jc) throws Exception {

		V3MLServiceContext mlServiceContext = (V3MLServiceContext) jc.get(FacilioConstants.ContextNames.ML_SERVICE_DATA);
		long mlServiceId = -1;
		if(mlServiceContext!=null) {
			mlServiceId = mlServiceContext.getId();
		}
		try
		{
			LOGGER.info("Inside Energy Prediction Command");
			long energyMeterID=(long) jc.get("energyMeterID");
			EnergyMeterContext assetContext = DeviceAPI.getEnergyMeter(energyMeterID);
			if(assetContext != null){
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule module = modBean.getModule("energyprediction");
				List<FacilioField> fields = modBean.getAllFields(module.getName());

				long mlID = updateEnergyModel(assetContext, (JSONObject) jc.get("mlModelVariables"), (JSONObject) jc.get("mlVariables"),(String) jc.get("modelPath"), mlServiceId);
				if(mlServiceContext!=null) {
					mlServiceContext.setMlID(mlID);
				}

				MLAPI.addReading(Collections.singletonList(energyMeterID),"EnergyPrediction", fields, module.getTableName(), module);

				scheduleJob(mlID, mlServiceContext);
				LOGGER.info("After updating energy model");
			}else{
				String errMsg = energyMeterID+" energy meter is not available";
				if(mlServiceContext!=null) {
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.RESOURCE_NOT_FOUND, errMsg);
				}
			}
			return false;
		}
		catch(Exception e)
		{
			String errMsg = "Error while adding Energy Prediction Job";
			e.printStackTrace();
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
				ScheduleInfo info = FormulaFieldAPI.getSchedule(FacilioFrequency.DAILY);
				MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
//			} else {
//				FacilioTimer.scheduleOneTimeJobWithTimestampInSec(mlID, "DefaultMLJob",(executionTime/1000), "ml");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Energy prediction default job creation failed");
		}
	}

	private long updateEnergyModel(EnergyMeterContext context, JSONObject mlModelVariables, JSONObject mlVariables, String modelPath, long mlServiceId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

//		FacilioModule logReadingModule = modBean.getModule("energypredictionmllogreadings");
		FacilioModule energyPredictionModule = modBean.getModule("energyprediction");

		long mlID = MLAPI.addMLModel(modelPath,context.getName(), -1,energyPredictionModule.getModuleId(), mlServiceId);

		Map<String,Long> maxSamplingPeriodMap = new HashMap<>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<>();
		Map<String,String> aggregationMap = new HashMap<>();

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

		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		//		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);

		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);

		MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(), maxSamplingPeriodMap.getOrDefault(energyField.getName(), 7776000000L),futureSamplingPeriodMap.getOrDefault(energyField.getName(), 0L),true,aggregationMap.getOrDefault(energyField.getName(), "SUM"));
		//		MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):7776000000l,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false,aggregationMap.containsKey(markedField.getName())? aggregationMap.get(markedField.getName()):"SUM");
		
		long oneYearSampling = 365 * DAYS_IN_MILLISECONDS; 
		long tempMaxSamplingPeriod = maxSamplingPeriodMap.getOrDefault(temperatureField.getName(), oneYearSampling);

		if(tempMaxSamplingPeriod < oneYearSampling) {
			tempMaxSamplingPeriod = oneYearSampling;
		}
		MLAPI.addMLVariables(mlID,
				temperatureField.getModuleId(),
				temperatureField.getFieldId(),
				temperatureParentField.getFieldId(),
				context.getSiteId(),
				tempMaxSamplingPeriod,
				futureSamplingPeriodMap.getOrDefault(temperatureField.getName(),172800000L),false,
				aggregationMap.getOrDefault(temperatureField.getName(),"SUM"));

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
