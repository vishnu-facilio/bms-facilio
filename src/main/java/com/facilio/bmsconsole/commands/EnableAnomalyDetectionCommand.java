package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.JSONArray;

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
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;


public class EnableAnomalyDetectionCommand implements Command
{

	@Override
	public boolean execute(Context context) throws Exception 
	{
		
		//Get All Energy Meters
		List<EnergyMeterContext>  emContextList = DeviceAPI.getAllEnergyMeters();
		
		buildGamModel(emContextList);
		
		System.out.println("testing "+emContextList.get(0).getCategory().getId());
		addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(0).getCategory().getId(),"AnomalyDetectionMLLogReadings",FieldFactory.getMLLogCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(0).getCategory().getId(),"AnomalyDetectionMLReadings",FieldFactory.getMLCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		long ratioCheckMLid = addRatioCheckModel(emContextList,(String)context.get("TreeHierachy"));
		checkGamModel(ratioCheckMLid,emContextList);
		
		return false;
	}
	
	private void checkGamModel(long ratioCheckMLID, List<EnergyMeterContext> emContextList) throws Exception
	{
		JSONArray mlIDList = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("AnomalyDetectionMLLogReadings");
		FacilioModule readingModule = modBean.getModule("AnomalyDetectionMLReadings");
		
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		for(EnergyMeterContext context:emContextList)
		{
			long mlID = addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000);
			addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000);
			addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),3600000);
			
			addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
			addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
			
			addMLModelVariables(mlID,"timezone","Asia/Dubai");
			addMLModelVariables(mlID,"dimension1","WEEKDAY");
			addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			addMLModelVariables(mlID,"tableValue","1.96");
			addMLModelVariables(mlID,"adjustmentPercentage","10");
			addMLModelVariables(mlID,"orderRange","2");
			addMLModelVariables(mlID,"meterInterval","10");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			
			mlIDList.put(mlID);
		}
		
		addMLModelVariables((long) mlIDList.get(mlIDList.length()-1),"jobid",""+ratioCheckMLID);
		updateSequenceForMLModel((long)mlIDList.get(0),mlIDList.toString());
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);
		
		List<LocalTime> hourlyList = new ArrayList<LocalTime>();
		hourlyList.add(LocalTime.MIDNIGHT);
		for(int i=1;i<24;i++)
		{
			hourlyList.add(LocalTime.MIDNIGHT.plusHours(i));
		}
		info.setTimeObjects(hourlyList);
		addJobs((long)mlIDList.get(0),info);
		
	}
	
	private void updateSequenceForMLModel(long mlID,String mlIDList) throws SQLException
	{
		FacilioModule module = ModuleFactory.getMLModule();
        List< FacilioField> updateFields = FieldFactory.getMLFields();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName()).andCustomWhere("ID = ?", mlID).fields(updateFields);

        Map<String,Object> prop = new HashMap<String,Object>(1);
        prop.put("sequence", mlIDList);
        updateBuilder.update(prop);
	}
	
	private long addRatioCheckModel(List<EnergyMeterContext> emContextList, String treeHierachy) throws Exception
	{
		addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(0).getCategory().getId(),"checkRatioMLLogReadings",FieldFactory.getMLLogCheckRatioFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(0).getCategory().getId(),"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("checkRatioMLLogReadings");
		FacilioModule readingModule = modBean.getModule("checkRatioMLReadings");
		
		FacilioModule checkGamReadingModule = modBean.getModule("AnomalyDetectionMLLogReadings");
		
		long mlID = addMLModel("ratioCheck",logReadingModule.getModuleId(),readingModule.getModuleId());
	
		FacilioField parentField = modBean.getField("parentId", checkGamReadingModule.getName());
		
		FacilioField actualValueField = modBean.getField("actualValue", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField adjustedLowerBoundField = modBean.getField("adjustedLowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField adjustedUpperBoundField = modBean.getField("adjustedUpperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField gamAnomalyField = modBean.getField("gamAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField lowerARMAField = modBean.getField("lowerARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		
		FacilioField lowerBoundField = modBean.getField("lowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField lowerGAMField = modBean.getField("lowerGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField predictedField = modBean.getField("predicted", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField predictedResidualFields = modBean.getField("predictedResidual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField residualField = modBean.getField("residual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField temperatureField = modBean.getField("temperature", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField lowerAnomalyField = modBean.getField("lowerAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField upperARMAField = modBean.getField("upperARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField upperAnomalyField = modBean.getField("upperAnomaly",checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField upperBoundField = modBean.getField("upperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		FacilioField upperGAMField = modBean.getField("upperGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000);
		}
		
		addMLModelVariables(mlID,"TreeHierachy",treeHierachy);
		
		return mlID;
	}
	
	private void buildGamModel(List<EnergyMeterContext> emContextList) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		for(EnergyMeterContext emContext : emContextList)
		{
			long mlID = addMLModel("buildGamModel",-1,-1);
			System.out.println("Module id are "+energyField.getModuleId());
			addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000);
			addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000);
			addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),emContext.getSiteId(),777600000);
			
			addMLAssetVariables(mlID,emContext.getId(),"TYPE","Energy Meter");
			addMLAssetVariables(mlID,emContext.getSiteId(),"TYPE","Site");
			
			addMLModelVariables(mlID,"timezone","Asia/Dubai");
			addMLModelVariables(mlID,"dimension1","WEEKDAY");
			addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			addMLModelVariables(mlID,"tableValue","1.96");
			addMLModelVariables(mlID,"percentile","5");
			addMLModelVariables(mlID,"adjustmentPercentage","10");
			addMLModelVariables(mlID,"orderRange","2");
			addMLModelVariables(mlID,"meterInterval","10");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			addJobs(mlID,info);
			
		}
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
	
	private void addJobs(long mlID,ScheduleInfo info) throws Exception
	{
		FacilioTimer.scheduleCalendarJob(mlID, "DefaultMLJob", System.currentTimeMillis(), info, "ml");
	}
	
	private void addMLVariables(long mlID,long moduleid,long fieldid,long parentFieldid,long parentid,long maxSamplingPeriod) throws Exception
	{
		MLVariableContext variableContext = new MLVariableContext();
		variableContext.setMlID(mlID);
		variableContext.setModuleID(moduleid);
		variableContext.setFieldID(fieldid);
		variableContext.setParentFieldID(parentFieldid);
		variableContext.setParentID(parentid);
		variableContext.setMaxSamplingPeriod(maxSamplingPeriod);
		
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

	
	
}
