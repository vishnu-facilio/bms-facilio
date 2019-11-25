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
		
		buildGamModel(assetContextList,(JSONObject)((JSONObject)context.get("mlVariables")).get("buildGam"),(JSONObject)context.get("mlModelVariables"),energyField,markedField,energyParentField);
				
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
			ratioCheckMLid = addMultipleRatioCheckModel(categoryId,assetContextList,ratioHierachy,moduleNames,(JSONObject)((JSONObject)context.get("mlVariables")).get("ratioCheck"),energyField.getId());
		}
		else
		{
			ratioCheckMLid = addRatioCheckModel(categoryId,assetContextList,(String)context.get("TreeHierarchy"),moduleNames,(JSONObject)((JSONObject)context.get("mlVariables")).get("ratioCheck"),energyField.getId());
		}
		checkGamModel(ratioCheckMLid,assetContextList,(JSONObject)((JSONObject)context.get("mlVariables")).get("checkGam"),(JSONObject)context.get("mlModelVariables"),energyField,markedField,energyParentField);
		
		return false;
	}
	
	private void checkGamModel(long ratioCheckMLID, LinkedList<AssetContext> assetContextList,JSONObject mlVariables,JSONObject mlModelVariables,FacilioField energyField,FacilioField markedField,FacilioField energyParentField) throws Exception
	{
		JSONArray mlIDList = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		FacilioModule readingModule = modBean.getModule("anomalydetectionmlreadings");
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		if (mlVariables != null) {
			for(Object entry:mlVariables.entrySet()){
				Map.Entry en = (Map.Entry) entry;
				if(((JSONObject)en.getValue()).containsKey("maxSamplingPeriod")){
					maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("maxSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("futureSamplingPeriod")){
					futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("futureSamplingPeriod").toString()));
				}
			}
		}
		
		for(AssetContext context:assetContextList)
		{
			long mlID = MLAPI.addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false);
			
			MLAPI.addMLAssetVariables(mlID,context.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,context.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
			
			for(Object entry:mlModelVariables.entrySet()){
				Map.Entry<String, String> en = (Map.Entry) entry;
				MLAPI.addMLModelVariables(mlID, en.getKey(), en.getValue());
			}
			
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
	
	private long addMultipleRatioCheckModel(Long categoryId,LinkedList<AssetContext> assetContextList, JSONArray ratioHierachyList,Set<String> moduleNames,JSONObject mlVariables,long energyfieldid) throws Exception
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
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		if (mlVariables != null) {
			for(Object entry:mlVariables.entrySet()){
				Map.Entry en = (Map.Entry) entry;
				if(((JSONObject)en.getValue()).containsKey("maxSamplingPeriod")){
					maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("maxSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("futureSamplingPeriod")){
					futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("futureSamplingPeriod").toString()));
				}
			}
		}
		
		for(int i=0;i<ratioHierachyList.length();i++)
		{
			JSONArray emObject =(JSONArray) ratioHierachyList.get(i);
			long ml_id = mlIDList.get(i);
			LOGGER.info("EM Object "+emObject.toString()+"::"+ml_id);
			for(int j=0;j<emObject.length();j++)
			{
				Integer id =  (Integer) emObject.get(j);
				
				MLAPI.addMLVariables(ml_id,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(actualValueField.getName())? maxSamplingPeriodMap.get(actualValueField.getName()): 4200000,futureSamplingPeriodMap.containsKey(actualValueField.getName())? futureSamplingPeriodMap.get(actualValueField.getName()):0,j==0);
				MLAPI.addMLVariables(ml_id,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? maxSamplingPeriodMap.get(adjustedLowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? futureSamplingPeriodMap.get(adjustedLowerBoundField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? maxSamplingPeriodMap.get(adjustedUpperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? futureSamplingPeriodMap.get(adjustedUpperBoundField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(gamAnomalyField.getName())? maxSamplingPeriodMap.get(gamAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(gamAnomalyField.getName())? futureSamplingPeriodMap.get(gamAnomalyField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerARMAField.getName())? maxSamplingPeriodMap.get(lowerARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerARMAField.getName())? futureSamplingPeriodMap.get(lowerARMAField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerBoundField.getName())? maxSamplingPeriodMap.get(lowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerBoundField.getName())? futureSamplingPeriodMap.get(lowerBoundField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerGAMField.getName())? maxSamplingPeriodMap.get(lowerGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerGAMField.getName())? futureSamplingPeriodMap.get(lowerGAMField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(predictedField.getName())? maxSamplingPeriodMap.get(predictedField.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedField.getName())? futureSamplingPeriodMap.get(predictedField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(predictedResidualFields.getName())? maxSamplingPeriodMap.get(predictedResidualFields.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedResidualFields.getName())? futureSamplingPeriodMap.get(predictedResidualFields.getName()):0,false);
				MLAPI.addMLVariables(ml_id,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(residualField.getName())? maxSamplingPeriodMap.get(residualField.getName()): 4200000,futureSamplingPeriodMap.containsKey(residualField.getName())? futureSamplingPeriodMap.get(residualField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()): 4200000,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? maxSamplingPeriodMap.get(lowerAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? futureSamplingPeriodMap.get(lowerAnomalyField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperARMAField.getName())? maxSamplingPeriodMap.get(upperARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperARMAField.getName())? futureSamplingPeriodMap.get(upperARMAField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperAnomalyField.getName())? maxSamplingPeriodMap.get(upperAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperAnomalyField.getName())? futureSamplingPeriodMap.get(upperAnomalyField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperBoundField.getName())? maxSamplingPeriodMap.get(upperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperBoundField.getName())? futureSamplingPeriodMap.get(upperBoundField.getName()):0,false);
				MLAPI.addMLVariables(ml_id,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperGAMField.getName())? maxSamplingPeriodMap.get(upperGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperGAMField.getName())? futureSamplingPeriodMap.get(upperGAMField.getName()):0,false);
			}
			MLAPI.addMLModelVariables(ml_id,"TreeHierarchy",emObject.toString());
			MLAPI.addMLModelVariables(ml_id,"energyfieldid",""+energyfieldid);
			MLAPI.addMLModelVariables(ml_id,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());	
		}
		updateSequenceForMLModel(mlID,mlIDList.toString());
		return mlID;
	}
	
	private long addRatioCheckModel(Long categoryId,LinkedList<AssetContext> assetContextList, String TreeHierarchy,Set<String> moduleNames,JSONObject mlVariables,long energyfieldid) throws Exception
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
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		if (mlVariables != null) {
			for(Object entry:mlVariables.entrySet()){
				Map.Entry en = (Map.Entry) entry;
				if(((JSONObject)en.getValue()).containsKey("maxSamplingPeriod")){
					maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("maxSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("futureSamplingPeriod")){
					futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("futureSamplingPeriod").toString()));
				}
			}
		}
		
		
		for(AssetContext emContext : assetContextList)
		{
			MLAPI.addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(actualValueField.getName())? maxSamplingPeriodMap.get(actualValueField.getName()): 4200000,futureSamplingPeriodMap.containsKey(actualValueField.getName())? futureSamplingPeriodMap.get(actualValueField.getName()):0,(emContext.getId()==Long.parseLong(TreeHierarchy.split(",")[0])));
			MLAPI.addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? maxSamplingPeriodMap.get(adjustedLowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? futureSamplingPeriodMap.get(adjustedLowerBoundField.getName()):0,false);
			MLAPI.addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? maxSamplingPeriodMap.get(adjustedUpperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? futureSamplingPeriodMap.get(adjustedUpperBoundField.getName()):0,false);
			MLAPI.addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(gamAnomalyField.getName())? maxSamplingPeriodMap.get(gamAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(gamAnomalyField.getName())? futureSamplingPeriodMap.get(gamAnomalyField.getName()):0,false);
			MLAPI.addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(lowerARMAField.getName())? maxSamplingPeriodMap.get(lowerARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerARMAField.getName())? futureSamplingPeriodMap.get(lowerARMAField.getName()):0,false);
			MLAPI.addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(lowerBoundField.getName())? maxSamplingPeriodMap.get(lowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerBoundField.getName())? futureSamplingPeriodMap.get(lowerBoundField.getName()):0,false);
			MLAPI.addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(lowerGAMField.getName())? maxSamplingPeriodMap.get(lowerGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerGAMField.getName())? futureSamplingPeriodMap.get(lowerGAMField.getName()):0,false);
			MLAPI.addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(predictedField.getName())? maxSamplingPeriodMap.get(predictedField.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedField.getName())? futureSamplingPeriodMap.get(predictedField.getName()):0,false);
			MLAPI.addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(predictedResidualFields.getName())? maxSamplingPeriodMap.get(predictedResidualFields.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedResidualFields.getName())? futureSamplingPeriodMap.get(predictedResidualFields.getName()):0,false);
			MLAPI.addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(residualField.getName())? maxSamplingPeriodMap.get(residualField.getName()): 4200000,futureSamplingPeriodMap.containsKey(residualField.getName())? futureSamplingPeriodMap.get(residualField.getName()):0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()): 4200000,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false);
			MLAPI.addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? maxSamplingPeriodMap.get(lowerAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? futureSamplingPeriodMap.get(lowerAnomalyField.getName()):0,false);
			MLAPI.addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(upperARMAField.getName())? maxSamplingPeriodMap.get(upperARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperARMAField.getName())? futureSamplingPeriodMap.get(upperARMAField.getName()):0,false);
			MLAPI.addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(upperAnomalyField.getName())? maxSamplingPeriodMap.get(upperAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperAnomalyField.getName())? futureSamplingPeriodMap.get(upperAnomalyField.getName()):0,false);
			MLAPI.addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(upperBoundField.getName())? maxSamplingPeriodMap.get(upperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperBoundField.getName())? futureSamplingPeriodMap.get(upperBoundField.getName()):0,false);
			MLAPI.addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(upperGAMField.getName())? maxSamplingPeriodMap.get(upperGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperGAMField.getName())? futureSamplingPeriodMap.get(upperGAMField.getName()):0,false);
		}
		
		MLAPI.addMLModelVariables(mlID,"TreeHierarchy",TreeHierarchy);
		MLAPI.addMLModelVariables(mlID,"energyfieldid",""+energyfieldid);
		MLAPI.addMLModelVariables(mlID,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());
		
		return mlID;
	}
	
	private void buildGamModel(List<AssetContext> assetContextList,JSONObject mlVariables,JSONObject mlModelVariables,FacilioField energyField,FacilioField markedField,FacilioField energyParentField) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
		Map<String,Long> maxSamplingPeriodMap = new HashMap<String, Long>();
		Map<String,Long> futureSamplingPeriodMap = new HashMap<String, Long>();
		if (mlVariables != null) {
			for(Object entry:mlVariables.entrySet()){
				Map.Entry en = (Map.Entry) entry;
				if(((JSONObject)en.getValue()).containsKey("maxSamplingPeriod")){
					maxSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("maxSamplingPeriod").toString()));
				}
				if(((JSONObject)en.getValue()).containsKey("futureSamplingPeriod")){
					futureSamplingPeriodMap.put(en.getKey().toString(), Long.parseLong(((JSONObject)en.getValue()).get("futureSamplingPeriod").toString()));
				}
			}
		}
		
		for(AssetContext emContext : assetContextList)
		{
			long mlID = MLAPI.addMLModel("buildGamModel",-1,-1);
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):777600000,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true);
			MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):777600000,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false);
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),emContext.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):777600000,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false);
			
			MLAPI.addMLAssetVariables(mlID,emContext.getId(),"TYPE","Energy Meter");
			MLAPI.addMLAssetVariables(mlID,emContext.getSiteId(),"TYPE","Site");
			
			MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
			
			for(Object entry:mlModelVariables.entrySet()){
				Map.Entry<String, String> en = (Map.Entry) entry;
				MLAPI.addMLModelVariables(mlID, en.getKey(), en.getValue());
			}
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
			
		}
	}
	
}
