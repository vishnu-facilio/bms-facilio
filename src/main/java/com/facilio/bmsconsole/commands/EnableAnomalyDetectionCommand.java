package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;


public class EnableAnomalyDetectionCommand extends FacilioCommand
{
	private static final Logger LOGGER= Logger.getLogger(EnableAnomalyDetectionCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		
		//Get All Energy Meters
		Hashtable<Long,EnergyMeterContext>  emContextList = new Hashtable<Long,EnergyMeterContext>(10);
		
		String[] energyMeterID = context.get("TreeHierarchy").toString().split(",");
		for(String ID:energyMeterID)
		{
			long id = Long.parseLong(ID);
			EnergyMeterContext emContext = DeviceAPI.getEnergyMeter(Long.parseLong(ID));
			emContextList.put(id,emContext);
		}
		
		buildGamModel(emContextList,(String) context.get("meterInterval"));
		
		Entry<Long, EnergyMeterContext> entry = emContextList.entrySet().iterator().next();
		LOGGER.info("testing "+emContextList.get(entry.getKey()).getCategory().getId());
		MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(entry.getKey()).getCategory().getId(),"AnomalyDetectionMLLogReadings",FieldFactory.getMLLogCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		MLAPI.addReading(FacilioConstants.ContextNames.ASSET_CATEGORY,emContextList.get(entry.getKey()).getCategory().getId(),"AnomalyDetectionMLReadings",FieldFactory.getMLCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
		long ratioCheckMLid = 0L;
		
		LOGGER.info("Additional Context "+context.containsKey("ratioHierarchy")+"::"+context.containsKey("meterInterval")+"::"+context.containsKey("TreeHierarchy"));
		for(Object key:context.keySet())
		{
			LOGGER.info("Key is "+(String)key);
		}
		if(context.containsKey("ratioHierarchy"))
		{
			JSONArray ratioHierachy = new JSONArray((String)context.get("ratioHierarchy"));
			LOGGER.info("Ratio Hierachy is "+ratioHierachy);
			ratioCheckMLid = addMultipleRatioCheckModel(emContextList,ratioHierachy);
		}
		else
		{
			ratioCheckMLid = addRatioCheckModel(emContextList,(String)context.get("TreeHierarchy"));
		}
		checkGamModel(ratioCheckMLid,emContextList,(String) context.get("meterInterval"));
		
		return false;
	}
	
	private void checkGamModel(long ratioCheckMLID, Hashtable<Long,EnergyMeterContext> emContextList,String meterInterval) throws Exception
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
		for(EnergyMeterContext context:emContextList.values())
		{
			long mlID = MLAPI.addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000,0,true);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),3600000,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),3600000,0,false);
			
			MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
			MLAPI.addMLModelVariables(mlID,"dimension1","WEEKDAY");
			MLAPI.addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			MLAPI.addMLModelVariables(mlID,"tableValue","1.96");
			MLAPI.addMLModelVariables(mlID,"adjustmentPercentage","10");
			MLAPI.addMLModelVariables(mlID,"orderRange","2");
			MLAPI.addMLModelVariables(mlID,"meterInterval",meterInterval);
			MLAPI.addMLModelVariables(mlID,"modelname","Gam");
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
	
	private long addMultipleRatioCheckModel(Hashtable<Long,EnergyMeterContext> emContextTable, JSONArray ratioHierachyList) throws Exception
	{
		Entry<Long, EnergyMeterContext> entry = emContextTable.entrySet().iterator().next();
		MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextTable.get(entry.getKey()).getCategory().getId(),"checkRatioMLLogReadings",FieldFactory.getMLLogCheckRatioFields(),ModuleFactory.getMLLogReadingModule().getTableName());
		MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextTable.get(entry.getKey()).getCategory().getId(),"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("checkRatioMLLogReadings");
		FacilioModule readingModule = modBean.getModule("checkRatioMLReadings");
		
		FacilioModule checkGamReadingModule = modBean.getModule("AnomalyDetectionMLLogReadings");
		
		
	
		FacilioField parentField = modBean.getField("parentId", checkGamReadingModule.getName());
		
		FacilioField actualValueField = modBean.getField("actualValue", checkGamReadingModule.getName());
		FacilioField adjustedLowerBoundField = modBean.getField("adjustedLowerBound", checkGamReadingModule.getName());
		FacilioField adjustedUpperBoundField = modBean.getField("adjustedUpperBound", checkGamReadingModule.getName());
		FacilioField gamAnomalyField = modBean.getField("gamAnomaly", checkGamReadingModule.getName());
		FacilioField lowerARMAField = modBean.getField("lowerARMA", checkGamReadingModule.getName());
		FacilioField lowerBoundField = modBean.getField("lowerBound", checkGamReadingModule.getName());
		FacilioField lowerGAMField = modBean.getField("lowerGAM", checkGamReadingModule.getName());
		FacilioField predictedField = modBean.getField("predicted", checkGamReadingModule.getName());
		FacilioField predictedResidualFields = modBean.getField("predictedResidual", checkGamReadingModule.getName());
		FacilioField residualField = modBean.getField("residual", checkGamReadingModule.getName());
		FacilioField temperatureField = modBean.getField("temperature", checkGamReadingModule.getName());
		FacilioField lowerAnomalyField = modBean.getField("lowerAnomaly", checkGamReadingModule.getName());
		FacilioField upperARMAField = modBean.getField("upperARMA", checkGamReadingModule.getName());
		FacilioField upperAnomalyField = modBean.getField("upperAnomaly",checkGamReadingModule.getName());
		FacilioField upperBoundField = modBean.getField("upperBound", checkGamReadingModule.getName());
		FacilioField upperGAMField = modBean.getField("upperGAM", checkGamReadingModule.getName());
		
		long mlID = MLAPI.addMLModel("ratioCheck",logReadingModule.getModuleId(),readingModule.getModuleId());
		List<Long> mlIDList = new ArrayList<Long>(10);
		mlIDList.add(mlID);
		
		long ratioHierachySize = ratioHierachyList.length(); 
		for(long i=1;i<ratioHierachySize;i++)
		{
			LOGGER.info("Adding ML Model for "+i);
			mlIDList.add(MLAPI.addMLModel("ratioCheck",logReadingModule.getModuleId(),readingModule.getModuleId()));
		}
		
		for(int i=0;i<ratioHierachyList.length();i++)
		{
			JSONArray emObject =(JSONArray) ratioHierachyList.get(i);
			long ml_id = mlIDList.get(i);
			LOGGER.info("EM Object "+emObject.toString()+"::"+ml_id);
			for(int j=0;j<emObject.length();j++)
			{
				Integer id =  (Integer) emObject.get(j);
				
				MLAPI.addMLVariables(ml_id,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
				MLAPI.addMLVariables(ml_id,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),3600000,0,false);
			}
			MLAPI.addMLModelVariables(ml_id,"TreeHierarchy",emObject.toString());
			FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			MLAPI.addMLModelVariables(ml_id,"energyfieldid",""+energyField.getId());
			
			MLAPI.addMLModelVariables(ml_id,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());	
		}
		updateSequenceForMLModel(mlID,mlIDList.toString());
		return mlID;
	}
	
	private long addRatioCheckModel(Hashtable<Long,EnergyMeterContext> emContextList, String TreeHierarchy) throws Exception
	{
		Map.Entry<Long,EnergyMeterContext> entry = emContextList.entrySet().iterator().next();
		MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(entry.getKey()).getCategory().getId(),"checkRatioMLLogReadings",FieldFactory.getMLLogCheckRatioFields(),ModuleFactory.getMLLogReadingModule().getTableName());
        MLAPI.addReading(FacilioConstants.ContextNames.ENERGY_METER,emContextList.get(entry.getKey()).getCategory().getId(),"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("checkRatioMLLogReadings");
		FacilioModule readingModule = modBean.getModule("checkRatioMLReadings");
		
		FacilioModule checkGamReadingModule = modBean.getModule("AnomalyDetectionMLLogReadings");
		
		long mlID = MLAPI.addMLModel("ratioCheck",logReadingModule.getModuleId(),readingModule.getModuleId());
	
		FacilioField parentField = modBean.getField("parentId", checkGamReadingModule.getName());
		
		FacilioField actualValueField = modBean.getField("actualValue", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField adjustedLowerBoundField = modBean.getField("adjustedLowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField adjustedUpperBoundField = modBean.getField("adjustedUpperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField gamAnomalyField = modBean.getField("gamAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerARMAField = modBean.getField("lowerARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		
		FacilioField lowerBoundField = modBean.getField("lowerBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerGAMField = modBean.getField("lowerGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField predictedField = modBean.getField("predicted", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField predictedResidualFields = modBean.getField("predictedResidual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField residualField = modBean.getField("residual", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField temperatureField = modBean.getField("temperature", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField lowerAnomalyField = modBean.getField("lowerAnomaly", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperARMAField = modBean.getField("upperARMA", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperAnomalyField = modBean.getField("upperAnomaly",checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperBoundField = modBean.getField("upperBound", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		FacilioField upperGAMField = modBean.getField("upperGAM", checkGamReadingModule.getName());
		for(EnergyMeterContext emContext : emContextList.values())
		{
			MLAPI.addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),3600000,0,false);
		}
		
		MLAPI.addMLModelVariables(mlID,"TreeHierarchy",TreeHierarchy);
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		MLAPI.addMLModelVariables(mlID,"energyfieldid",""+energyField.getId());
		
		MLAPI.addMLModelVariables(mlID,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());
		
		return mlID;
	}
	
	private void buildGamModel(Hashtable<Long,EnergyMeterContext> emContextList,String meterInterval) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField energyField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		FacilioField energyParentField = modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		for(EnergyMeterContext emContext : emContextList.values())
		{
			long mlID = MLAPI.addMLModel("buildGamModel",-1,-1);
			System.out.println("Module id are "+energyField.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000,0,true);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),777600000,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),emContext.getSiteId(),777600000,0,false);
			
			MLAPI.addMLAssetVariables(mlID,emContext.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,emContext.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
			MLAPI.addMLModelVariables(mlID,"dimension1","WEEKDAY");
			MLAPI.addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			MLAPI.addMLModelVariables(mlID,"tableValue","1.96");
			MLAPI.addMLModelVariables(mlID,"percentile","5");
			MLAPI.addMLModelVariables(mlID,"adjustmentPercentage","10");
			MLAPI.addMLModelVariables(mlID,"orderRange","2");
			MLAPI.addMLModelVariables(mlID,"meterInterval",meterInterval);
			MLAPI.addMLModelVariables(mlID,"modelname","Gam");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
			
		}
	}
	
}
