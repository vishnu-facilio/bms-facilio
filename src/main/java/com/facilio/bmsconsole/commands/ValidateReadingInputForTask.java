package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.joda.time.DateTime;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskErrorContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;

public class ValidateReadingInputForTask extends FacilioCommand {

	
	public static final int noOfDaysDeltaToBeFetched = 10;
	
	public final static int averageboundPercentage = 30; 
	
	private static final Logger LOGGER = Logger.getLogger(ValidateReadingInputForTask.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			
			if(AccountUtil.getCurrentOrg().getId() != 155l) {
				return false;
			}
			
			TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			List<TaskErrorContext> errors = new ArrayList<>();
			boolean hasErrors = false;
			if(currentTask != null) {
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				if(recordIds != null && !recordIds.isEmpty()) {
					Map<Long, TaskContext> oldTasks = TicketAPI.getTaskMap(recordIds);
					for(int i = 0; i < recordIds.size(); i++) {
						TaskContext taskContext = oldTasks.get(recordIds.get(i));
						
						switch(taskContext.getInputTypeEnum()) {
						case READING:
							if (taskContext.getReadingField() != null && taskContext.getResource() != null) {
								
								switch(taskContext.getReadingField().getDataTypeEnum()) {
								case NUMBER:
								case DECIMAL:
									
									ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
									NumberField numberField = (NumberField) taskContext.getReadingField();
									
									if(rdm.getValue() != null && rdm.getValue().equals("-1.0")) {
										return true;
									}
									
									Double currentValue = FacilioUtil.parseDouble(currentTask.getInputValue());
									
									Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
									
									Double currentValueInSiUnit = UnitsUtil.convertToSiUnit(currentValue, currentInputUnit);
									
									if(numberField.isCounterField()) {
										TaskErrorContext taskError = checkIncremental(currentTask,numberField,rdm,currentValueInSiUnit);
										if(taskError!= null) {
											hasErrors = true;
											errors.add(taskError);
											TaskErrorContext unitSuggetion = checkUnitForValueError(currentTask,numberField,rdm,currentValueInSiUnit);
											if(unitSuggetion != null) {
												errors.add(unitSuggetion);
											}
										}
										else {
											taskError = checkValueRangeForCounterField(currentTask,numberField,rdm,currentValueInSiUnit);
											if(taskError!= null) {
												errors.add(taskError);
												TaskErrorContext unitSuggetion = checkUnitForValueError(currentTask,numberField,rdm,currentValueInSiUnit);
												if(unitSuggetion != null) {
													errors.add(unitSuggetion);
												}
											}
										}
										
									}
								}
							}
							break;
						}
					}
				}
			}
			if(!errors.isEmpty()) {
				context.put(FacilioConstants.ContextNames.TASK_ERRORS, errors);
				context.put(FacilioConstants.ContextNames.HAS_TASK_ERRORS, hasErrors);
//				if(hasErrors) {
					return true;
//				}
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}
	
	
	private TaskErrorContext checkUnitForHighValueError(TaskContext currentTask, NumberField numberField,ReadingDataMeta rdm, Double currentValueInSiUnit) throws Exception {

		double value = FacilioUtil.parseDouble(rdm.getValue());
		double currentvalue = FacilioUtil.parseDouble(currentTask.getInputValue());
		
		double diff = value / currentvalue;
		
		Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
		
		Unit suggestedUnit = UnitsUtil.getUnitMultiplierResult(currentInputUnit, diff);
		
		if(suggestedUnit != null) {
			
			TaskErrorContext error = new TaskErrorContext();
			error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
			error.setSuggestionType(TaskErrorContext.SuggestionType.UNIT_CHANGE.getValue());
			error.setMessage("You might have to check your unit");
			
			double previousValue = FacilioUtil.parseDouble(rdm.getValue());
			String previousValueString = previousValue+"";
			if(numberField.getMetric() > 0) {
				previousValue = (double) UnitsUtil.convertToDisplayUnit(previousValue, numberField);
				previousValueString  = previousValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			} 
			
			error.setPreviousValue(previousValueString);
			
			String currentValueString = currentTask.getInputValue()+"";
			
			if(currentInputUnit != null) {
				currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
			}
			
			error.setCurrentValue(currentValueString);
			
			return error;
		}
		return null;
	}


	public Double getAverageValue(ReadingDataMeta rdm,NumberField numberField) throws Exception {
		
		if(rdm.getResourceId() > 0) {
			
			FacilioModule module = numberField.getModule();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
			
			DateRange lastNdays = DateOperators.LAST_N_DAYS.getRange(noOfDaysDeltaToBeFetched+"");
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(module.getTableName())
					.module(module)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), rdm.getResourceId()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), lastNdays.getStartTime()+","+DateTimeUtil.getCurrenTime(), DateOperators.BETWEEN))
					.aggregate(NumberAggregateOperator.AVERAGE, numberField);
					;
			
			List<Map<String, Object>> res = selectBuilder.getAsProps();
			if(res != null && !res.isEmpty()) {
				Double average = (Double) res.get(0).get(numberField.getName());
				return average;
			}
		}
		return null;
	}

	private TaskErrorContext checkValueRangeForCounterField(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm,Double currentValueInSiUnit) throws Exception {

		double value = FacilioUtil.parseDouble(rdm.getValue());
		double currentvalue = FacilioUtil.parseDouble(currentTask.getInputValue());
		
		double currentDelta = currentvalue-value;
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Double averageValue = getAverageValue(rdm, (NumberField)modBean.getField(numberField.getName()+"Delta", numberField.getModule().getName()));
		
		if(averageValue != null) {
			
			Double averageLowerLimit = averageValue - (averageValue * averageboundPercentage /100);
			Double averageHigherLimit = averageValue + (averageValue * averageboundPercentage /100);
			
			if(currentDelta <= averageLowerLimit) {
				
				TaskErrorContext error = new TaskErrorContext();
				error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
				error.setSuggestionType(TaskErrorContext.SuggestionType.LESS_THAN_AVG_VALUE.getValue());
				error.setMessage("Value less than avg delta value");
				
				String averageValueString = averageValue+"";
				if(numberField.getMetric() > 0) {
					averageValue = (double) UnitsUtil.convertToDisplayUnit(averageValueString, numberField);
					averageValueString  = averageValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
				} 
				
				error.setAverageValue(averageValueString);
				
				String currentValueString = currentTask.getInputValue()+"";
				
				Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
				
				if(currentInputUnit != null) {
					currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
				}
				
				error.setCurrentValue(currentValueString);
				
				return error;
			}
			if(currentDelta >= averageHigherLimit) {
				
				TaskErrorContext error = new TaskErrorContext();
				error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
				error.setSuggestionType(TaskErrorContext.SuggestionType.GREATER_THAN_AVG_VALUE.getValue());
				error.setMessage("Value Greater than avg delta value");
				
				String averageValueString = averageValue+"";
				if(numberField.getMetric() > 0) {
					averageValue = (double) UnitsUtil.convertToDisplayUnit(averageValueString, numberField);
					averageValueString  = averageValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
				} 
				
				error.setAverageValue(averageValueString);
				
				String currentValueString = currentTask.getInputValue()+"";
				
				Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
				
				if(currentInputUnit != null) {
					currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
				}
				
				error.setCurrentValue(currentValueString);
				
				return error;
			}
		}
		 return null;
	}

	private TaskErrorContext checkUnitForValueError(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm,Double currentValueInSiUnit) throws Exception {
		double value = FacilioUtil.parseDouble(rdm.getValue());
		double currentvalue = FacilioUtil.parseDouble(currentTask.getInputValue());
		
		double diff = value / currentvalue;
		
		Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
		
		Unit suggestedUnit = UnitsUtil.getUnitMultiplierResult(currentInputUnit, diff);
		
		if(suggestedUnit != null) {
			
			TaskErrorContext error = new TaskErrorContext();
			
			error.setSuggestedUnit(suggestedUnit);
			error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
			error.setSuggestionType(TaskErrorContext.SuggestionType.UNIT_CHANGE.getValue());
			error.setMessage("You might have to check your unit");
			
			double previousValue = FacilioUtil.parseDouble(rdm.getValue());
			String previousValueString = previousValue+"";
			if(numberField.getMetric() > 0) {
				previousValue = (double) UnitsUtil.convertToDisplayUnit(previousValue, numberField);
				previousValueString  = previousValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			} 
			
			error.setPreviousValue(previousValueString);
			
			String currentValueString = currentTask.getInputValue()+"";
			
			if(currentInputUnit != null) {
				currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
			}
			
			error.setCurrentValue(currentValueString);
			
			return error;
		}
		return null;
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
			
			Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
			
			if(currentInputUnit != null) {
				currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
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
