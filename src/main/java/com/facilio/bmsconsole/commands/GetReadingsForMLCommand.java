package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class GetReadingsForMLCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetReadingsForMLCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		LOGGER.info("inside getReadingsforML");
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		List<MLVariableContext> mlVariable = mlContext.getMLVariable();
		
		long currentTime = mlContext.isHistoric() ? mlContext.getExecutionEndTime() : System.currentTimeMillis();
		if( FacilioProperties.isDevelopment() && !mlContext.isHistoric())
		{
			// for dev testing purpose time is moved back 
			currentTime = 1536300000000L;
		}
		
		
		SortedMap<Long,Hashtable<String,Object>> variableData = new TreeMap<Long,Hashtable<String,Object>>();
		SortedMap<Long,Hashtable<String,Object>> criteriavariableData = new TreeMap<Long,Hashtable<String,Object>>();
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(MLVariableContext variables:mlVariable)
		{
			long startTime = currentTime-variables.getMaxSamplingPeriod();
			if(variables.getFutureSamplingPeriod()!=0L)
			{
				currentTime = currentTime + variables.getFutureSamplingPeriod();
			}
			SortedMap<Long,Object> data = new TreeMap<Long,Object>();
            FacilioField variableField = modBean.getField(variables.getFieldID());
            FacilioField parentField = modBean.getField(variables.getParentFieldID());
            FacilioField ttimeField = FieldFactory.getField("ttime", "TTIME", variableField.getModule(), FieldType.NUMBER);
            List<FacilioField> fieldList = new ArrayList<FacilioField>(2);
            fieldList.add(variableField);
            fieldList.add(ttimeField);
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																	.select(fieldList)
																	.module(variableField.getModule())
																	.beanClass(ReadingContext.class)
																	.orderBy("TTIME ASC")
																	.andCustomWhere("TTIME >= ? AND TTIME < ? AND "+parentField.getColumnName()+"=? ",
																			startTime, currentTime,variables.getParentID());
			if(mlContext.getOrgId()==232 && mlContext.getId()==83)
			{
				LOGGER.info("JAVEED"+selectBuilder.toString()+"::"+startTime+"::"+currentTime);
			}
			List<Map<String, Object>> props = selectBuilder.getAsProps();
			
			for(Map<String,Object> prop : props)
			{
					long ttime = (long) prop.get(ttimeField.getName());
					
					if(data.containsKey(ttime) && variableField.getDataTypeEnum().equals(FieldType.DECIMAL)){
						Double previousValue = (Double) FacilioUtil.castOrParseValueAsPerType(variableField, data.get(ttime));
						Double currentValue = (Double) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName()));
						Double newValue = previousValue == null && currentValue == null ? null : previousValue == null ? currentValue : currentValue == null ? previousValue : previousValue + currentValue;
						data.put(ttime, newValue);
					}else if(data.containsKey(ttime) && variableField.getDataTypeEnum().equals(FieldType.NUMBER)){
						Long previousValue = (Long) FacilioUtil.castOrParseValueAsPerType(variableField, data.get(ttime));
						Long currentValue = (Long) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName()));
						Long newValue = previousValue == null && currentValue == null ? null : previousValue == null ? currentValue : currentValue == null ? previousValue : previousValue + currentValue;
						data.put(ttime, newValue);
					}else{
						if(variableField.getDataTypeEnum().equals(FieldType.DECIMAL)){
							data.put(ttime,(Double) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName())));
						}else if(variableField.getDataTypeEnum().equals(FieldType.NUMBER)){
							data.put(ttime, (Long) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName())));
						}else{
							data.put(ttime, prop.get(variableField.getName()));
						}
					}
			}
			
			if("AVG".equalsIgnoreCase(variables.getAggregation())){

				Map<Long, Long> timeOccurenceMap = props.stream().collect(Collectors.groupingBy(prop -> (long) prop.get(ttimeField.getName()), Collectors.counting()));
				
				for(Entry<Long,Object> en:data.entrySet()){
					if(variableField.getDataTypeEnum().equals(FieldType.DECIMAL)){
						Double tot = (Double) FacilioUtil.castOrParseValueAsPerType(variableField, en.getValue());
						data.put(en.getKey(), tot/timeOccurenceMap.get(en.getKey()));
					}else if(variableField.getDataTypeEnum().equals(FieldType.NUMBER)){
						Long tot = (Long) FacilioUtil.castOrParseValueAsPerType(variableField, en.getValue());
						data.put(en.getKey(), tot/timeOccurenceMap.get(en.getKey()));
					}
				}
			}
			
			if(mlContext.getOrgId()==232 && mlContext.getId()==83)
			{
				
				LOGGER.info("Javeed"+data+"::"+selectBuilder.toString());
			}
			mlContext.setMlVariablesDataMap(variables.getParentID(), variableField.getName(), data);
		}
		
		List<MLVariableContext> mlCriteriaVariable = mlContext.getMLCriteriaVariables();
		if(mlCriteriaVariable!=null)
		{
			for(MLVariableContext variables:mlCriteriaVariable)
			{
				long startTime = currentTime-variables.getMaxSamplingPeriod();
	            FacilioField variableField = modBean.getField(variables.getFieldID());
	            FacilioField ttimeField = FieldFactory.getField("ttime", "TTIME", variableField.getModule(), FieldType.NUMBER);
	            List<FacilioField> fieldList = new ArrayList<FacilioField>(2);
	            fieldList.add(variableField);
	            fieldList.add(ttimeField);
				SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.select(fieldList)
																		.module(variableField.getModule())
																		.beanClass(ReadingContext.class)
																		.orderBy("TTIME ASC")
																		.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
																				startTime, currentTime, variables.getParentID());
				List<Map<String, Object>> props = selectBuilder.getAsProps();
				for(Map<String,Object> prop : props)
				{
					long ttime = (long)prop.get(ttimeField.getName());
					if(prop.get(variableField.getName())!=null)
					{
						if(criteriavariableData.containsKey(ttime))
						{
							criteriavariableData.get(ttime).put(variableField.getName(), prop.get(variableField.getName()));
						}
						else
						{
							Hashtable<String,Object> data = new Hashtable<String,Object>();
							data.put(variableField.getName(), prop.get(variableField.getName()));
							criteriavariableData.put(ttime, data);
						}
					}
				}
				
			}
			mlContext.setMlCriteriaVariablesDataMap(criteriavariableData);
		}
		
		return false;
	}

}
