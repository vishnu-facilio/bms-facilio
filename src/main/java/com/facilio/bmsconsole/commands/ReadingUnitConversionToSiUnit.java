package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class ReadingUnitConversionToSiUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
			
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
		
		if(currentTask != null && reading != null) {
			TaskContext taskContext = TicketAPI.getTaskMap(Collections.singletonList(currentTask.getId())).get(currentTask.getId());
			if(taskContext.getInputTypeEnum() != null)
			{
				switch(taskContext.getInputTypeEnum()) {
				case READING:
					if (taskContext.getReadingField() != null && taskContext.getReadingFieldUnitEnum() != null && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) 
					{			
						NumberField readingNumberField = (NumberField) taskContext.getReadingField();
						if(readingNumberField.getMetricEnum() != null && reading!= null && currentTask.getReadingFieldUnitEnum() != null)
						{								
							Unit displayUnit = UnitsUtil.getDisplayUnit(readingNumberField);
							Double convertedreading = UnitsUtil.convertToSiUnit(reading.getReading(readingNumberField.getName()), displayUnit);
							reading.addReading(readingNumberField.getName(), convertedreading);
							context.put(FacilioConstants.ContextNames.READING, reading);
							
							currentTask.setInputValue(String.valueOf(convertedreading));
							context.put(FacilioConstants.ContextNames.TASK, currentTask);									
						}
					}
					break;		
				}
			}
		}
		
		return false;
	}

}
