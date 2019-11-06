package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MLAssetVariableContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.chain.FacilioChain;
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
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AddAnomalyJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception 
	{
		List<EnergyMeterContext> emContextList = new ArrayList<EnergyMeterContext>(10);
		EnergyMeterContext emContext = DeviceAPI.getEnergyMeter(5674);
		emContextList.add(emContext);
		EnergyMeterContext emContext1 = DeviceAPI.getEnergyMeter(5645);
		emContextList.add(emContext1);
		EnergyMeterContext emContext2 = DeviceAPI.getEnergyMeter(5646);
		emContextList.add(emContext2);
		EnergyMeterContext emContext3 = DeviceAPI.getEnergyMeter(5647);
		emContextList.add(emContext3);
		
		buildGamModel(emContextList);
		
		MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(0).getCategory().getId(),"AnomalyDetectionMLLogReadings",FieldFactory.getMLLogCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(0).getCategory().getId(),"AnomalyDetectionMLReadings",FieldFactory.getMLCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		long ratioCheckMLid = addRatioCheckModel(emContextList,"5674,5645,5646,5647");
		checkGamModel(ratioCheckMLid,emContextList);

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
			long mlID = MLAPI.addMLModel("buildGamModel",-1,-1);
			System.out.println("Module id are "+energyField.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000,0,false);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),emContext.getSiteId(),777600000,0,false);
			
			MLAPI.addMLAssetVariables(mlID,emContext.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,emContext.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone","Asia/Muscat");
			MLAPI.addMLModelVariables(mlID,"dimension1","WEEKDAY");
			MLAPI.addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			MLAPI.addMLModelVariables(mlID,"tableValue","1.96");
			MLAPI.addMLModelVariables(mlID,"percentile","5");
			MLAPI.addMLModelVariables(mlID,"adjustmentPercentage","10");
			MLAPI.addMLModelVariables(mlID,"orderRange","2");
			MLAPI.addMLModelVariables(mlID,"meterInterval","10");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
			
		}
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
			long mlID = MLAPI.addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000,0,false);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),3600000,0,false);
			
			MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone","Asia/Muscat");
			MLAPI.addMLModelVariables(mlID,"dimension1","WEEKDAY");
			MLAPI.addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,7],[5,6]");
			MLAPI.addMLModelVariables(mlID,"tableValue","1.96");
			MLAPI.addMLModelVariables(mlID,"adjustmentPercentage","10");
			MLAPI.addMLModelVariables(mlID,"orderRange","2");
			MLAPI.addMLModelVariables(mlID,"meterInterval","10");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			
			mlIDList.put(mlID);
		}
		
		MLAPI.addMLModelVariables((long) mlIDList.get(mlIDList.length()-1),"jobid",""+ratioCheckMLID);
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
		MLAPI.addJobs((long)mlIDList.get(0),"DefaultMLJob",info,"ml");
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
		MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(0).getCategory().getId(),"checkRatioMLLogReadings",FieldFactory.getMLLogCheckRatioFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(0).getCategory().getId(),"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("checkRatioMLLogReadings");
		FacilioModule readingModule = modBean.getModule("checkRatioMLReadings");
		
		FacilioModule checkGamReadingModule = modBean.getModule("AnomalyDetectionMLLogReadings");
		
		long mlID = MLAPI.addMLModel("ratioCheck",logReadingModule.getModuleId(),readingModule.getModuleId());
	
		FacilioField parentField = modBean.getField("parentId", checkGamReadingModule.getName());
		
		FacilioField actualValueField = modBean.getField("actualValue", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField adjustedLowerBoundField = modBean.getField("adjustedLowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField adjustedUpperBoundField = modBean.getField("adjustedUpperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField gamAnomalyField = modBean.getField("gamAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerARMAField = modBean.getField("lowerARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		
		FacilioField lowerBoundField = modBean.getField("lowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerGAMField = modBean.getField("lowerGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField predictedField = modBean.getField("predicted", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField predictedResidualFields = modBean.getField("predictedResidual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField residualField = modBean.getField("residual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField temperatureField = modBean.getField("temperature", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerAnomalyField = modBean.getField("lowerAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperARMAField = modBean.getField("upperARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperAnomalyField = modBean.getField("upperAnomaly",checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperBoundField = modBean.getField("upperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperGAMField = modBean.getField("upperGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList)
		{
			MLAPI.addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		MLAPI.addMLModelVariables(mlID,"TreeHierachy",treeHierachy);
		
		return mlID;
	}
}
