package com.facilio.bmsconsole.commands;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MLAssetVariableContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class AddEnergyPredictionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context jc) throws Exception {
		
		long energyMeterID=(long) jc.get("energyMeterID");
		
		EnergyMeterContext emContext2 = DeviceAPI.getEnergyMeter(energyMeterID);
		
		addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContext2.getCategory().getId(),"EnergyPredictionMLLogReadings",FieldFactory.getMLLogPredictCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContext2.getCategory().getId(),"EnergyPredictionMLReadings",FieldFactory.getMLPredictCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		
		checkGamModel(energyMeterID,emContext2,(String) jc.get("weekEnd"),(String) jc.get("meterInterval"), (String) jc.get("modelName"));
		return false;
	}
	
	private void checkGamModel(long ratioCheckMLID, EnergyMeterContext context,String weekend,String meterInterval,String modelName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("EnergyPredictionMLLogReadings");
		FacilioModule readingModule = modBean.getModule("EnergyPredictionMLReadings");
		
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
	
		long mlID = addMLModel("predictEnergyWithRQ",logReadingModule.getModuleId(),readingModule.getModuleId());
		addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),31536000000l,0,true);
		addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),31536000000l,0,false);
		addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),31536000000l,172800000l,false);
		
		addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
		addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
		
		addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		addMLModelVariables(mlID,"weekend",weekend);
		addMLModelVariables(mlID,"meterinterval",meterInterval);
		addMLModelVariables(mlID,"modelName",modelName);
		
		
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);

		addJobs(mlID,info);
		
	}

	private void addReading(String parentModule,long parentCategoryID, String readingName,List<FacilioField> fields,String tableName) throws Exception 
	{
         FacilioContext context = new FacilioContext();
         context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModule);
         context.put(FacilioConstants.ContextNames.READING_NAME,readingName);
         context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
         context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
         context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, parentCategoryID);
         context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, tableName);
         context.put(FacilioConstants.ContextNames.OVER_RIDE_READING_SPLIT, true);
         

         Chain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
         addReadingChain.execute(context);
	}
	
	private long addMLModel(String modelPath,long logReadingModuleID,long readingModuleID) throws Exception
	{
		MLContext mlContext = new MLContext();
		mlContext.setModelPath(modelPath);
		if(logReadingModuleID!=-1)
		{
			mlContext.setPredictionLogModuleID(logReadingModuleID);
		}
		if(readingModuleID!=-1)
		{
			mlContext.setPredictionModuleID(readingModuleID);
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                									.table(ModuleFactory.getMLModule().getTableName())
                									.fields(FieldFactory.getMLFields());
		return insertBuilder.insert(FieldUtil.getAsProperties(mlContext));
	}
	
	private void addMLModelVariables(long mlId,String Key,String value) throws Exception
	{
		MLModelVariableContext context = new MLModelVariableContext();
		context.setMlID(mlId);
		context.setVariableKey(Key);
		context.setVariableValue(value);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table(ModuleFactory.getMLModelVariablesModule().getTableName())
												.fields(FieldFactory.getMLModelVariablesFields());

		builder.insert(FieldUtil.getAsProperties(context));
	}
	
	private void addMLVariables(long mlID,long moduleid,long fieldid,long parentFieldid,long parentid,long maxSamplingPeriod,long futureSamplingPeriod,boolean isSource) throws Exception
	{
		MLVariableContext variableContext = new MLVariableContext();
		variableContext.setMlID(mlID);
		variableContext.setModuleID(moduleid);
		variableContext.setFieldID(fieldid);
		variableContext.setParentFieldID(parentFieldid);
		variableContext.setParentID(parentid);
		variableContext.setMaxSamplingPeriod(maxSamplingPeriod);
		variableContext.setFutureSamplingPeriod(futureSamplingPeriod);
		variableContext.setIsSource(isSource);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
											.table(ModuleFactory.getMLVariablesModule().getTableName())
											.fields(FieldFactory.getMLVariablesFields());

		builder.insert(FieldUtil.getAsProperties(variableContext));
	}
	
	private void addMLAssetVariables(long mlId,long parentid,String key,String value) throws Exception
	{
		MLAssetVariableContext context = new MLAssetVariableContext();
		context.setMlId(mlId);
		context.setAssetID(parentid);
		context.setVariableKey(key);
		context.setVariableValue(value);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table(ModuleFactory.getMLAssetVariablesModule().getTableName())
												.fields(FieldFactory.getMLAssetVariablesFields());
		builder.insert(FieldUtil.getAsProperties(context));
	}
	
	
	
	private void addJobs(long mlID,ScheduleInfo info) throws Exception
	{
		FacilioTimer.scheduleCalendarJob(mlID, "DefaultMLJob", System.currentTimeMillis(), info, "ml");
	}

	
}
