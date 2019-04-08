package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

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

	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		List<MLVariableContext> mlVariable = mlContext.getMLVariable();
		
		long maxSamplingPeriod = mlContext.getMaxSamplingPeriod();
		long currentTime = System.currentTimeMillis();
		long startTime = currentTime-maxSamplingPeriod;
		SortedMap<Long,Hashtable<String,Object>> variableData = new TreeMap<Long,Hashtable<String,Object>>();
		SortedMap<Long,Hashtable<String,Object>> criteriavariableData = new TreeMap<Long,Hashtable<String,Object>>();
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(MLVariableContext variables:mlVariable)
		{
			
            FacilioField variableField = modBean.getField(variables.getFieldid());
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
																			startTime, currentTime, mlContext.getAssetContext().getAssetID());
			List<Map<String, Object>> props = selectBuilder.getAsProps();
			for(Map<String,Object> prop : props)
			{
				long ttime = (long)prop.get(ttimeField.getName());
				if(variableData.containsKey(ttime))
				{
					variableData.get(ttime).put(variableField.getName(), prop.get(variableField.getName()));
				}
				else
				{
					Hashtable<String,Object> data = new Hashtable<String,Object>();
					data.put(variableField.getName(), prop.get(variableField.getName()));
					variableData.put(ttime, data);
				}
			}
			
		}
		
		List<MLVariableContext> mlCriteriaVariable = mlContext.getMLCriteriaVariables();
		for(MLVariableContext variables:mlCriteriaVariable)
		{
			
            FacilioField variableField = modBean.getField(variables.getFieldid());
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
																			startTime, currentTime, mlContext.getAssetContext().getAssetID());
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
		
		mlContext.setMlVariablesDataMap(variableData);
		mlContext.setMlCriteriaVariablesDataMap(criteriavariableData);
		
		return false;
	}

}
