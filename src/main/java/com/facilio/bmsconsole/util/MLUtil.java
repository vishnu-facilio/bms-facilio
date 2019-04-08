package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLAssetContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class MLUtil 
{
	private static final Logger LOGGER = Logger.getLogger(MLUtil.class.getName());
	
	public static void getFields(MlForecastingContext pc) throws Exception
	{
		try
		{
			FacilioModule module = ModuleFactory.getMlForecastingFieldsModule();
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getMlForecastingFieldsFields())
														.andCondition(CriteriaAPI.getIdCondition(pc.getId(), module));
			
			List<Map<String,Object>> predictionFieldMap = recordBuilder.get();
			
			List<FacilioField> columnFields = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for(Map<String,Object> fieldMap : predictionFieldMap)
			{
				FacilioField field = modBean.getField((Long)fieldMap.get("fieldid"));
				if(columnFields.size()==0)
				{
					FacilioField ttimeField = modBean.getField("ttime", field.getModule().getName());
					columnFields.add(ttimeField);
				}
				columnFields.add(field);
			}
			pc.setFields(columnFields);
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting fields for "+pc.getId(), e);
			throw e;
		}
		
	}
	
	public static MlForecastingContext getContext(long forecastingID) throws Exception
	{
		FacilioModule module = ModuleFactory.getMlForecastingModule();
		GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(FieldFactory.getMlForecastingFields())
													.andCondition(CriteriaAPI.getIdCondition(forecastingID, module));
		
		List<Map<String, Object>> predictionListMap = recordBuilder.get();
		MlForecastingContext mlForecast = FieldUtil.getAsBeanFromMap(predictionListMap.get(0), MlForecastingContext.class);
		getFields(mlForecast);
		return mlForecast;
	}
	
	public static boolean checkValidPrediction(MlForecastingContext context,List<Long> predictedTimeArray)
	{
		try
		{
			long minTime = predictedTimeArray.stream().min(Comparator.comparing(Long::valueOf)).get();
			SortedMap<Long,ReadingContext> actualValues = new TreeMap<Long,ReadingContext>();
			FacilioField actualField = context.getFields().get(0);
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
					.select(context.getFields())
					.module(context.getFields().get(0).getModule())
					.beanClass(ReadingContext.class)
					.orderBy("TTIME ASC")
					.andCustomWhere("TTIME >= ? AND PARENT_ID=? ",
							minTime, context.getAssetid());
			
			List<ReadingContext> props = selectBuilder.get();
			props.forEach(readingContext->actualValues.put(readingContext.getTtime(), readingContext));
			
			long validCount=0;
			
			for(long predictedTime : predictedTimeArray)
			{
				Condition parentCondition=CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(context.getAssetid()),NumberOperators.EQUALS);
				Condition ttimeCondition=CriteriaAPI.getCondition("TTIME","ttime", getTtimeList(props),NumberOperators.EQUALS);
				Condition predictedTimeCondition = CriteriaAPI.getCondition("PREDICTED_TIME", "predictedTime",String.valueOf(predictedTime),NumberOperators.EQUALS);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField predictLogField = modBean.getField(context.getPredictedlogfieldid());
				FacilioModule predictLogModule = predictLogField.getModule();
				List<FacilioField> fields = modBean.getAllFields(predictLogModule.getName());
				String tableName=predictLogModule.getTableName();
				SelectRecordsBuilder<ReadingContext> selectBuilder1 = new SelectRecordsBuilder<ReadingContext>()
																			.select(fields)
																			.table(tableName)
																			.moduleName(predictLogModule.getName())
																			.maxLevel(0)
																			.beanClass(ReadingContext.class)
																			.andCondition(parentCondition)
																			.andCondition(predictedTimeCondition)
																			.andCondition(ttimeCondition);
				List<ReadingContext> predictedList = selectBuilder1.get();
				SortedMap<Long,ReadingContext> predictedValues = new TreeMap<Long,ReadingContext>();
				predictedList.forEach(readingContext->predictedValues.put(readingContext.getTtime(), readingContext));
				
				if(MLUtil.isValidPrediction(actualField,predictLogField,actualValues, predictedValues))
				{
					validCount++;
				}
			}
			if(validCount>predictedTimeArray.size()/2)
			{
				return true;
			}
			
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error whiele validating Prediction ",e);
		}
		return false;
	}
	
	private static String getTtimeList(List<ReadingContext> readingList) 
	{
		StringJoiner ttimeCriteria = new StringJoiner(",");
		readingList.forEach(reading->ttimeCriteria.add(String.valueOf(reading.getTtime())));
		return ttimeCriteria.toString();
	}
	
	public static boolean isValidPrediction(FacilioField actualField,FacilioField logField,SortedMap<Long,ReadingContext> actual,SortedMap<Long,ReadingContext> predicted)
	{	
		long errorPercentage = 15;
		long wrongPredictionCount=0;
		
		//Long[] keyList = (Long[]) actual.keySet().toArray();
		Long[] keyList = new Long[actual.size()];
		Iterator<Long> itr = actual.keySet().iterator();
		int i=0;
		while(itr.hasNext())
		{
			keyList[i]= itr.next();
			i++;
		}
		
		Long midPoint = keyList[keyList.length/2];
		
		SortedMap<Long,ReadingContext> lastHalfPredicted = predicted.tailMap(midPoint);
		
		Set<Long> keySet = lastHalfPredicted.keySet();
		for(long key: keySet)
		{
			Double actualValue = (Double) actual.get(key).getReading(actualField.getName());
			Double predictedValue = (Double) predicted.get(key).getReading(logField.getName());
			
			Double lowCeiling = actualValue - ( actualValue * errorPercentage /100);
			Double highCeiling = actualValue + (actualValue * errorPercentage/100);
			
			if(!(predictedValue > lowCeiling && predictedValue < highCeiling))
			{
				wrongPredictionCount++;
			}
		}
		
		if(wrongPredictionCount > actual.size()/4)
		{
			return false;
		}
		
		return true;
	}
	
	
	public static MLContext getMlContext(long mlID,long orgID) throws Exception
	{

		//prepare builder to get ML details
		FacilioModule mlModule = ModuleFactory.getMLModule();
		List<FacilioField> mlModuleFields = FieldFactory.getMLFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(mlModuleFields)
														.table(mlModule.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlModule))
														.andCondition(CriteriaAPI.getIdCondition(mlID, mlModule));
		
		List<Map<String, Object>> listMap = selectBuilder.get();
		MLContext mlContext = FieldUtil.getAsBeanFromMap(listMap.get(0), MLContext.class);
		
		//prepare builder to get AssetDetails
		Condition mlIDCondition=CriteriaAPI.getCondition("ML_ID",String.valueOf(mlContext.getId()), String.valueOf(mlID),NumberOperators.EQUALS);
		FacilioModule mlAssetModule = ModuleFactory.getMLAssetModule();
		List<FacilioField> mlAssetFields = FieldFactory.getMLAssetFields();
		GenericSelectRecordBuilder selectBuilder1 = new GenericSelectRecordBuilder()
														.select(mlAssetFields)
														.table(mlAssetModule.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlAssetModule))
														.andCondition(mlIDCondition);
			
		List<Map<String,Object>> assetMLListMap = selectBuilder1.get();
		List<MLAssetContext> mlAssetList = MLUtil.getMLAssetContext(assetMLListMap);
		for(MLAssetContext mlAssetContext : mlAssetList)
		{
			Condition assetCondition=CriteriaAPI.getCondition("AssetID",String.valueOf(mlAssetContext.getAssetID()), String.valueOf(mlAssetContext.getAssetID()),NumberOperators.EQUALS);
			FacilioModule mlAssetVariablesModule = ModuleFactory.getMLAssetVariablesModule();
			List<FacilioField> mlAssetVariableFields = FieldFactory.getMLAssetVariablesFields();
			GenericSelectRecordBuilder selectBuilder2 = new GenericSelectRecordBuilder()
															.select(mlAssetVariableFields)
															.table(mlAssetVariablesModule.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlAssetModule))
															.andCondition(mlIDCondition)
															.andCondition(assetCondition);
				
			List<Map<String,Object>> assetVariableList = selectBuilder2.get();
			for(Map<String,Object> prop : assetVariableList)
			{
				mlAssetContext.addData(prop);
			}
		}
		mlContext.setAssetContext(mlAssetList.get(0));
			
		
		//prepare builder to get ModelVariables
		FacilioModule mlModelVariablesModule = ModuleFactory.getMLModelVariablesModule();
		List<FacilioField> mlModelVariableAssetFields = FieldFactory.getMLModelVariablesFields();
		GenericSelectRecordBuilder selectBuilder5 = new GenericSelectRecordBuilder()
														.select(mlModelVariableAssetFields)
														.table(mlModelVariablesModule.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlModelVariablesModule))
														.andCondition(mlIDCondition);
		List<Map<String,Object>> modelVariableList = selectBuilder5.get();
		for(Map<String,Object> prop : modelVariableList)
		{
			mlContext.addData(prop);
		}
		
		//prepare builder to get MLVariables
		FacilioModule mlVariablesModule = ModuleFactory.getMLVariablesModule();
		List<FacilioField> mlVariableAssetFields = FieldFactory.getMLVariablesFields();
		GenericSelectRecordBuilder selectBuilder6 = new GenericSelectRecordBuilder()
														.select(mlVariableAssetFields)
														.table(mlVariablesModule.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlVariablesModule))
														.andCondition(mlIDCondition);
		List<Map<String,Object>> mlVariableList = selectBuilder6.get();
		for(Map<String,Object> prop : mlVariableList)
		{
			MLVariableContext mlVariableContext = FieldUtil.getAsBeanFromMap(prop, MLVariableContext.class);
			mlContext.addMLVariable(mlVariableContext);
		}
		
		//prepare builder to get MLCriteria Variables
		FacilioModule mlCriteriaVariablesModule = ModuleFactory.getMLCriteriaVariablesModule();
		List<FacilioField> mlCriteriaVariableFields = FieldFactory.getMLCriteriaVariablesFields();
		GenericSelectRecordBuilder selectBuilder7 = new GenericSelectRecordBuilder()
														.select(mlCriteriaVariableFields)
														.table(mlCriteriaVariablesModule.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlCriteriaVariablesModule))
														.andCondition(mlIDCondition);
		List<Map<String,Object>> mlCriteriaVariableList = selectBuilder7.get();
		for(Map<String,Object> prop : mlCriteriaVariableList)
		{
			MLVariableContext mlVariableContext = FieldUtil.getAsBeanFromMap(prop, MLVariableContext.class);
			mlContext.addMLCriteriaVariable(mlVariableContext);
		}
		
		return mlContext;
	}
	
	private static List<MLAssetContext> getMLAssetContext(List<Map<String,Object>> listMap)
	{
		if(listMap != null && listMap.size() > 0) 
		{
			List<MLAssetContext> mlList = new ArrayList<>();
			listMap.forEach(prop->mlList.add(FieldUtil.getAsBeanFromMap(prop, MLAssetContext.class)));
			return mlList;
		}
		return null;
	}


}
