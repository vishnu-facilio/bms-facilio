package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

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
import com.facilio.util.FacilioUtil;

public class ReadingUnitConversionToRdmOrSiUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		
		if(recordIds != null && !recordIds.isEmpty() && currentTask.getReadingFieldUnitEnum() != null) 
		{
			Map<Long, TaskContext> tasks = TicketAPI.getTaskMap(recordIds);		
			if(tasks != null && currentTask != null && reading != null) 
			{
				TaskContext taskContext= tasks.get(recordIds.get(0));
				if(taskContext.getInputTypeEnum() != null)
				{
					switch(taskContext.getInputTypeEnum()) {
					case READING:
						if (taskContext.getReadingField() != null && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) 
						{
							if(currentTask.getInputValue() != null)
							{
								
								ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
								Double currentTaskValue = FacilioUtil.parseDouble(currentTask.getInputValue());
								Double convertedInputReading = null;
								
								if(rdm != null && rdm.getUnitEnum() != null)
								{
									Unit rdmUnit = rdm.getUnitEnum();
									convertedInputReading = UnitsUtil.convert(currentTaskValue, currentTask.getReadingFieldUnitEnum(), rdmUnit);
								}
							
								else 
								{	
									convertedInputReading = UnitsUtil.convertToSiUnit(currentTaskValue, currentTask.getReadingFieldUnitEnum());
								}
								
								reading.addReading(taskContext.getReadingField().getName(), convertedInputReading);
								context.put(FacilioConstants.ContextNames.READING, reading);			
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
