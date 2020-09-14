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

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext.Status;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.jobs.ScheduledFormulaCalculatorJob;
import com.facilio.bmsconsole.jobs.SingleResourceHistoricalFormulaCalculatorJob;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.FormulaFieldDependenciesAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.bmsconsole.util.MarkingUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;

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
		
		try {
			formulaResourceId = (long) context.get(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID);
			frequencyTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES);
			
			long jobStartTime = System.currentTimeMillis();
				
			if(formulaResourceId != -1)
			{				
				formulaResourceStatusContext = FormulaFieldResourceStatusAPI.getFormulaFieldResourceStatusById(formulaResourceId);
				List<Long> childDependencyIds = FormulaFieldDependenciesAPI.getFormulaFieldDependencyIdsByParentFormula(formulaResourceId);
					
				if(formulaResourceStatusContext != null && formulaResourceStatusContext.getStatus() == FormulaFieldResourceStatusContext.Status.IN_QUEUE.getIntVal())
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
							int rowsUpdated = NewTransactionService.newTransactionWithReturn(() -> FormulaFieldResourceStatusAPI.updateInqueueFormulaFieldResourceStatus(formulaResourceStatusContext));	
							if (rowsUpdated == 1)
							{
								currentStatusUpdate = true;
								formulaResourceStatusContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
								LOGGER.info("Entered for formulaResourceId -- "+formulaResourceId + " rowsUpdated: "+rowsUpdated+ " childCompletedCount: "+childCompletedCount+" childDependencyIds.size() "+childDependencyIds.size());
								calculateScheduledFormula(formula,formulaResourceStatusContext);
								LOGGER.info("Time taken for FormulaFieldExecution job : " +(System.currentTimeMillis() - jobStartTime) + " with jobId: " +formulaResourceId);								
							}
							else {
								LOGGER.info("Failed at rowsUpdate -- "+formulaResourceId);
							}
						}			
					}
					else {			
						LOGGER.info("Failed at parentTrigger mismatch -- "+formulaResourceId);
					}
				}	
			}	
		}
		catch (Exception formulaFieldException) {
			exceptionMessage = formulaFieldException.getMessage();
			stack = formulaFieldException.getStackTrace();
			throw formulaFieldException;
		}
			
		return false;
	}
	
	public boolean postExecute() throws Exception {
		
		try {
			if(formulaResourceId != -1 && formulaResourceStatusContext != null && currentStatusUpdate)
			{
				FacilioChain chain = TransactionChainFactory.getScheduleFormulaFieldParentJobCommand();
				chain.getContext().put(FacilioConstants.ContextNames.FORMULA_RESOURCE, formulaResourceStatusContext);
				chain.getContext().put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, frequencyTypes);
				chain.execute();	
			}	
		}	
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Formula Resource Calculation Job Exception Handling Failed",
					"PostExecute - Error occurred while doing Formula Resource Calculation Job for orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", formulaResourceId -- " +formulaResourceId, e);
			LOGGER.error("PostExecute - Formula Resource Calculation Job Exception Handling Failed for formulaResourceId : "+formulaResourceId+ " Exception: "+ e);
		}
		return false;
	}
	
	public void onError() throws Exception {
		constructErrorMessage();
	}
	
	public void constructErrorMessage() throws Exception 
	{
		try {
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) {
				mailExp.setStackTrace(stack);
			}
			
			CommonCommandUtil.emailException(SingleResourceHistoricalFormulaCalculatorJob.class.getName(), "Error occurred while doing Formula Resource Calculation Job for formulaResourceId : "+formulaResourceId, mailExp); 
			LOGGER.error("Error occurred while doing Formula Resource Calculation Job for formulaResourceId : "+formulaResourceId+ " ExceptionMessage: "+ exceptionMessage);
			
			if(formulaResourceStatusContext == null) {
				formulaResourceStatusContext = FormulaFieldResourceStatusAPI.getFormulaFieldResourceStatusById(formulaResourceId);
			}
			if(formulaResourceId != -1 && formulaResourceStatusContext != null && currentStatusUpdate)
			{
				FacilioChain chain = TransactionChainFactory.getScheduleFormulaFieldParentJobCommand();
				chain.getContext().put(FacilioConstants.ContextNames.FORMULA_RESOURCE, formulaResourceStatusContext);
				chain.getContext().put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, frequencyTypes);
				chain.execute();
				
			}				
		}
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Formula Resource Calculation Job Exception Handling Failed",
					"OnError - Error occurred while doing Formula Resource Calculation Job for orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", formulaResourceId -- " +formulaResourceId, e);
			LOGGER.error("OnError - Formula Resource Calculation Job Exception Handling Failed for formulaResourceId : "+formulaResourceId+ " Exception: "+ e);
		}
	}
	
	
	public void calculateScheduledFormula(FormulaFieldContext formula, FormulaFieldResourceStatusContext formulaResourceStatusContext) throws Exception {
		
		try {		
			long resourceId = formulaResourceStatusContext.getResourceId();
			ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, formula.getReadingField());
			long startTime = getStartTime(formula, meta.getTtime());
			long endTime = getEndTime(formula);	
			boolean calculateVMDeltaThroughFormula = false;
			
			if(formula.getFormulaFieldTypeEnum() == FormulaFieldType.VM && formula.getReadingField().getModule().getName().equals((FacilioConstants.ContextNames.ENERGY_DATA_READING)) && formula.getReadingField().getName().equals("totalEnergyConsumptionDelta")) {
				formula.getWorkflow().setFetchMarkedReadings(true);
				calculateVMDeltaThroughFormula = true;
			}
			FormulaFieldAPI.computeFormulaResourceReadings(formula, resourceId, startTime, endTime, false, calculateVMDeltaThroughFormula);	
		}
		catch (Exception e) {
			LOGGER.info("Exception occurred in Formula Scheduled Calculation", e);
			CommonCommandUtil.emailException("FormulaFieldCalculatorJob", "ScheduledFormulaField Calculation failed for formula: " +formula.getId()+ "with resourceId: " +formulaResourceStatusContext.getResourceId()+ "and jobId: " +formulaResourceStatusContext.getId(), e);
			throw e;
		}		
	}
	
	private long getStartTime (FormulaFieldContext formula, long lastReadingTime) {
		ZonedDateTime zdt = null;
//		if (formula.getFrequencyEnum() == FacilioFrequency.TEN_MINUTES) {
//			int greaterRoundedMinute = FormulaFieldAPI.getRoundedMinute(DateTimeUtil.getDateTime(lastReadingTime).getMinute(), 10) + 10;
//			int plusMinutes = greaterRoundedMinute - DateTimeUtil.getDateTime(lastReadingTime).getMinute();
//			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusMinutes(plusMinutes).truncatedTo(ChronoUnit.MINUTES);
//		}
//		else if (formula.getFrequencyEnum() == FacilioFrequency.FIFTEEN_MINUTES) {
//			int greaterRoundedMinute = FormulaFieldAPI.getRoundedMinute(DateTimeUtil.getDateTime(lastReadingTime).getMinute(), 15) + 15;
//			int plusMinutes = greaterRoundedMinute - DateTimeUtil.getDateTime(lastReadingTime).getMinute();
//			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusMinutes(plusMinutes).truncatedTo(ChronoUnit.MINUTES);
//		}
		
		if (formula.getFrequencyEnum() == FacilioFrequency.TEN_MINUTES || formula.getFrequencyEnum() == FacilioFrequency.FIFTEEN_MINUTES) {			
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusSeconds(formula.getInterval() * 60).truncatedTo(new SecondsChronoUnit(formula.getInterval() * 60));
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
		
		if (formula.getFrequencyEnum() == FacilioFrequency.TEN_MINUTES || formula.getFrequencyEnum() == FacilioFrequency.FIFTEEN_MINUTES) {
//			zdt = DateTimeUtil.getDateTime().plusMinutes(-minusMinute).truncatedTo(ChronoUnit.MINUTES);
//			return DateTimeUtil.getMillis(zdt, true);
			
			return (DateTimeUtil.getDateTime(System.currentTimeMillis()).truncatedTo(new SecondsChronoUnit(formula.getInterval() * 60)).toInstant().toEpochMilli()) - 1;	
		}
		else {
			return DateTimeUtil.getHourStartTime();
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
