package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class ReadingUnitConversionToSiUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
			
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		
		
		if(recordIds != null && !recordIds.isEmpty() && currentTask != null && currentTask.getReadingFieldUnitEnum() != null) 
		{
			Map<Long, TaskContext> tasks = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);		
			if(tasks != null && reading != null) 
			{
				TaskContext taskContext= tasks.get(recordIds.get(0));
				if(taskContext.getInputTypeEnum() != null)
				{
					switch(taskContext.getInputTypeEnum()) {
					case READING:
						if (taskContext.getReadingField() != null && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) 
						{			
							NumberField readingNumberField = (NumberField) taskContext.getReadingField();
							if(readingNumberField.getMetricEnum() != null)
							{								
								Unit displayUnit = UnitsUtil.getDisplayUnit(readingNumberField);
								
								Object value = UnitsUtil.convertToSiUnit(reading.getReading(readingNumberField.getName()), displayUnit);
								
								if(value != null) {
									reading.addReading(readingNumberField.getName(), value);
									context.put(FacilioConstants.ContextNames.READING, reading);
									
									currentTask.setInputValue(String.valueOf(value));
									context.put(FacilioConstants.ContextNames.TASK, currentTask);								
								}										
							}
						}
						break;		
					}
				}
			}
		}
		return false;
	}

}
