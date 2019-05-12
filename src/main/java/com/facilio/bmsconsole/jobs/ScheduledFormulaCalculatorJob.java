package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.db.criteria.DateRange;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduledFormulaCalculatorJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(ScheduledFormulaCalculatorJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			List<Integer> types = getFrequencyTypesToBeFetched();
			LOGGER.log(Level.INFO, "Frequencies to be fetched for Scheduled Formula Calculation : "+types);
			List<FormulaFieldContext> formulas = FormulaFieldAPI.getActiveScheduledFormulasOfFrequencyType(types);
			LOGGER.log(Level.INFO, "Formulas to be calculated : "+formulas);
			List<Long> calculatedFieldIds = new ArrayList<>();
			if (formulas != null && !formulas.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				fetchFields(formulas, modBean);
				long endTime = DateTimeUtil.getHourStartTime();
				while (!formulas.isEmpty()) {
					Iterator<FormulaFieldContext> it = formulas.iterator();
					while (it.hasNext()) {
						FormulaFieldContext formula = it.next();
						if(isCalculatable(formula, calculatedFieldIds)) {
							if (AccountUtil.getCurrentOrg().getId() == 88) {
								LOGGER.info("Gonna execute scheduled formula : "+formula.getName());
							}
							try {
								List<ReadingContext> readings = new ArrayList<>();
								if (AccountUtil.getCurrentOrg().getId() == 186l) {
									LOGGER.info("Formula matched resources : "+StringUtils.join(formula.getMatchedResourcesIds(), ","));
								}
								for (Long resourceId : formula.getMatchedResourcesIds()) {
									ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, formula.getReadingField());
									long startTime = getStartTime(formula, meta.getTtime());
									ScheduleInfo schedule = FormulaFieldAPI.getSchedule(formula.getFrequencyEnum());
									List<DateRange> intervals = DateTimeUtil.getTimeIntervals(startTime, endTime, schedule);
									List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), true, false);
									
									if (AccountUtil.getCurrentOrg().getId() == 88) {
										LOGGER.info("Readings to be added for Formula : "+formula.getName()+" between the intervals ("+intervals+") is "+currentReadings);
									}
									
									if (currentReadings != null && !currentReadings.isEmpty()) {
										readings.addAll(currentReadings);
									}
								}
								
								if (!readings.isEmpty()) {
									FacilioContext context = new FacilioContext();
									context.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
									context.put(FacilioConstants.ContextNames.READINGS, readings);
									context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
									
									Chain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
									addReadingChain.execute(context);
									
								}
							}
							catch (Exception e) {
								LOGGER.info("Exception occurred ", e);
								CommonCommandUtil.emailException("ScheduledFormulaCalculatorJob", "EnPI Calculation failed for : "+formula.getId()+" in org : "+jc.getOrgId(), e);
							}
							calculatedFieldIds.add(formula.getReadingFieldId());
							it.remove();
						}
					}
				}
			}
			LOGGER.info("Time taken for ScheduledFormulaExecution job : "+(System.currentTimeMillis() - jobStartTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("Exception occurred ", e);
			CommonCommandUtil.emailException("ScheduledFormulaCalculatorJobENPI", "EnPI Calculation job failed for orgid : "+jc.getOrgId(), e);
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

	private void fetchFields (List<FormulaFieldContext> formulas, ModuleBean modBean) throws Exception {
		for (FormulaFieldContext formula : formulas) {
			List<Long> dependentIds = formula.getWorkflow().getDependentFieldIds();
			if (dependentIds != null && !dependentIds.isEmpty()) {
				List<FacilioField> fields = new ArrayList<>();
				for (Long fieldId : dependentIds) {
					fields.add(modBean.getField(fieldId));
				}
				formula.getWorkflow().setDependentFields(fields);
			}
		}
	}
}
