package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;


public class UpdateRdmWithLatestInputUnit extends FacilioCommand {
	
		@Override
		public boolean executeCommand(Context context) throws Exception {
		
			
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
	
		if(recordIds != null && !recordIds.isEmpty() && currentTask != null && reading!= null && currentTask.getReadingFieldUnit() > 0 ) 
		{
			Map<Long, TaskContext> taskMap = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);		
			if(MapUtils.isNotEmpty(taskMap)) 
			{
				TaskContext taskContext= taskMap.get(recordIds.get(0));
				if(taskContext.getInputTypeEnum() != null)
				{
					switch(taskContext.getInputTypeEnum()) {
						case READING:
							if (taskContext.getReadingField() != null && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) 
							{			
								NumberField readingNumberField = (NumberField) taskContext.getReadingField();
								ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), readingNumberField); 
								if(rdm != null && rdm.getUnit() != currentTask.getReadingFieldUnit())
								{
									rdm.setUnit(currentTask.getReadingFieldUnit());
									ReadingsAPI.updateReadingDataMeta(rdm);
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
