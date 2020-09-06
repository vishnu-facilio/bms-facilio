package com.facilio.bmsconsole.commands;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext.Status;
import com.facilio.bmsconsole.jobs.ScheduledFormulaCalculatorJob;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.FormulaFieldDependenciesAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class FormulaFieldJobCalculationCommand extends FacilioCommand implements PostTransactionCommand{

	private static final Logger LOGGER = LogManager.getLogger(FormulaFieldJobCalculationCommand.class.getName());
	
	private long formulaResourceId;
	private FormulaFieldResourceStatusContext formulaResourceStatusContext = null;
	private List<Integer> frequencyTypes;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	
	private boolean timedOut = false;
	private boolean currentStatusUpdate = false;
	
	public boolean executeCommand(Context context) throws Exception {
		
		formulaResourceId = (long) context.get(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID);
		frequencyTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES);
		
		long jobStartTime = System.currentTimeMillis();
			
		if(formulaResourceId != -1)
		{				
			formulaResourceStatusContext = FormulaFieldResourceStatusAPI.getFormulaFieldResourceStatusById(formulaResourceId);
			List<Long> childDependencyIds = FormulaFieldDependenciesAPI.getFormulaFieldDependencyIdsByParentFormula(formulaResourceId);
				
			if(formulaResourceStatusContext != null && formulaResourceStatusContext.getStatus() == FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal())
			{			
				formulaResourceStatusContext.setActualStartTime(DateTimeUtil.getCurrenTime());	
				long childCompletedCount = FormulaFieldResourceStatusAPI.getCompletedFormulaFieldResourceStatusCountByIds(childDependencyIds);
					
				if((int)childCompletedCount == childDependencyIds.size())
				{
					long formulaId = formulaResourceStatusContext.getFormulaFieldId();
					FormulaFieldContext formula = FormulaFieldAPI.getActiveFormulaField(formulaId, true);
					if(formula != null && !timedOut)
					{							
						formulaResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.IN_PROGRESS.getIntVal());
						int rowsUpdated = NewTransactionService.newTransactionWithReturn(() -> FormulaFieldResourceStatusAPI.updateCompletedFormulaFieldResourceStatus(formulaResourceStatusContext));	
						if (rowsUpdated == 1)
						{
							currentStatusUpdate = true;
							formulaResourceStatusContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
							System.out.println("Entered for formulaResourceId -- "+formulaResourceId);
							calculateScheduledFormula(formula,formulaResourceStatusContext);
							LOGGER.info("Time taken for FormulaFieldExecution job : " +(System.currentTimeMillis() - jobStartTime) + " with jobId: " +formulaResourceId);								
						}
						else {
							System.out.println("Failed at rowsUpdate -- "+formulaResourceId);
						}
					}			
				}
				else {			
					System.out.println("Failed at parentTrigger mismatch -- "+formulaResourceId);
				}
			}	
		}												
			
		return false;
	}
	
	public boolean postExecute() throws Exception {
		
		if(formulaResourceId != -1 && formulaResourceStatusContext != null && currentStatusUpdate)
		{
			formulaResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal());
			formulaResourceStatusContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			FormulaFieldResourceStatusAPI.updateInProgressFormulaFieldResourceStatus(formulaResourceStatusContext);
			
			List<Long> parentFormulaResourceIds = FormulaFieldDependenciesAPI.getFormulaFieldResourceParentIdsByDependentFormula(formulaResourceId);
			if(parentFormulaResourceIds != null && !parentFormulaResourceIds.isEmpty())
			{
				List<Long> typedParentFormulaResourceStatusIds = FormulaFieldResourceStatusAPI.getFormulaFieldResourceStatusIdsByFrequencyAndIds(parentFormulaResourceIds, frequencyTypes);
				if(typedParentFormulaResourceStatusIds != null && !typedParentFormulaResourceStatusIds.isEmpty())
				{					
					for(Long typedParentFormulaResourceStatusId :typedParentFormulaResourceStatusIds)
					{
						System.out.println("Triggering parents for --"+ formulaResourceId + " parent --" +typedParentFormulaResourceStatusId);	
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID, typedParentFormulaResourceStatusId);
						context.put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, frequencyTypes);
						FacilioTimer.scheduleInstantJob("FormulaFieldCalculatorJob", context);
					}	
				}				
			}				
		}	
		return false;
	}
	
	public void calculateScheduledFormula(FormulaFieldContext formula, FormulaFieldResourceStatusContext formulaResourceStatusContext) throws Exception {
		
		try {		
			long endTime = getEndTime(formula);				
			long resourceId = formulaResourceStatusContext.getResourceId();
			ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, formula.getReadingField());
			long startTime = getStartTime(formula, meta.getTtime());
			List<ReadingContext> readings = new ArrayList<>();
			
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(formula.getFrequencyEnum());
			List<DateRange> intervals = new ArrayList<DateRange>();
			if(schedule != null) {
				intervals = schedule.getTimeIntervals(startTime, endTime);
			}
			else {
				intervals= DateTimeUtil.getTimeIntervals(startTime, endTime, formula.getInterval());
			}

			//check ignoreNullValues
			List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), true, false);	
			if (currentReadings != null && !currentReadings.isEmpty()) {
				readings.addAll(currentReadings);
			}
	
			if (!readings.isEmpty()) {
				FacilioChain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
				FacilioContext context = addReadingChain.getContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
				context.put(FacilioConstants.ContextNames.READINGS, readings);
				context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);	
				addReadingChain.execute();					
			}	
		}
		catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
			CommonCommandUtil.emailException("FormulaFieldCalculatorJob", "Formula Scheduled Calculation failed for formula: " +formula.getId()+ "with resourceId: " +formulaResourceStatusContext.getResourceId()+ "and jobId: " +formulaResourceStatusContext.getId(), e);
		}		
	}
	
	private void fetchFields (FormulaFieldContext formula, ModuleBean modBean) throws Exception {	
		List<Long> dependentIds = formula.getWorkflow().getDependentFieldIds();
		if (dependentIds != null && !dependentIds.isEmpty()) {
			List<FacilioField> fields = new ArrayList<>();
			for (Long fieldId : dependentIds) {
				fields.add(modBean.getField(fieldId));
			}
			formula.getWorkflow().setDependentFields(fields);
		}	
	}
	
	private long getStartTime (FormulaFieldContext formula, long lastReadingTime) {
		ZonedDateTime zdt = null;
		if (formula.getFrequencyEnum() == FacilioFrequency.TEN_MINUTES) {
			int greaterRoundedMinute = FormulaFieldAPI.getRoundedMinute(DateTimeUtil.getDateTime(lastReadingTime).getMinute(), 10) + 10;
			int plusMinutes = greaterRoundedMinute - DateTimeUtil.getDateTime(lastReadingTime).getMinute();
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusMinutes(plusMinutes).truncatedTo(ChronoUnit.MINUTES);
		}
		else if (formula.getFrequencyEnum() == FacilioFrequency.FIFTEEN_MINUTES) {
			int greaterRoundedMinute = FormulaFieldAPI.getRoundedMinute(DateTimeUtil.getDateTime(lastReadingTime).getMinute(), 15) + 15;
			int plusMinutes = greaterRoundedMinute - DateTimeUtil.getDateTime(lastReadingTime).getMinute();
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusMinutes(plusMinutes).truncatedTo(ChronoUnit.MINUTES);
		}
		else if (formula.getFrequencyEnum() == FacilioFrequency.HOURLY) {
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusHours(1).truncatedTo(ChronoUnit.HOURS);
		}
		else {
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusDays(1).truncatedTo(ChronoUnit.DAYS);
		}
		return DateTimeUtil.getMillis(zdt, true);
	}
	
	private long getEndTime(FormulaFieldContext formula) {
		
		ZonedDateTime zdt = DateTimeUtil.getDateTime();
		int currentMinute = zdt.getMinute();
		int roundedMinute = FormulaFieldAPI.getRoundedMinute(currentMinute, 5);
		int minusMinute = currentMinute - roundedMinute;
		
		if (formula.getFrequencyEnum() == FacilioFrequency.TEN_MINUTES) {
			zdt = DateTimeUtil.getDateTime().plusMinutes(-minusMinute).truncatedTo(ChronoUnit.MINUTES);
			return DateTimeUtil.getMillis(zdt, true);
		}
		else if (formula.getFrequencyEnum() == FacilioFrequency.FIFTEEN_MINUTES) {
			zdt = DateTimeUtil.getDateTime().plusMinutes(-minusMinute).truncatedTo(ChronoUnit.MINUTES);
			return DateTimeUtil.getMillis(zdt, true);
		}
		else {
			return DateTimeUtil.getHourStartTime();
		}
	}
	
	private boolean isCalculatable(FormulaFieldContext formula, List<Long> calculatedFieldIds) throws Exception {
		if (formula.getWorkflow().getDependentFields() != null && !formula.getWorkflow().getDependentFields().isEmpty()) {
			for(FacilioField field : formula.getWorkflow().getDependentFields()) {
				if (field.getModule().getTypeEnum() == ModuleType.SCHEDULED_FORMULA && !calculatedFieldIds.contains(field.getFieldId())) {
					return false;
				}
			}
		}
		return true;
	}
}
