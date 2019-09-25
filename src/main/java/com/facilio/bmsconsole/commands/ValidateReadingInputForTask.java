package com.facilio.bmsconsole.commands;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskErrorContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class ValidateReadingInputForTask extends FacilioCommand {

	
	public static final int noOfDaysDeltaToBeFetched = 10;
	
	public final static int averageboundPercentage = 50; 
	
	long taskContextId, currentInputTime;
	String currentInputValue;
	
	private static final Logger LOGGER = Logger.getLogger(ValidateReadingInputForTask.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
						
			if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION)) {
				return false;
			}
			
			Boolean skipValidation = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_VALIDATION);
			
			skipValidation = skipValidation == null ? Boolean.FALSE : skipValidation;  
			
			if(skipValidation)
			{
				TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
				List<TaskErrorContext> errors = new ArrayList<TaskErrorContext>();
				boolean hasErrors = false;
				if(currentTask != null && currentTask.getInputValue() != null) {
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
										
										taskContextId = currentTask.getId();
										ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
										NumberField numberField = (NumberField) taskContext.getReadingField();
										
										if(rdm != null && rdm.getValue() != null && rdm.getValue().equals("-1.0")) {
											return false;
										}
										
										if(currentTask.getInputValue().trim().isEmpty())
										{
											return false;
										}
								
										currentInputValue = currentTask.getInputValue();
										currentInputTime = currentTask.getInputTime();
										Double currentValue = FacilioUtil.parseDouble(currentTask.getInputValue());
										
										Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);	
										Double currentValueInSiUnit = UnitsUtil.convertToSiUnit(currentValue, currentInputUnit);
										
										if(numberField.isCounterField() || (numberField.getName().equals("totalEnergyConsumption") && numberField.getModule().getName().equals("energydata"))) 
										{
											List<TaskErrorContext> taskErrors = checkIncremental(currentTask,numberField,rdm,currentValueInSiUnit,taskContext);
											if(taskErrors!= null && !taskErrors.isEmpty()) {
												hasErrors = true;
												errors.addAll(taskErrors);
												TaskErrorContext unitSuggestion = checkUnitForValueError(currentTask,numberField,rdm,currentValueInSiUnit,taskContext);
												if(unitSuggestion != null && errors.size() == 1) {
													errors.add(unitSuggestion);
												}
											}
											else {
												TaskErrorContext taskError = checkValueRangeForCounterField(currentTask,numberField,rdm,currentValueInSiUnit,taskContext);
												if(taskError!= null) {
													errors.add(taskError);
													TaskErrorContext unitSuggestion = checkUnitForValueError(currentTask,numberField,rdm,currentValueInSiUnit,taskContext);
													if(unitSuggestion != null) {
														errors.add(unitSuggestion);
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
					LOGGER.log(Level.INFO, "Currenttask ID: "+ taskContextId + " Current Input Value: " + currentInputValue + 
							" Current Input Time: " + currentInputTime +" Task Errors: "+ errors);
//					if(hasErrors) {
						return true;
//					}
				}
				
			}
			
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Currenttask ID: "+ taskContextId + " Current Input Value: " + currentInputValue + 
					" Current Input Time: " + currentInputTime + " " + e.getMessage() , e);
		}
		return false;
	}

	private List<TaskErrorContext> checkIncremental(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm, Double currentValueInSiUnit, TaskContext taskContext) throws Exception {

		List<TaskErrorContext> taskErrors = new ArrayList<TaskErrorContext>();
		double previousValue = -1, nextValue = -1;
		boolean futureCase = false;
		if(currentTask.getInputTime() > rdm.getTtime() && taskContext.getReadingDataId() != rdm.getReadingDataId()) 
		{
			previousValue = FacilioUtil.parseDouble(rdm.getValue());
		}
		else if(taskContext.getReadingDataId() != -1 && rdm.getReadingDataId()!= -1 && 
				taskContext.getReadingDataId() == rdm.getReadingDataId())
		{				
			previousValue = getLatestInputReading(numberField, rdm, currentTask, "TTIME DESC", rdm.getTtime(), NumberOperators.LESS_THAN);	
		}
		else 
		{
			futureCase = true;
			previousValue = getLatestInputReading(numberField, rdm, currentTask, "TTIME DESC", currentTask.getInputTime(), NumberOperators.LESS_THAN);
			nextValue =	getLatestInputReading(numberField, rdm, currentTask, "TTIME ASC", (currentTask.getInputTime()+1000), NumberOperators.GREATER_THAN);
		}	
		
		if(previousValue < 0 && nextValue < 0)
			return null;
		
		if(currentValueInSiUnit < previousValue) 
		{
			TaskErrorContext error = setIncrementalErrorMode(currentTask, numberField, rdm);
			String previousValueString = previousValue+"";	
			
			if(numberField.getMetric() > 0) {
				previousValue = (double) UnitsUtil.convertToDisplayUnit(previousValue, numberField);
				
				String previousValueInString = WorkflowUtil.getStringValueFromDouble(previousValue);
				previousValueString  = (previousValueInString != null ? previousValueInString : previousValue) + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			}	
			error.setPreviousValue(previousValueString);
			
			error.setMessage("The reading you have entered is less than the previous reading of " + error.getPreviousValue() +".");
			taskErrors.add(error);
		}
		
		if(futureCase && nextValue > 0 && currentValueInSiUnit > nextValue) 
		{		
			TaskErrorContext error = setIncrementalErrorMode(currentTask, numberField, rdm);
			String nextValueString = nextValue+"";
			if(numberField.getMetric() > 0) {
				nextValue = (double) UnitsUtil.convertToDisplayUnit(nextValue, numberField);
				
				String nextValueInString = WorkflowUtil.getStringValueFromDouble(nextValue);			 
				nextValueString  = (nextValueInString != null ? nextValueInString : nextValue) + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			} 
			error.setNextValue(nextValueString);
	        
			error.setMessage("The reading you have entered is greater than the next reading of " + error.getNextValue() + ", in this series.");
			taskErrors.add(error);
		}
		
		return taskErrors;
	}

	private TaskErrorContext checkValueRangeForCounterField(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm,Double currentValueInSiUnit, TaskContext taskContext) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		double previousValue = getLatestPreviousReading (numberField, rdm, currentTask, taskContext);	
		if(previousValue < 0)
		{
			return null;
		}
		
		double currentValue = FacilioUtil.parseDouble(currentTask.getInputValue());
		double currentDelta = currentValueInSiUnit-previousValue;
		
		Double averageValue = getAverageValue(rdm, (NumberField)modBean.getField(numberField.getName()+"Delta", numberField.getModule().getName()), currentTask, taskContext);
		
		if(averageValue != null && averageValue > 0 && numberField.getMetric() > 0) {
			
			Double averageLowerLimit = averageValue - (averageValue * averageboundPercentage /100);
			Double averageHigherLimit = averageValue + (averageValue * averageboundPercentage /100);
			
			double averageLowerLimitEnergyReading = previousValue + averageLowerLimit;
			double averageHigherLimitEnergyReading = previousValue + averageHigherLimit;
			
			double averageLowerLimitInDisplayUnit  = (double)UnitsUtil.convertToDisplayUnit(averageLowerLimitEnergyReading, numberField);
			double averageHigherLimitInDisplayUnit  = (double)UnitsUtil.convertToDisplayUnit(averageHigherLimitEnergyReading, numberField);
					
			String averageLowerLimitString = WorkflowUtil.getStringValueFromDouble(FacilioUtil.decimalClientFormat(averageLowerLimitInDisplayUnit));
			String averageHigherLimitString = WorkflowUtil.getStringValueFromDouble(FacilioUtil.decimalClientFormat(averageHigherLimitInDisplayUnit));
			
			if(currentDelta <= averageLowerLimit) {
				
				TaskErrorContext error = new TaskErrorContext();
				error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
				error.setSuggestionType(TaskErrorContext.SuggestionType.LESS_THAN_AVG_VALUE.getValue());
				
				Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);	
				error.setCurrentValue(setCurrentValueString(currentTask, currentInputUnit));
				error.setAverageValue(setAverageValueString(averageValue, numberField));
				
				if(averageLowerLimitString != null && averageHigherLimitString != null )
				{
					error.setMessage("The reading you have entered " +error.getCurrentValue()+ " is not within the expected range of " 
							+ averageLowerLimitString + " - " 
							+ averageHigherLimitString + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol());	
				}
				else
				{
					error.setMessage("The reading you have entered is less than the average delta value of "+ error.getAverageValue() +".");
				}	
				
				return error;
			}
			if(currentDelta >= averageHigherLimit) {
				
				TaskErrorContext error = new TaskErrorContext();
				error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
				error.setSuggestionType(TaskErrorContext.SuggestionType.GREATER_THAN_AVG_VALUE.getValue());
					
				Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
				error.setCurrentValue(setCurrentValueString(currentTask, currentInputUnit));
				error.setAverageValue(setAverageValueString(averageValue, numberField));
				
				if(averageLowerLimitString != null && averageHigherLimitString != null )
				{
					error.setMessage("The reading you have entered " +error.getCurrentValue()+ " is not within the expected range of " 
							+ averageLowerLimitString + " - " 
							+ averageHigherLimitString + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol());			
				}
				else
				{
					error.setMessage("The reading you have entered is greater than the average delta value of " + error.getAverageValue() +".");
				}
				
				return error;
			}
		}
		 return null;
	}

	private TaskErrorContext checkUnitForValueError(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm,Double currentValueInSiUnit, TaskContext taskContext) throws Exception {

		double previousValue = getLatestPreviousReading (numberField, rdm, currentTask, taskContext);	
		if(previousValue < 0)
		{
			return null;
		}
		
		double currentValue = FacilioUtil.parseDouble(currentTask.getInputValue());
		double diff = previousValue / currentValueInSiUnit;
		
		Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
		Unit suggestedUnit = UnitsUtil.getUnitMultiplierResult(currentInputUnit, diff);
		
		if(suggestedUnit != null) {
			
			TaskErrorContext error = new TaskErrorContext();		
			error.setMode(TaskErrorContext.Mode.SUGGESTION.getValue());
			error.setSuggestionType(TaskErrorContext.SuggestionType.UNIT_CHANGE.getValue());
			error.setCurrentValue(setCurrentValueString(currentTask, currentInputUnit));
			error.setSuggestedUnit(suggestedUnit);
			error.setMessage("We suggest you to double check the unit you have chosen.");
			//It might be "+ Unit.valueOf(error.getSuggestedUnit()).getSymbol()+"."

			String previousValueString = previousValue+"";
			if(numberField.getMetric() > 0) {
				previousValue = (double) UnitsUtil.convertToDisplayUnit(previousValue, numberField);
				previousValueString  = previousValue + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
			} 
			error.setPreviousValue(previousValueString);
		
			return error;
		}
		return null;
	}
	
	public Double getAverageValue(ReadingDataMeta rdm,NumberField numberField, TaskContext currentTask, TaskContext taskContext) 
			throws Exception {
		
		if(rdm.getResourceId() > 0) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = numberField.getModule();			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
			
			DateRange lastNdays = DateOperators.LAST_N_DAYS.getRange(noOfDaysDeltaToBeFetched+"");
			long endTaskTime;
			
			if(currentTask.getInputTime() > rdm.getTtime() && taskContext.getReadingDataId() == rdm.getReadingDataId())
			{
				endTaskTime = rdm.getTtime();
			}
			else 
			{
				endTaskTime = currentTask.getInputTime();
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(module.getTableName())
					.module(module)
					.select(Collections.singletonList(numberField))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), rdm.getResourceId()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), lastNdays.getStartTime()+","+(endTaskTime-1), DateOperators.BETWEEN));
							
			List<Map<String, Object>> res = selectBuilder.getAsProps();
			if(res != null && !res.isEmpty()) { 			
				double sum=0, avg=0, n=0;
				for(Map<String, Object> prop: res)
				{
					if(prop.get(numberField.getName()) != null)
					{
						sum+=(double)prop.get(numberField.getName());
						n++;
					}			
				}
				if(n>7)
				{
					avg = sum/n;
					return avg;
				}
			}
		}
		LOGGER.log(Level.INFO,"Avg not calculated, Currenttask ID: "+ taskContextId + " Current Input Value: " + currentInputValue);
		return null;
	}
	
	
	public static Unit getCurrentInputUnit(ReadingDataMeta rdm,TaskContext currentTask,NumberField numberField) throws Exception {
		if(currentTask.getReadingFieldUnit() > 0) {
			return Unit.valueOf(currentTask.getReadingFieldUnit());
		}
		else if(rdm.getUnit() > 0) {
			return Unit.valueOf(rdm.getUnit());
		}
		return UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), numberField.getMetric());
	}
	
	public double getLatestPreviousReading(NumberField numberField, ReadingDataMeta rdm, TaskContext currentTask, TaskContext taskContext) throws Exception {
		
		double value = -1;
		if(currentTask.getInputTime() > rdm.getTtime() && taskContext.getReadingDataId() != rdm.getReadingDataId())
		{
			value = FacilioUtil.parseDouble(rdm.getValue());
		}
		else if(taskContext.getReadingDataId() != -1 && rdm.getReadingDataId()!= -1 &&  
				taskContext.getReadingDataId() == rdm.getReadingDataId())
		{				
			value = getLatestInputReading(numberField, rdm, currentTask, "TTIME DESC", rdm.getTtime(),NumberOperators.LESS_THAN);	
		}
		else 
		{
			value = getLatestInputReading(numberField, rdm, currentTask, "TTIME DESC", currentTask.getInputTime(), NumberOperators.LESS_THAN);
		}
		return value;
		
	}
	
	public double getLatestInputReading(NumberField numberField, ReadingDataMeta rdm, TaskContext currentTask, String OrderBy, long ttime, Operator<String> NumberOperator) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(Collections.singletonList(numberField))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(rdm.getResourceId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(ttime), NumberOperator))
				.orderBy(OrderBy).limit(1);
			
		ReadingContext readingContext = selectBuilder.fetchFirst();
		if (readingContext != null) {
			Double value = (double) readingContext.getReading(numberField.getName());
			return value;
		}
		return -1;
	}
	
	public static TaskErrorContext setIncrementalErrorMode(TaskContext currentTask, NumberField numberField, ReadingDataMeta rdm) throws Exception {
		
		TaskErrorContext error = new TaskErrorContext();
		error.setMode(TaskErrorContext.Mode.ERROR.getValue());
		error.setFailType(TaskErrorContext.FailType.NON_INCREMENTAL_VALUE.getValue());
		
		String currentValueString = currentTask.getInputValue()+"";
		
		Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);
		if(currentInputUnit != null) {
			currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
		}
		error.setCurrentValue(currentValueString);
		
		return error;
	}
	
	public static String setCurrentValueString(TaskContext currentTask, Unit currentInputUnit) throws Exception {	
		String currentValueString = currentTask.getInputValue()+"";
		if(currentInputUnit != null) {
			currentValueString = currentValueString + " "+currentInputUnit.getSymbol();
		}
		return currentValueString;
	}
	
	public static String setAverageValueString(Double averageValue, NumberField numberField) throws Exception {	
		
		String averageValueString = averageValue+"";
		if(numberField.getMetric() > 0) {
			double averageValueInDisplayUnit  = (double)UnitsUtil.convertToDisplayUnit(averageValue, numberField);
			
			String averageValueInString = WorkflowUtil.getStringValueFromDouble(FacilioUtil.decimalClientFormat(averageValueInDisplayUnit));			 
			averageValueString  = (averageValueInString != null ? averageValueInString : averageValueInDisplayUnit) + " " + UnitsUtil.getDisplayUnit(numberField).getSymbol();
		} 
		return averageValueString;
	}
		
}
