package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class SiUnitConversionToEnteredReadingUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION)) {
			return false;
		}
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
	
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		TaskContext taskContext = null;
		
		if(recordIds != null && !recordIds.isEmpty()) 
		{
			Map<Long, TaskContext> taskMap = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			if(MapUtils.isNotEmpty(taskMap))
			{
				taskContext = taskMap.get(recordIds.get(0));						
			}
		}	
		
		if(task != null && taskContext != null)
		{	
			if (taskContext.getReadingField() != null && task.getReadingFieldUnit() > 0 && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) 
			{
				NumberField numberField = (NumberField) taskContext.getReadingField();
				if(numberField.getMetricEnum() != null && taskContext.getInputValue() != null)
				{
					int siUnit = numberField.getMetricEnum().getSiUnitId();
					String convertedReadingString = WorkflowUtil.getStringValueFromDouble(UnitsUtil.convert(task.getInputValue(), siUnit, task.getReadingFieldUnit()));
					if(convertedReadingString != null) {
						task.setInputValue(String.valueOf(convertedReadingString));
					}
				}
			}
			context.put(FacilioConstants.ContextNames.TASK, task);		
		}
		
		
		return false;
	}

}
