package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.time.SecondsChronoUnit;

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
	
	public static void addScheduledRuleJob(WorkflowRuleContext rule) throws Exception {
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(ScheduledRuleAPI.DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;

		String jobName = rule.getSchedulerJobName();
		if(ScheduledRuleJobsMetaUtil.checkNewOrOldScheduleRuleExecution()) {
			jobName = rule.getScheduleRuleJobName();
		}

		if (rule.getTimeObj() != null) {
			FacilioTimer.scheduleCalendarJob(rule.getId(), jobName, startTime, getDateSchedule(rule), "facilio");
		}
		else {
			FacilioTimer.schedulePeriodicJob(rule.getId(), jobName, 300, DATE_TIME_RULE_INTERVAL * 60, "facilio");
		}
	}

	public static void addScheduledRuleJob(BaseTriggerContext trigger) throws Exception {
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(ScheduledRuleAPI.DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;

		String jobName = FacilioConstants.ContextNames.SCHEDULE_TRIGGER_EXECUTION;

		TriggerFieldRelContext fieldRel = (TriggerFieldRelContext) trigger;
		if (fieldRel.getTimeValue() != null) {
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequencyType(FrequencyType.DAILY);
			info.addTime(fieldRel.getTimeValue());
			FacilioTimer.scheduleCalendarJob(trigger.getId(), jobName, startTime, info, "facilio");
		}
		else {
			FacilioTimer.schedulePeriodicJob(trigger.getId(), jobName, 300, DATE_TIME_RULE_INTERVAL * 60, "facilio");
		}
	}

	public static void deleteScheduledTriggerJob(long triggerId) throws Exception {
		TriggerUtil.deleteTriggerRecordRelationshipTable(triggerId);
		String jobName = FacilioConstants.ContextNames.SCHEDULE_TRIGGER_EXECUTION;
		FacilioTimer.deleteJob(triggerId, jobName);
	}

	protected static void deleteScheduledRuleJob(WorkflowRuleContext rule) throws Exception {

		String jobName = rule.getSchedulerJobName();
		if(ScheduledRuleJobsMetaUtil.checkNewOrOldScheduleRuleExecution()) {
			jobName = rule.getScheduleRuleJobName();
		}

		FacilioTimer.deleteJob(rule.getId(), jobName);
	}
	
	protected static void updateScheduledRuleJob(WorkflowRuleContext rule) throws Exception {
		deleteScheduledRuleJob(rule);
		addScheduledRuleJob(rule);
	}
	
	private static final int DATE_TIME_RULE_INTERVAL = 30; //In Minutes //Only 5, 10, 15, 20, 30
	private static ScheduleInfo getDateSchedule(WorkflowRuleContext rule) {
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);
		info.addTime(rule.getTimeObj());
		return info;
	}
}
