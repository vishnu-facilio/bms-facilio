package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingsForMLCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(GenerateMLModelCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		List<MLVariableContext> mlVariable = mlContext.getMLVariable();
		
		long currentTime = System.currentTimeMillis();
		if( AwsUtil.getConfig("environment").equals("development")) 
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
			
			List<Map<String, Object>> props = selectBuilder.getAsProps();
			LOGGER.info(" select Builder query "+selectBuilder);
			LOGGER.info(" props are "+props);
			for(Map<String,Object> prop : props)
			{
				data.put((long)prop.get(ttimeField.getName()), prop.get(variableField.getName()));
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
			mlContext.setMlCriteriaVariablesDataMap(criteriavariableData);
		}
		
		return false;
	}

}
