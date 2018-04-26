package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class HistoricalENPICalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long enpiId = jc.getJobId();
			EnergyPerformanceIndicatorContext enpi = EnergyPerformanceIndicatiorAPI.getENPI(enpiId);
			
			logger.log(Level.INFO, "Calculating EnPI for "+enpi.getName());
			
			long currentTime = DateTimeUtil.getCurrenTime(true);
			long startTime = EnergyPerformanceIndicatiorAPI.getStartTimeForHistoricalCalculation(enpi);
			ScheduleInfo schedule = getSchedule(enpi.getFrequencyEnum());
			long endTime = schedule.nextExecutionTime(startTime);
			
			List<ReadingContext> readings = new ArrayList<>();
			while (endTime <= currentTime) {
				try {
					ReadingContext reading = EnergyPerformanceIndicatiorAPI.calculateENPI(enpi, startTime*1000, (endTime*1000)-1);
					readings.add(reading);
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					if (e == null || !e.getMessage().contains("Division by zero") || !e.getMessage().contains("Division undefined")  || !e.getMessage().contains("/ by zero")) {
						CommonCommandUtil.emailException("EnPI calculation failed for : "+jc.getJobId()+" between "+startTime+" and "+endTime, e);
					}
				}
				startTime = endTime;
				endTime = schedule.nextExecutionTime(startTime);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
			
			Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReadingChain.execute(context);
			
			if (!readings.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fieldsList = modBean.getAllFields(enpi.getReadingField().getModule().getName());
				ReadingsAPI.updateLastReading(fieldsList, Collections.singletonList(readings.get(readings.size() - 1)), null);
			}
			
			List<EnergyPerformanceIndicatorContext> allEnPIs = EnergyPerformanceIndicatiorAPI.getAllENPIs();
			for (EnergyPerformanceIndicatorContext currentEnPI : allEnPIs) {
				if (currentEnPI.getId() != enpi.getId()) {
					List<FacilioField> dependentFields = currentEnPI.getDependentFields();
					if (dependentFields.contains(enpi.getReadingField())) {
						EnergyPerformanceIndicatiorAPI.recalculateHistoricalData(currentEnPI, currentEnPI.getReadingField());
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("Historical EnPI calculation failed for : "+jc.getJobId(), e);
		}
	}
	
	private ScheduleInfo getSchedule (FacilioFrequency frequency) {
		ScheduleInfo schedule = null;
		List<Integer> values = null;
		switch (frequency) {
		    case DAILY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.DAILY);
					return schedule;
			case WEEKLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.WEEKLY);
					values = new ArrayList<>();
					values.add(DateTimeUtil.getWeekFields().getFirstDayOfWeek().getValue());
					schedule.setValues(values);
					return schedule;
			case MONTHLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.MONTHLY_DAY);
					values = new ArrayList<>();
					values.add(1);
					schedule.setValues(values);
					return schedule;
			case QUARTERTLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					values.add(4);
					values.add(7);
					values.add(10);
					schedule.setValues(values);
					return schedule;
			case HALF_YEARLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					values.add(7);
					schedule.setValues(values);
					return schedule;
			case ANNUALLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					schedule.setValues(values);
					return schedule;
			default:
					return null;
		}
	}

}
