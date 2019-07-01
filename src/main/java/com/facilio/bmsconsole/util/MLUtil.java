package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.util.*;

;

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
	
	
	public static List<MLContext> getMlContext(JobContext jc) throws Exception
	{
		return MLUtil.getMLContext(jc.getJobId());
	}
	
	public static List<MLContext> getMLContext(long mlID) throws Exception
	{
		List<MLContext> mlContextList = new ArrayList<MLContext>(10);
		
		//prepare builder to get ML details
		FacilioModule mlModule = ModuleFactory.getMLModule();
		List<FacilioField> mlModuleFields = FieldFactory.getMLFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(mlModuleFields)
														.table(mlModule.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlModule))
														.andCondition(CriteriaAPI.getIdCondition(mlID, mlModule));
		
		List<Map<String, Object>> listMap = selectBuilder.get();
		MLContext jobMlContext = FieldUtil.getAsBeanFromMap(listMap.get(0), MLContext.class);
		Hashtable<Long,Integer> positionList = new Hashtable<Long,Integer>();
		
		if(jobMlContext.getSequence()!=null)
		{
			ArrayList<Long> listdata = new ArrayList<Long>();     
			JSONArray sequenceArr = new JSONArray(jobMlContext.getSequence());
			for (int i=0;i<sequenceArr.length();i++)
			{ 
			    listdata.add(sequenceArr.getLong(i));
			    positionList.put(sequenceArr.getLong(i), i);
			}
			GenericSelectRecordBuilder selectMlContextBuilder = new GenericSelectRecordBuilder()
																	.select(mlModuleFields)
																	.table(mlModule.getTableName())
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlModule))
																	.andCondition(CriteriaAPI.getIdCondition(listdata, mlModule));
			
			listMap = selectMlContextBuilder.get();
		}
		
		for(Map<String,Object> map:listMap)
		{
			MLContext mlContext = FieldUtil.getAsBeanFromMap(map, MLContext.class);
			
			List<Map<String,Object>> modelVariableList = MLUtil.get(ModuleFactory.getMLModelVariablesModule(), FieldFactory.getMLModelVariablesFields(),mlContext);
			for(Map<String,Object> prop : modelVariableList)
			{
				MLModelVariableContext modelContext =FieldUtil.getAsBeanFromMap(prop, MLModelVariableContext.class);
				mlContext.addMLModelVariable(modelContext);
			}
			
			//prepare builder to get MLVariables
			List<Map<String,Object>> mlVariableList = MLUtil.get(ModuleFactory.getMLVariablesModule(), FieldFactory.getMLVariablesFields(),mlContext);
			for(Map<String,Object> prop : mlVariableList)
			{
				Condition mlIDCondition=CriteriaAPI.getCondition("ML_ID",String.valueOf(mlContext.getId()), String.valueOf(mlContext.getId()),NumberOperators.EQUALS);
				MLVariableContext mlVariableContext = FieldUtil.getAsBeanFromMap(prop, MLVariableContext.class);
				Condition assetCondition=CriteriaAPI.getCondition("AssetID",String.valueOf(mlVariableContext.getParentID()), String.valueOf(mlVariableContext.getParentID()),NumberOperators.EQUALS);
				FacilioModule mlAssetVariablesModule = ModuleFactory.getMLAssetVariablesModule();
				List<FacilioField> mlAssetVariableFields = FieldFactory.getMLAssetVariablesFields();
				GenericSelectRecordBuilder selectBuilder2 = new GenericSelectRecordBuilder()
																.select(mlAssetVariableFields)
																.table(mlAssetVariablesModule.getTableName())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(mlAssetVariablesModule))
																.andCondition(mlIDCondition)
																.andCondition(assetCondition);
					
				List<Map<String,Object>> assetVariableList = selectBuilder2.get();
				for(Map<String,Object> props : assetVariableList)
				{
					MLAssetVariableContext assetVariableContext =FieldUtil.getAsBeanFromMap(props, MLAssetVariableContext.class);
					mlContext.setAssetVariables(mlVariableContext.getParentID(),assetVariableContext);
				}
				mlContext.addMLVariable(mlVariableContext);
			}
			
			//prepare builder to get MLCriteria Variables
			List<Map<String,Object>> mlCriteriaVariableList = MLUtil.get(ModuleFactory.getMLCriteriaVariablesModule(), FieldFactory.getMLCriteriaVariablesFields(),mlContext);
			for(Map<String,Object> prop : mlCriteriaVariableList)
			{
				MLVariableContext mlVariableContext = FieldUtil.getAsBeanFromMap(prop, MLVariableContext.class);
				mlContext.addMLCriteriaVariable(mlVariableContext);
			}
			//LOGGER.fatal("To be removed JAVEED mlCOntext size and sequence are  "+mlContextList.size()+"::"+mlContext.getSequence());
			if(jobMlContext.getSequence()!=null)
			{
				int position = positionList.get(mlContext.getId());
				if(mlContextList.size()>=position)
				{
					mlContextList.add(position,mlContext);
				}
				else
				{
					mlContextList.add(mlContext);
				}
			}
			else
			{
				mlContextList.add(mlContext);
			}
		}
		
		return mlContextList;	
		
	}
	
	private static List<Map<String,Object>> get(FacilioModule module,List<FacilioField> fieldList,MLContext mlContext) throws Exception
	{
		Condition mlIDCondition=CriteriaAPI.getCondition("ML_ID",String.valueOf(mlContext.getId()), String.valueOf(mlContext.getId()),NumberOperators.EQUALS);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fieldList)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(mlIDCondition);
		return selectBuilder.get();
	}
	

}
