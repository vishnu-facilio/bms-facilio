package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.time.SecondsChronoUnit;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public class ScheduledRuleAPI extends WorkflowRuleAPI {
	protected static void validateScheduledRule(WorkflowRuleContext rule, boolean isUpdate) throws Exception {
		if (rule.getDateFieldId() == -1 && !isUpdate) {
			throw new IllegalArgumentException("Date Field Id cannot be null for Scheduled Rule");
		}
		
		if (rule.getScheduleTypeEnum() == null && !isUpdate) {
			throw new IllegalArgumentException("Schedule Type cannot be null for Scheduled Rule");
		}
		
		if (rule.getScheduleTypeEnum() != null) {
			switch (rule.getScheduleTypeEnum()) {
				case BEFORE:
				case AFTER:
					if (rule.getInterval() == -1) {
						throw new IllegalArgumentException("Interval cannot be null for Scheduled Rule with type BEFORE/ AFTER");
					}
					break;
				case ON:
					break;
			}
		}
		
		if (rule.getDateFieldId() != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field = modBean.getField(rule.getDateFieldId());
			switch (field.getDataTypeEnum()) {
				case DATE:
					if (rule.getTimeObj() == null) {
						throw new IllegalArgumentException("Time is mandatory for DATE field");
					}
					if (rule.getInterval() % 86400 != 0) {
						throw new IllegalArgumentException("Interval should be in multiples of days for DATE field");
					}
					break;
				case DATE_TIME:
					if (rule.getTimeObj() != null) {
						throw new IllegalArgumentException("Time is not required for DATE_TIME field");
					}
					if (rule.getInterval() % (DATE_TIME_RULE_INTERVAL * 60) != 0) {
						throw new IllegalArgumentException("Interval should be in multiples of "+DATE_TIME_RULE_INTERVAL+" min for DATE_TIME field");
					}
					break;
				default:
					throw new IllegalArgumentException("Only DATE/ DATE_TIME field can be used for Scheduled Rules");
			}
		}
	}
	
	protected static void addScheduledRuleJob(WorkflowRuleContext rule) throws Exception {
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(ScheduledRuleAPI.DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;

		if (rule.getTimeObj() != null) {
			FacilioTimer.scheduleCalendarJob(rule.getId(), "ScheduledRuleExecution", startTime, getDateSchedule(rule), "facilio");
		}
		else {
			FacilioTimer.schedulePeriodicJob(rule.getId(), "ScheduledRuleExecution", 300, DATE_TIME_RULE_INTERVAL * 60, "facilio");
		}
	}
	
	protected static void updateScheduledRuleJob(WorkflowRuleContext rule) throws Exception {
		deleteScheduledRuleJob(rule);
		addScheduledRuleJob(rule);
	}
	
	protected static void deleteScheduledRuleJob(WorkflowRuleContext rule) throws Exception {
		FacilioTimer.deleteJob(rule.getId(), "ScheduledRuleExecution");
	}
	
	private static final int DATE_TIME_RULE_INTERVAL = 30; //In Minutes //Only 5, 10, 15, 20, 30
	private static ScheduleInfo getDateSchedule(WorkflowRuleContext rule) {
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);
		info.addTime(rule.getTimeObj());
		return info;
	}
}
