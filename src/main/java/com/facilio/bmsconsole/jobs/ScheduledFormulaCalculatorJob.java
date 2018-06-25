package com.facilio.bmsconsole.jobs;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledFormulaCalculatorJob extends FacilioJob {
	private static final Logger logger = LogManager.getLogger(ScheduledFormulaCalculatorJob.class.getName());
	private static Logger log = LogManager.getLogger(ScheduledFormulaCalculatorJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			List<Integer> types = getFrequencyTypesToBeFetched();
			logger.log(Level.INFO, "Frequencies to be fetched for Scheduled Formula Calculation : ");
			List<FormulaFieldContext> formulas = FormulaFieldAPI.getActiveScheduledFormulasOfFrequencyType(types);
			logger.log(Level.INFO, "Formulas to be calculated : "+formulas);
			List<Long> calculatedFieldIds = new ArrayList<>();
			if (formulas != null && !formulas.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				fetchFields(formulas, modBean);
				long endTime = DateTimeUtil.getHourStartTime();
				while (!formulas.isEmpty()) {
					Iterator<FormulaFieldContext> it = formulas.iterator();
					while (it.hasNext()) {
						FormulaFieldContext enpi = it.next();
						if(isCalculatable(enpi, calculatedFieldIds)) {
							try {
								List<ReadingContext> readings = new ArrayList<>();
								List<ReadingContext> lastReadings = new ArrayList<>();
								for (Long resourceId : enpi.getMatchedResources()) {
									ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, enpi.getReadingField());
									long lastReadingTime = meta.getTtime();
									ZonedDateTime zdt = DateTimeUtil.getDateTime(lastReadingTime).plusHours(1).truncatedTo(ChronoUnit.HOURS);
									long startTime = DateTimeUtil.getMillis(zdt, true);
									ScheduleInfo schedule = FormulaFieldAPI.getSchedule(enpi.getFrequencyEnum());
									List<Pair<Long, Long>> intervals = DateTimeUtil.getTimeIntervals(startTime, endTime, schedule);
									List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, enpi.getReadingField().getName(), intervals, enpi.getWorkflow());
									if (currentReadings != null && !currentReadings.isEmpty()) {
										readings.addAll(currentReadings);
										lastReadings.add(readings.get(readings.size() - 1));
									}
								}
								
								if (!readings.isEmpty()) {
									FacilioContext context = new FacilioContext();
									context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
									context.put(FacilioConstants.ContextNames.READINGS, readings);
									context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
									
									Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
									addReadingChain.execute(context);
									
									ReadingsAPI.updateReadingDataMeta(Collections.singletonList(enpi.getReadingField()), lastReadings, null);
								}
							}
							catch (Exception e) {
								log.info("Exception occurred ", e);
								CommonCommandUtil.emailException("ScheduledFormulaCalculatorJob", "EnPI Calculation failed for : "+enpi.getId()+" in org : "+jc.getOrgId(), e);
							}
							calculatedFieldIds.add(enpi.getReadingFieldId());
							it.remove();
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
			CommonCommandUtil.emailException("ScheduledFormulaCalculatorJobENPI", "EnPI Calculation job failed for orgid : "+jc.getOrgId(), e);
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
