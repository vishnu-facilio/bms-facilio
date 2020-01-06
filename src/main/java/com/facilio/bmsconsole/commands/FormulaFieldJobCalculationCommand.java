package com.facilio.bmsconsole.commands;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
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
import com.facilio.fw.BeanFactory;
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
			List<FormulaFieldDependenciesContext> childDependencies = FormulaFieldDependenciesAPI.getFormulaFieldDependencyByParentFormula(formulaResourceId);
				
			if(formulaResourceStatusContext != null)
			{						
				List<Long> childFormulaResourceIds = null;
				if(childDependencies != null || !childDependencies.isEmpty())
				{
					System.out.println("Entering1 for formulaResourceId -- "+formulaResourceId);
					childFormulaResourceIds = childDependencies.stream().map(childDependency -> childDependency.getDependentFormulaResourceId()).filter(childDependency -> childDependency != null).collect(Collectors.toList());
					List<FormulaFieldResourceStatusContext> childFormulaResourceStatusList = FormulaFieldResourceStatusAPI.getActiveFormulaFieldResourceStatusByIds(childFormulaResourceIds);
					
					if(childFormulaResourceStatusList == null || childFormulaResourceStatusList.isEmpty())
					{
						System.out.println("Entering2 for formulaResourceId -- "+formulaResourceId);
						long formulaId = formulaResourceStatusContext.getFormulaFieldId();
						FormulaFieldContext formula = FormulaFieldAPI.getActiveFormulaField(formulaId, true);
						if(formula != null && !timedOut)
						{	
							System.out.println("Entering for formulaResourceId -- "+formulaResourceId);	
							formulaResourceStatusContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
							calculateScheduledFormula(formula,formulaResourceStatusContext);
							currentStatusUpdate = true;
							LOGGER.info("Time taken for FormulaFieldExecution job : " +(System.currentTimeMillis() - jobStartTime) + " with jobId: " +formulaResourceId);
							System.out.println("Time taken for FormulaFieldExecution job : " +(System.currentTimeMillis() - jobStartTime) + " with jobId: " +formulaResourceId);
						}			
					}
					else
					{			
						System.out.println("Failing for formulaResourceId --"+formulaResourceId);		
					}
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
			FormulaFieldResourceStatusAPI.updateFormulaFieldResourceStatus(formulaResourceStatusContext);
			
			List<FormulaFieldDependenciesContext> parentDependentFormulaeList = FormulaFieldDependenciesAPI.getFormulaFieldDependencyByDependentFormula(formulaResourceId);
			if(parentDependentFormulaeList != null && !parentDependentFormulaeList.isEmpty())
			{
				List<Long> parentFormulaResourceIds = parentDependentFormulaeList.stream().map(parentFormulaDependency -> parentFormulaDependency.getParentFormulaResourceId()).filter(parentFormulaDependency -> parentFormulaDependency != null).collect(Collectors.toList());
				List<FormulaFieldResourceStatusContext> typedParentFormulaResourceStatusList = FormulaFieldResourceStatusAPI.getFormulaFieldResourceStatusByFrequencyAndIds(parentFormulaResourceIds, frequencyTypes);
				
				if(typedParentFormulaResourceStatusList != null && !typedParentFormulaResourceStatusList.isEmpty())
				{
					System.out.println("Triggering parents formulaResourceId --"+typedParentFormulaResourceStatusList);	
					List<Long> typedParentFormulaResourceStatusIds = new ArrayList<Long>();	
					for(FormulaFieldResourceStatusContext typedParentFormulaResourceStatusContext: typedParentFormulaResourceStatusList)
					{
						typedParentFormulaResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.IN_PROGRESS.getIntVal());
						typedParentFormulaResourceStatusContext.setActualStartTime(DateTimeUtil.getCurrenTime());
						FormulaFieldResourceStatusAPI.updateFormulaFieldResourceStatus(typedParentFormulaResourceStatusContext);
						typedParentFormulaResourceStatusIds.add(typedParentFormulaResourceStatusContext.getId());
					}
					
					for(Long typedParentFormulaResourceStatusId :typedParentFormulaResourceStatusIds)
					{
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long endTime = DateTimeUtil.getHourStartTime();	
			fetchFields(formula, modBean);
			
			long resourceId = formulaResourceStatusContext.getId();
			ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, formula.getReadingField());
			long startTime = getStartTime(formula, meta.getTtime());
			List<ReadingContext> readings = new ArrayList<>();
			
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(formula.getFrequencyEnum());
			List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);
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
		if (formula.getFrequencyEnum() == FacilioFrequency.HOURLY) {
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusHours(1).truncatedTo(ChronoUnit.HOURS);
		}
		else {
			zdt = DateTimeUtil.getDateTime(lastReadingTime).plusDays(1).truncatedTo(ChronoUnit.DAYS);
		}
		return DateTimeUtil.getMillis(zdt, true);
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
	
	private List<Integer> getFrequencyTypesToBeFetched() {
		List<Integer> types = new ArrayList<Integer>();
		types.add(FacilioFrequency.HOURLY.getValue());
		
		ZonedDateTime zdt = DateTimeUtil.getDateTime();
		
		if (zdt.getHour() == 0) {
			types.add(FacilioFrequency.DAILY.getValue());
			if (zdt.getDayOfWeek() == DateTimeUtil.getWeekFields().getFirstDayOfWeek()) {
				types.add(FacilioFrequency.WEEKLY.getValue());
			}
			
			if (zdt.getDayOfMonth() == 1) {
				types.add(FacilioFrequency.MONTHLY.getValue());
				
				if (zdt.getMonth() == Month.JANUARY) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
					types.add(FacilioFrequency.HALF_YEARLY.getValue());
					types.add(FacilioFrequency.ANNUALLY.getValue());
				}
				else if (zdt.getMonth() == Month.JULY) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
					types.add(FacilioFrequency.HALF_YEARLY.getValue());
				}
				else if (zdt.getMonth() == Month.APRIL || zdt.getMonth() == Month.OCTOBER) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
				}
			}
		}
		return types;
	}
}
