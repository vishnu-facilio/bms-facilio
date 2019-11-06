package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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
				LOGGER.info("Checking for null 1:"+FacilioConstants.ContextNames.ASSET_CATEGORY);
				LOGGER.info("Checking for null 1:"+emContext2.getCategory().getId());
				LOGGER.info("Checking for null 1:"+FieldFactory.getMLLogPredictCheckGamFields());
				LOGGER.info("Checking for null 1:"+ModuleFactory.getMLLogReadingModule().getTableName());
				MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContext2.getCategory().getId(),"EnergyPredictionMLLogReadings",FieldFactory.getMLLogPredictCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName());
				MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContext2.getCategory().getId(),"EnergyPredictionMLReadings",FieldFactory.getMLPredictCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
				LOGGER.info("After adding Reading");
				
				checkGamModel(energyMeterID,emContext2,(String) jc.get("weekEnd"),(String) jc.get("meterInterval"), (String) jc.get("modelName"),(String) jc.get("modelPath"));
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
	
	private void checkGamModel(long ratioCheckMLID, EnergyMeterContext context,String weekend,String meterInterval,String modelName,String modelPath) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("EnergyPredictionMLLogReadings");
		FacilioModule readingModule = modBean.getModule("EnergyPredictionMLReadings");
		
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
	
		long mlID = MLAPI.addMLModel(modelPath,logReadingModule.getModuleId(),readingModule.getModuleId());
		MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),31536000000l,0,true);
		MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),31536000000l,0,false);
		MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),31536000000l,172800000l,false);
		
		MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
		MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
		
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		MLAPI.addMLModelVariables(mlID,"weekend",weekend);
		MLAPI.addMLModelVariables(mlID,"meterinterval",meterInterval);
		MLAPI.addMLModelVariables(mlID,"modelName",modelName);
		
		
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);

		MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
		
	}


	
}
