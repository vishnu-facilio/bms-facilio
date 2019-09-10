package com.facilio.bmsconsole.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class ValidateReadingInputForTask extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(currentTask != null) {
			TaskContext taskContext = TicketAPI.getTaskMap(Collections.singletonList(currentTask.getId())).get(currentTask.getId());
			
			switch(taskContext.getInputTypeEnum()) {
			case READING:
				if (taskContext.getReadingField() != null && taskContext.getResource() != null) {
					
					switch(taskContext.getReadingField().getDataTypeEnum()) {
					case NUMBER:
						
						ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
						NumberField numberField = (NumberField) taskContext.getReadingField();
						
						if(numberField.isCounterField()) {
							checkIncremental(currentTask,numberField,rdm);		// to check whether the value is incemental
							checkValueCorrectness(currentTask,numberField,rdm);
						}
						else {
							checkValueCorrectness(currentTask,numberField,rdm);
						}
					}
				}
				break;
				
			}
		}
		return false;
	}

	private String checkValueCorrectness(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm) {
		
		double value = FacilioUtil.parseDouble(rdm.getValue());
		double currentvalue = FacilioUtil.parseDouble(currentTask.getInputValue());
		
		//get delta
		//get delta average
		//check whether value lies withiin average
		// if not find the root cause
		//case 1. unit error
		//case 2. mannual error
		//case 3. meter reset
	
		return "";
	}

	private String checkIncremental(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm) {
		
		// if not incremental
		// case 1. unit error
		// case 2. mannual error
//		/case 3. meter reset
		return "";
	}

}
