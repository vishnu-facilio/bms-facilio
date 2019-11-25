package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
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
		
		//Get All Assets
		LinkedList<AssetContext> assetContextList = new LinkedList<>();
		String[] assetID = context.get("TreeHierarchy").toString().split(",");
		
		AssetContext assetContext = AssetsAPI.getAssetInfo(Long.parseLong(assetID[0]));
		assetContextList.add(assetContext);
		
		List<Long> assetIDList = Stream.of(assetID).filter(i->i != assetID[0]).map(Long::valueOf).collect(Collectors.toList());
		assetContextList.addAll(AssetsAPI.getAssetInfo(assetIDList));

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		JSONObject fieldObj = ((JSONObject)context.get("energyDeltaField"));
		FacilioField energyField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		fieldObj = ((JSONObject)context.get("markedField"));
		FacilioField markedField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		fieldObj = ((JSONObject)context.get("parentIdField"));
		FacilioField energyParentField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		
		buildGamModel(assetContextList,(String) context.get("meterInterval"),energyField,markedField,energyParentField);
				
		Long categoryId=assetContextList.get(0).getCategory().getId();
		AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
		long assetModuleID = assetCategory.getAssetModuleID();
		List<FacilioModule> modules = modBean.getAllSubModules(assetModuleID);
		Set<String> moduleNames = new HashSet<>();
		if(modules !=null){
			moduleNames = modules.stream().filter(m-> m != null && m.getName() != null).map(FacilioModule::getName).collect(Collectors.toSet());
		}
		
		if(!moduleNames.contains("anomalydetectionmllogreadings")){
			MLAPI.addReading(categoryId,"AnomalyDetectionMLLogReadings",FieldFactory.getMLLogCheckGamFields(),ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING);
		}
		if(!moduleNames.contains("anomalydetectionmlreadings")){
			MLAPI.addReading(categoryId,"AnomalyDetectionMLReadings",FieldFactory.getMLCheckGamFields(),ModuleFactory.getMLReadingModule().getTableName());
		}
		
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
			ratioCheckMLid = addMultipleRatioCheckModel(categoryId,assetContextList,ratioHierachy,moduleNames,energyField.getId());
		}
		else
		{
			ratioCheckMLid = addRatioCheckModel(categoryId,assetContextList,(String)context.get("TreeHierarchy"),moduleNames,energyField.getId());
		}
		checkGamModel(ratioCheckMLid,assetContextList,(String) context.get("meterInterval"),energyField,markedField,energyParentField);
		
		return false;
	}
	
	private void checkGamModel(long ratioCheckMLID, LinkedList<AssetContext> assetContextList,String meterInterval,FacilioField energyField,FacilioField markedField,FacilioField energyParentField) throws Exception
	{
		JSONArray mlIDList = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		FacilioModule readingModule = modBean.getModule("anomalydetectionmlreadings");
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		for(AssetContext context:assetContextList)
		{
			long mlID = MLAPI.addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),1209600000l,0,true);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),1209600000l,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),1209600000l,0,false);
			
			MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
			MLAPI.addMLModelVariables(mlID,"dimension1","WEEKDAY");
			MLAPI.addMLModelVariables(mlID,"dimension1Value","[1,2,3,4,5],[6,7]");
			MLAPI.addMLModelVariables(mlID,"tableValue","1.96");
			MLAPI.addMLModelVariables(mlID,"adjustmentPercentage","10");
			MLAPI.addMLModelVariables(mlID,"orderRange","2");
			MLAPI.addMLModelVariables(mlID,"meterInterval",meterInterval);
			MLAPI.addMLModelVariables(mlID,"modelname","catboost");
			MLAPI.addMLModelVariables(mlID,"ml_custom_configs","{\"inputmetrics\" : [\"LOCAL_TTIME\",\"totalEnergyConsumptionDelta\",\"HOUR\", \"prevWeekMedianEnergy\",\"currentWeekMedianEnergy\",\"prevWeekEnergy\",\"prevDayEnergy\"],\"outputmetrics\" : \"totalEnergyConsumptionDelta\" ,\"optionalVariables\" : [\"marked\" , \"temperature\"] ,\"additionalVariables\": [] ,\"Aggregator\" : {\"totalEnergyConsumptionDelta\": \"sum\" ,\"marked\":\"sum\" , \"runStatus\":\"sum\"}}");
			MLAPI.addMLModelVariables(mlID,"percentile","5");
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
		try {
			MLAPI.addJobs((long) mlIDList.get(0), "DefaultMLJob", info, "ml");
		} catch (InterruptedException e) {
			Thread.sleep(1000);
		}
		LOGGER.info("Check Gam Module adding Completed");
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
	
	private long addMultipleRatioCheckModel(Long categoryId,LinkedList<AssetContext> assetContextList, JSONArray ratioHierachyList,Set<String> moduleNames,long energyfieldid) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> mLLogCheckRatioFields = FieldFactory.getMLLogCheckRatioFields();
		int decimalColumnHigherValue = mLLogCheckRatioFields.stream().map(FacilioField::getColumnName).filter(f->f.startsWith("DECIMAL_CF")).map(c->c.replaceFirst("DECIMAL_CF", "")).map(Integer::parseInt).mapToInt(i -> i).max().orElse(0);
		for(AssetContext asset:assetContextList)
		{
			Long id =  asset.getId();
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_ratio","ratioLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_upperAnomaly","upperAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_lowerAnomaly","lowerAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));				
		}
		
		long checkRatioLogReadingNewModuleId = MLAPI.addReadingEveryTime(categoryId,"checkRatioMLLogReadings",mLLogCheckRatioFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING);
		if(!moduleNames.contains("checkratiomlreadings")){
			MLAPI.addReading(categoryId,"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		}
		
		FacilioModule readingModule = modBean.getModule("checkratiomlreadings");
		
		FacilioModule checkGamReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		
		
	
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
		
		long mlID = MLAPI.addMLModel("ratioCheck",checkRatioLogReadingNewModuleId,readingModule.getModuleId());
		List<Long> mlIDList = new ArrayList<Long>(10);
		mlIDList.add(mlID);
		
		long ratioHierachySize = ratioHierachyList.length(); 
		for(long i=1;i<ratioHierachySize;i++)
		{
			LOGGER.info("Adding ML Model for "+i);
			mlIDList.add(MLAPI.addMLModel("ratioCheck",checkRatioLogReadingNewModuleId,readingModule.getModuleId()));
		}
		
		for(int i=0;i<ratioHierachyList.length();i++)
		{
			JSONArray emObject =(JSONArray) ratioHierachyList.get(i);
			long ml_id = mlIDList.get(i);
			LOGGER.info("EM Object "+emObject.toString()+"::"+ml_id);
			for(int j=0;j<emObject.length();j++)
			{
				Integer id =  (Integer) emObject.get(j);
				
				MLAPI.addMLVariables(ml_id,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,j==0);
				MLAPI.addMLVariables(ml_id,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
				MLAPI.addMLVariables(ml_id,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),4200000,0,false);
			}
			MLAPI.addMLModelVariables(ml_id,"TreeHierarchy",emObject.toString());
			MLAPI.addMLModelVariables(ml_id,"energyfieldid",""+energyfieldid);
			
			MLAPI.addMLModelVariables(ml_id,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());	
		}
		updateSequenceForMLModel(mlID,mlIDList.toString());
		return mlID;
	}
	
	private long addRatioCheckModel(Long categoryId,LinkedList<AssetContext> assetContextList, String TreeHierarchy,Set<String> moduleNames,long energyfieldid) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> mLLogCheckRatioFields = FieldFactory.getMLLogCheckRatioFields();
		int decimalColumnHigherValue = mLLogCheckRatioFields.stream().map(FacilioField::getColumnName).filter(f->f.startsWith("DECIMAL_CF")).map(c->c.replaceFirst("DECIMAL_CF", "")).map(Integer::parseInt).mapToInt(i -> i).max().orElse(0);
		for(AssetContext emContext : assetContextList)
		{
			long id =  emContext.getId();
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_ratio","ratioLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_upperAnomaly","upperAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_lowerAnomaly","lowerAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));				
		}
		Long checkRatioLogReadingNewModuleId = MLAPI.addReadingEveryTime(categoryId,"checkRatioMLLogReadings",mLLogCheckRatioFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING);
		
		if(!moduleNames.contains("checkratiomlreadings")){
			MLAPI.addReading(categoryId,"checkRatioMLReadings",FieldFactory.getMLCheckRatioFields(),ModuleFactory.getMLReadingModule().getTableName());
		}
		
		FacilioModule readingModule = modBean.getModule("checkratiomlreadings");
		long mlID = MLAPI.addMLModel("ratioCheck",checkRatioLogReadingNewModuleId,readingModule.getModuleId());

		FacilioModule checkGamReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		
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
		for(AssetContext emContext : assetContextList)
		{
			MLAPI.addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,(emContext.getId()==Long.parseLong(TreeHierarchy.split(",")[0])));
			MLAPI.addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
			MLAPI.addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),4200000,0,false);
		}
		
		MLAPI.addMLModelVariables(mlID,"TreeHierarchy",TreeHierarchy);
		MLAPI.addMLModelVariables(mlID,"energyfieldid",""+energyfieldid);
		
		MLAPI.addMLModelVariables(mlID,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());
		
		return mlID;
	}
	
	private void buildGamModel(List<AssetContext> assetContextList,String meterInterval,FacilioField energyField,FacilioField markedField,FacilioField energyParentField) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		for(AssetContext emContext : assetContextList)
		{
			long mlID = MLAPI.addMLModel("buildGamModel",-1,-1);
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
			MLAPI.addMLModelVariables(mlID,"modelname","catboost");
			MLAPI.addMLModelVariables(mlID,"ml_custom_configs","{\"inputmetrics\" : [\"LOCAL_TTIME\",\"totalEnergyConsumptionDelta\",\"HOUR\", \"prevWeekMedianEnergy\",\"currentWeekMedianEnergy\",\"prevWeekEnergy\",\"prevDayEnergy\"],\"outputmetrics\" : \"totalEnergyConsumptionDelta\" ,\"optionalVariables\" : [\"marked\" , \"temperature\"] ,\"additionalVariables\": [] ,\"Aggregator\" : {\"totalEnergyConsumptionDelta\": \"sum\" ,\"marked\":\"sum\" , \"runStatus\":\"sum\"}}");
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
			
		}
	}
	
}
