package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskErrorContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class ValidateReadingInputForTask extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		List<TaskErrorContext> errors = new ArrayList<>();
		if(currentTask != null) {
			TaskContext taskContext = TicketAPI.getTaskMap(Collections.singletonList(currentTask.getId())).get(currentTask.getId());
			
			switch(taskContext.getInputTypeEnum()) {
			case READING:
				if (taskContext.getReadingField() != null && taskContext.getResource() != null) {
					
					switch(taskContext.getReadingField().getDataTypeEnum()) {
					case NUMBER:
					case DECIMAL:
						
						ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
						NumberField numberField = (NumberField) taskContext.getReadingField();
						
						Double currentValue = FacilioUtil.parseDouble(currentTask.getInputValue());
						
						Double currentValueInSiUnit = currentValue;
						if(taskContext.getReadingFieldUnit() > 0) {
							currentValueInSiUnit = UnitsUtil.convertToSiUnit(currentValue, Unit.valueOf(taskContext.getReadingFieldUnit()));
						}
						else if (rdm.getUnit() > 0) {
							currentValueInSiUnit = UnitsUtil.convertToSiUnit(currentValue, Unit.valueOf(rdm.getUnit()));
						}
						
						if(numberField.isCounterField()) {
							TaskErrorContext taskError = checkIncremental(currentTask,numberField,rdm,currentValueInSiUnit);
							if(taskError!= null) {
								errors.add(taskError);
							}
//							checkValueCorrectness(currentTask,numberField,rdm,currentValueInSiUnit);
						}
//						else {
//							checkValueCorrectness(currentTask,numberField,rdm,currentValueInSiUnit);
//						}
					}
				}
				break;
				
			}
		}
		
		if(!errors.isEmpty()) {
			context.put(FacilioConstants.ContextNames.TASK_ERRORS, errors);
			return true;
		}
		return false;
	}

	private String checkValueCorrectness(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm, Double currentValueInSiUnit) {
		
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

	private TaskErrorContext checkIncremental(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm, Double currentValueInSiUnit) throws Exception {
		
		double previousValue = FacilioUtil.parseDouble(rdm.getValue());
		if(currentValueInSiUnit < previousValue) {
			TaskErrorContext error = new TaskErrorContext();
			error.setMode(TaskErrorContext.Mode.ERROR.getValue());
			error.setFailType(TaskErrorContext.FailType.NON_INCREMENTAL_VALUE.getValue());
			error.setMessage("Entered value is less than previous value");
			
			String previousValueString = previousValue+"";
			if(numberField.getMetric() > 0) {
				previousValue = (double) UnitsUtil.convertToDisplayUnit(previousValue, numberField);
				previousValueString  = previousValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			} 
			
			error.setPreviousValue(previousValueString);
			
			String currentValueString = currentTask.getInputValue()+"";
			
			Unit unit = getCurrentInputUnit(rdm, currentTask, numberField);
			
			if(unit != null) {
				currentValueString = currentValueString + " "+unit.getSymbol();
			}
			
			error.setCurrentValue(currentValueString);
			
			return error;
		}
		return null;
	}
	
	Unit getCurrentInputUnit(ReadingDataMeta rdm,TaskContext currentTask,NumberField numberField) throws Exception {
		if(currentTask.getReadingFieldUnit() > 0) {
			return Unit.valueOf(currentTask.getReadingFieldUnit());
		}
		else if(rdm.getUnit() > 0) {
			return Unit.valueOf(rdm.getUnit());
		}
		return UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), numberField.getMetric());
	}
}
