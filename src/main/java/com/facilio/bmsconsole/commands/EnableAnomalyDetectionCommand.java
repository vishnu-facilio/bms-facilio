package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;


public class EnableAnomalyDetectionCommand extends FacilioCommand
{
	private static final Logger LOGGER= Logger.getLogger(EnableAnomalyDetectionCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		LOGGER.info("Inside EnableAnomalyDetectionCommand");
		
		LinkedList<AssetContext> assetContextList = new LinkedList<>();
		String[] assetID = context.get("TreeHierarchy").toString().split(",");
		
		List<Long> assetIDList = Stream.of(assetID).map(Long::valueOf).collect(Collectors.toList());
		assetContextList.addAll(AssetsAPI.getAssetInfo(assetIDList));

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		JSONObject fieldObj = ((JSONObject)context.get("energyDeltaField"));
		FacilioField energyField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		FacilioField markedField = null;
		if(context.containsKey("markedField")){
			fieldObj = ((JSONObject)context.get("markedField"));
			markedField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		}
		fieldObj = ((JSONObject)context.get("parentIdField"));
		FacilioField energyParentField = modBean.getField(fieldObj.get("fieldName").toString(), fieldObj.get("moduleName").toString());
		
		JSONObject buildGamMlVariable  = context.containsKey("mlVariables") ? ((JSONObject)context.get("mlVariables")).containsKey("buildGam") ?(JSONObject)((JSONObject)context.get("mlVariables")).get("buildGam") :null  : null; 
		
		buildGamModel(assetContextList,buildGamMlVariable,(JSONObject)context.get("mlModelVariables"),energyField,markedField,energyParentField);
		
		FacilioModule module = modBean.getModule("anomalydetectionmllogreadings");
		List<FacilioField> fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLLogCheckGamFields();
		MLAPI.addReading(assetIDList,"AnomalyDetectionMLLogReadings",fields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING,module);
		
		module = modBean.getModule("anomalydetectionmlreadings");
		fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLCheckGamFields();
		MLAPI.addReading(assetIDList,"AnomalyDetectionMLReadings",fields,ModuleFactory.getMLReadingModule().getTableName(),module);

        Long ratioCheckMLid = null;
        if(context.get("parentHierarchy").toString().equalsIgnoreCase("true")){
	
			JSONObject ratiocheckMlVariable  = context.containsKey("mlVariables") ? ((JSONObject)context.get("mlVariables")).containsKey("ratioCheck") ?(JSONObject)((JSONObject)context.get("mlVariables")).get("ratioCheck") :null  : null; 
			
			if(context.containsKey("ratioHierarchy"))
			{
				JSONArray ratioHierachy = new JSONArray((String)context.get("ratioHierarchy"));
				ratioCheckMLid = addMultipleRatioCheckModel(assetIDList,ratioHierachy,ratiocheckMlVariable,energyField.getId());
			}
			else
			{
				ratioCheckMLid = addRatioCheckModel(assetIDList,(String)context.get("TreeHierarchy"),ratiocheckMlVariable,energyField.getId());
			}
		
		}
		JSONObject checkGamMlVariable  = context.containsKey("mlVariables") ? ((JSONObject)context.get("mlVariables")).containsKey("checkGam") ?(JSONObject)((JSONObject)context.get("mlVariables")).get("checkGam") :null  : null; 
		
		checkGamModel(ratioCheckMLid,assetContextList,checkGamMlVariable,(JSONObject)context.get("mlModelVariables"),energyField,markedField,energyParentField);
		
		LOGGER.info("Finished EnableAnomalyDetectionCommand");
		
		return false;
	}
	
	private void checkGamModel(Long ratioCheckMLID, LinkedList<AssetContext> assetContextList,JSONObject mlVariables,JSONObject mlModelVariables,FacilioField energyField,FacilioField markedField,FacilioField energyParentField) throws Exception
	{
		JSONArray mlIDList = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule logReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		FacilioModule readingModule = modBean.getModule("anomalydetectionmlreadings");
		
		FacilioField temperatureField = modBean.getField("temperature", FacilioConstants.ContextNames.WEATHER_READING);
		FacilioField temperatureParentField = modBean.getField("parentId", FacilioConstants.ContextNames.WEATHER_READING);
		
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
		
		for(AssetContext context:assetContextList)
		{
			long mlID = MLAPI.addMLModel("checkGam1",logReadingModule.getModuleId(),readingModule.getModuleId());
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true,aggregationMap.containsKey(energyField.getName())? aggregationMap.get(energyField.getName()):"SUM");
			if(markedField != null){
				MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),context.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false,aggregationMap.containsKey(markedField.getName())? aggregationMap.get(markedField.getName()):"SUM");
			}
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),context.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):1209600000l,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
			
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
		if(ratioCheckMLID != null){
			MLAPI.addMLModelVariables((long) mlIDList.get(mlIDList.length()-1),"jobid",""+ratioCheckMLID);
		}
		updateSequenceForMLModel((long)mlIDList.get(0),mlIDList.toString());
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);
		
		List<LocalTime> hourlyList = new ArrayList<LocalTime>();
		hourlyList.add(LocalTime.MIDNIGHT.plusMinutes(15));
		for(int i=1;i<24;i++)
		{
			hourlyList.add(LocalTime.MIDNIGHT.plusMinutes(15).plusHours(i));
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
        
        FacilioField idField = updateFields.stream().filter(f -> f.getName().equalsIgnoreCase("id")).findFirst().get();
        Condition idCondition = CriteriaAPI.getCondition(idField, String.valueOf(mlID), NumberOperators.EQUALS);
        
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName()).andCondition(idCondition).fields(updateFields);

        Map<String,Object> prop = new HashMap<String,Object>(1);
        prop.put("sequence", mlIDList);
        updateBuilder.update(prop);
	}
	
	private long addMultipleRatioCheckModel(List<Long> assetIDList, JSONArray ratioHierachyList,JSONObject mlVariables,long energyfieldid) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> mLLogCheckRatioFields = FieldFactory.getMLLogCheckRatioFields();
		int decimalColumnHigherValue = mLLogCheckRatioFields.stream().map(FacilioField::getColumnName).filter(f->f.startsWith("DECIMAL_CF")).map(c->c.replaceFirst("DECIMAL_CF", "")).map(Integer::parseInt).mapToInt(i -> i).max().orElse(0);
		for(Long id:assetIDList)
		{
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_ratio","ratioLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_upperAnomaly","upperAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_lowerAnomaly","lowerAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.NUMBER));				
		}
		
		Long checkRatioLogReadingNewModuleId = MLAPI.addReading(assetIDList,"checkRatioMLLogReadings",mLLogCheckRatioFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING,null);
		FacilioModule module = modBean.getModule("checkratiomlreadings");
		List<FacilioField> fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLCheckRatioFields();
		MLAPI.addReading(assetIDList,"checkRatioMLReadings",fields,ModuleFactory.getMLReadingModule().getTableName(),module);
		
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
		
		for(int i=0;i<ratioHierachyList.length();i++)
		{
			JSONArray emObject =(JSONArray) ratioHierachyList.get(i);
			long ml_id = mlIDList.get(i);
			LOGGER.info("EM Object "+emObject.toString()+"::"+ml_id);
			for(int j=0;j<emObject.length();j++)
			{
				Integer id =  (Integer) emObject.get(j);
				
				MLAPI.addMLVariables(ml_id,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(actualValueField.getName())? maxSamplingPeriodMap.get(actualValueField.getName()): 4200000,futureSamplingPeriodMap.containsKey(actualValueField.getName())? futureSamplingPeriodMap.get(actualValueField.getName()):0,j==0,aggregationMap.containsKey(actualValueField.getName())? aggregationMap.get(actualValueField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? maxSamplingPeriodMap.get(adjustedLowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? futureSamplingPeriodMap.get(adjustedLowerBoundField.getName()):0,false,aggregationMap.containsKey(adjustedLowerBoundField.getName())? aggregationMap.get(adjustedLowerBoundField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? maxSamplingPeriodMap.get(adjustedUpperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? futureSamplingPeriodMap.get(adjustedUpperBoundField.getName()):0,false,aggregationMap.containsKey(adjustedUpperBoundField.getName())? aggregationMap.get(adjustedUpperBoundField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(gamAnomalyField.getName())? maxSamplingPeriodMap.get(gamAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(gamAnomalyField.getName())? futureSamplingPeriodMap.get(gamAnomalyField.getName()):0,false,aggregationMap.containsKey(gamAnomalyField.getName())? aggregationMap.get(gamAnomalyField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerARMAField.getName())? maxSamplingPeriodMap.get(lowerARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerARMAField.getName())? futureSamplingPeriodMap.get(lowerARMAField.getName()):0,false,aggregationMap.containsKey(lowerARMAField.getName())? aggregationMap.get(lowerARMAField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerBoundField.getName())? maxSamplingPeriodMap.get(lowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerBoundField.getName())? futureSamplingPeriodMap.get(lowerBoundField.getName()):0,false,aggregationMap.containsKey(lowerBoundField.getName())? aggregationMap.get(lowerBoundField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerGAMField.getName())? maxSamplingPeriodMap.get(lowerGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerGAMField.getName())? futureSamplingPeriodMap.get(lowerGAMField.getName()):0,false,aggregationMap.containsKey(lowerGAMField.getName())? aggregationMap.get(lowerGAMField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(predictedField.getName())? maxSamplingPeriodMap.get(predictedField.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedField.getName())? futureSamplingPeriodMap.get(predictedField.getName()):0,false,aggregationMap.containsKey(predictedField.getName())? aggregationMap.get(predictedField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(predictedResidualFields.getName())? maxSamplingPeriodMap.get(predictedResidualFields.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedResidualFields.getName())? futureSamplingPeriodMap.get(predictedResidualFields.getName()):0,false,aggregationMap.containsKey(predictedResidualFields.getName())? aggregationMap.get(predictedResidualFields.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(residualField.getName())? maxSamplingPeriodMap.get(residualField.getName()): 4200000,futureSamplingPeriodMap.containsKey(residualField.getName())? futureSamplingPeriodMap.get(residualField.getName()):0,false,aggregationMap.containsKey(residualField.getName())? aggregationMap.get(residualField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()): 4200000,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? maxSamplingPeriodMap.get(lowerAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? futureSamplingPeriodMap.get(lowerAnomalyField.getName()):0,false,aggregationMap.containsKey(lowerAnomalyField.getName())? aggregationMap.get(lowerAnomalyField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperARMAField.getName())? maxSamplingPeriodMap.get(upperARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperARMAField.getName())? futureSamplingPeriodMap.get(upperARMAField.getName()):0,false,aggregationMap.containsKey(upperARMAField.getName())? aggregationMap.get(upperARMAField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperAnomalyField.getName())? maxSamplingPeriodMap.get(upperAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperAnomalyField.getName())? futureSamplingPeriodMap.get(upperAnomalyField.getName()):0,false,aggregationMap.containsKey(upperAnomalyField.getName())? aggregationMap.get(upperAnomalyField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperBoundField.getName())? maxSamplingPeriodMap.get(upperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperBoundField.getName())? futureSamplingPeriodMap.get(upperBoundField.getName()):0,false,aggregationMap.containsKey(upperBoundField.getName())? aggregationMap.get(upperBoundField.getName()):"SUM");
				MLAPI.addMLVariables(ml_id,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),id.longValue(),maxSamplingPeriodMap.containsKey(upperGAMField.getName())? maxSamplingPeriodMap.get(upperGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperGAMField.getName())? futureSamplingPeriodMap.get(upperGAMField.getName()):0,false,aggregationMap.containsKey(upperGAMField.getName())? aggregationMap.get(upperGAMField.getName()):"SUM");
			}
			MLAPI.addMLModelVariables(ml_id,"TreeHierarchy",emObject.toString());
			MLAPI.addMLModelVariables(ml_id,"energyfieldid",""+energyfieldid);
			MLAPI.addMLModelVariables(ml_id,"adjustedupperboundfieldid",""+adjustedUpperBoundField.getId());	
		}
		updateSequenceForMLModel(mlID,mlIDList.toString());
		return mlID;
	}
	
	private long addRatioCheckModel(List<Long> assetIDList, String TreeHierarchy,JSONObject mlVariables,long energyfieldid) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> mLLogCheckRatioFields = FieldFactory.getMLLogCheckRatioFields();
		int decimalColumnHigherValue = mLLogCheckRatioFields.stream().map(FacilioField::getColumnName).filter(f->f.startsWith("DECIMAL_CF")).map(c->c.replaceFirst("DECIMAL_CF", "")).map(Integer::parseInt).mapToInt(i -> i).max().orElse(0);
		for(Long id : assetIDList)
		{
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_ratio","ratioLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_upperAnomaly","upperAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));
			mLLogCheckRatioFields.add(FieldFactory.getField(id+"_lowerAnomaly","lowerAnomalyLog","DECIMAL_CF"+(++decimalColumnHigherValue),ModuleFactory.getMLLogReadingModule(),FieldType.DECIMAL));				
		}
		Long checkRatioLogReadingNewModuleId = MLAPI.addReading(assetIDList,"checkRatioMLLogReadings",mLLogCheckRatioFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING,null);
		FacilioModule module = modBean.getModule("checkratiomlreadings");
		List<FacilioField> fields = module != null ? modBean.getAllFields(module.getName()) : FieldFactory.getMLCheckRatioFields();
		MLAPI.addReading(assetIDList,"checkRatioMLReadings",fields,ModuleFactory.getMLReadingModule().getTableName(),module);
		
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
		
		
		for(Long id : assetIDList)
		{
			MLAPI.addMLVariables(mlID,actualValueField.getModuleId(),actualValueField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(actualValueField.getName())? maxSamplingPeriodMap.get(actualValueField.getName()): 4200000,futureSamplingPeriodMap.containsKey(actualValueField.getName())? futureSamplingPeriodMap.get(actualValueField.getName()):0,(id==Long.parseLong(TreeHierarchy.split(",")[0])),aggregationMap.containsKey(actualValueField.getName())? aggregationMap.get(actualValueField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,adjustedLowerBoundField.getModuleId(),adjustedLowerBoundField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? maxSamplingPeriodMap.get(adjustedLowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedLowerBoundField.getName())? futureSamplingPeriodMap.get(adjustedLowerBoundField.getName()):0,false,aggregationMap.containsKey(adjustedLowerBoundField.getName())? aggregationMap.get(adjustedLowerBoundField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,adjustedUpperBoundField.getModuleId(),adjustedUpperBoundField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? maxSamplingPeriodMap.get(adjustedUpperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(adjustedUpperBoundField.getName())? futureSamplingPeriodMap.get(adjustedUpperBoundField.getName()):0,false,aggregationMap.containsKey(adjustedUpperBoundField.getName())? aggregationMap.get(adjustedUpperBoundField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,gamAnomalyField.getModuleId(),gamAnomalyField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(gamAnomalyField.getName())? maxSamplingPeriodMap.get(gamAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(gamAnomalyField.getName())? futureSamplingPeriodMap.get(gamAnomalyField.getName()):0,false,aggregationMap.containsKey(gamAnomalyField.getName())? aggregationMap.get(gamAnomalyField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,lowerARMAField.getModuleId(),lowerARMAField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(lowerARMAField.getName())? maxSamplingPeriodMap.get(lowerARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerARMAField.getName())? futureSamplingPeriodMap.get(lowerARMAField.getName()):0,false,aggregationMap.containsKey(lowerARMAField.getName())? aggregationMap.get(lowerARMAField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,lowerBoundField.getModuleId(),lowerBoundField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(lowerBoundField.getName())? maxSamplingPeriodMap.get(lowerBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerBoundField.getName())? futureSamplingPeriodMap.get(lowerBoundField.getName()):0,false,aggregationMap.containsKey(lowerBoundField.getName())? aggregationMap.get(lowerBoundField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,lowerGAMField.getModuleId(),lowerGAMField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(lowerGAMField.getName())? maxSamplingPeriodMap.get(lowerGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerGAMField.getName())? futureSamplingPeriodMap.get(lowerGAMField.getName()):0,false,aggregationMap.containsKey(lowerGAMField.getName())? aggregationMap.get(lowerGAMField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,predictedField.getModuleId(),predictedField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(predictedField.getName())? maxSamplingPeriodMap.get(predictedField.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedField.getName())? futureSamplingPeriodMap.get(predictedField.getName()):0,false,aggregationMap.containsKey(predictedField.getName())? aggregationMap.get(predictedField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,predictedResidualFields.getModuleId(),predictedResidualFields.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(predictedResidualFields.getName())? maxSamplingPeriodMap.get(predictedResidualFields.getName()): 4200000,futureSamplingPeriodMap.containsKey(predictedResidualFields.getName())? futureSamplingPeriodMap.get(predictedResidualFields.getName()):0,false,aggregationMap.containsKey(predictedResidualFields.getName())? aggregationMap.get(predictedResidualFields.getName()):"SUM");
			MLAPI.addMLVariables(mlID,residualField.getModuleId(),residualField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(residualField.getName())? maxSamplingPeriodMap.get(residualField.getName()): 4200000,futureSamplingPeriodMap.containsKey(residualField.getName())? futureSamplingPeriodMap.get(residualField.getName()):0,false,aggregationMap.containsKey(residualField.getName())? aggregationMap.get(residualField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()): 4200000,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,lowerAnomalyField.getModuleId(),lowerAnomalyField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? maxSamplingPeriodMap.get(lowerAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(lowerAnomalyField.getName())? futureSamplingPeriodMap.get(lowerAnomalyField.getName()):0,false,aggregationMap.containsKey(lowerAnomalyField.getName())? aggregationMap.get(lowerAnomalyField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,upperARMAField.getModuleId(),upperARMAField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(upperARMAField.getName())? maxSamplingPeriodMap.get(upperARMAField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperARMAField.getName())? futureSamplingPeriodMap.get(upperARMAField.getName()):0,false,aggregationMap.containsKey(upperARMAField.getName())? aggregationMap.get(upperARMAField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,upperAnomalyField.getModuleId(),upperAnomalyField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(upperAnomalyField.getName())? maxSamplingPeriodMap.get(upperAnomalyField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperAnomalyField.getName())? futureSamplingPeriodMap.get(upperAnomalyField.getName()):0,false,aggregationMap.containsKey(upperAnomalyField.getName())? aggregationMap.get(upperAnomalyField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,upperBoundField.getModuleId(),upperBoundField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(upperBoundField.getName())? maxSamplingPeriodMap.get(upperBoundField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperBoundField.getName())? futureSamplingPeriodMap.get(upperBoundField.getName()):0,false,aggregationMap.containsKey(upperBoundField.getName())? aggregationMap.get(upperBoundField.getName()):"SUM");
			MLAPI.addMLVariables(mlID,upperGAMField.getModuleId(),upperGAMField.getFieldId(),parentField.getFieldId(),id,maxSamplingPeriodMap.containsKey(upperGAMField.getName())? maxSamplingPeriodMap.get(upperGAMField.getName()): 4200000,futureSamplingPeriodMap.containsKey(upperGAMField.getName())? futureSamplingPeriodMap.get(upperGAMField.getName()):0,false,aggregationMap.containsKey(upperGAMField.getName())? aggregationMap.get(upperGAMField.getName()):"SUM");
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
		
		for(AssetContext emContext : assetContextList)
		{
			long mlID = MLAPI.addMLModel("buildGamModel",-1,-1);
			MLAPI.addMLVariables(mlID,energyField.getModuleId(),energyField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(energyField.getName())? maxSamplingPeriodMap.get(energyField.getName()):7776000000L,futureSamplingPeriodMap.containsKey(energyField.getName())? futureSamplingPeriodMap.get(energyField.getName()):0,true,aggregationMap.containsKey(energyField.getName())? aggregationMap.get(energyField.getName()):"SUM");
			if(markedField != null){
				MLAPI.addMLVariables(mlID,markedField.getModuleId(),markedField.getFieldId(),energyParentField.getFieldId(),emContext.getId(),maxSamplingPeriodMap.containsKey(markedField.getName())? maxSamplingPeriodMap.get(markedField.getName()):7776000000L,futureSamplingPeriodMap.containsKey(markedField.getName())? futureSamplingPeriodMap.get(markedField.getName()):0,false,aggregationMap.containsKey(markedField.getName())? aggregationMap.get(markedField.getName()):"SUM");
			}
			MLAPI.addMLVariables(mlID,temperatureField.getModuleId(),temperatureField.getFieldId(),temperatureParentField.getFieldId(),emContext.getSiteId(),maxSamplingPeriodMap.containsKey(temperatureField.getName())? maxSamplingPeriodMap.get(temperatureField.getName()):7776000000L,futureSamplingPeriodMap.containsKey(temperatureField.getName())? futureSamplingPeriodMap.get(temperatureField.getName()):0,false,aggregationMap.containsKey(temperatureField.getName())? aggregationMap.get(temperatureField.getName()):"SUM");
			
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
