package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

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


}
