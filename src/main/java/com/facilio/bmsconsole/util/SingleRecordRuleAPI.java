package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduleMode;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.JobStore;
import com.facilio.time.SecondsChronoUnit;

public class SingleRecordRuleAPI extends WorkflowRuleAPI{

	public static List<? extends WorkflowRuleContext> getAllWorkFlowRule(long parentId, FacilioModule module) throws Exception {
		List<FacilioField> fields = FieldFactory.getRecordSpecificRuleFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.select(fields)
				.innerJoin(ModuleFactory.getRecordSpecificRuleModule().getTableName())
					.on(ModuleFactory.getWorkflowRuleModule().getTableName() + ".ID = " + ModuleFactory.getRecordSpecificRuleModule().getTableName() + ".ID")
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.orderBy("EXECUTION_ORDER");
		List<Map<String, Object>> list = builder.get();
		List<WorkflowRuleContext> singleRecordRuleList = getWorkFlowsFromMapList(list, false, true, true);
		return singleRecordRuleList;
	}
	
	private static final int DATE_TIME_RULE_INTERVAL = 30; //In Minutes //Only 5, 10, 15, 20, 30
	
	public static void addJob(WorkflowRuleContext rule) throws Exception{
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;

		if(rule.getScheduleModeEnum() == ScheduleMode.ONCE) {
			long nextExecutionTime = rule.getSchedule().nextExecutionTime(System.currentTimeMillis());
			FacilioTimer.scheduleOneTimeJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, nextExecutionTime, "facilio");
		}
		else {
		if (rule.getTimeObj() != null) {
			FacilioTimer.scheduleCalendarJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, startTime, getDateSchedule(rule), "facilio");
		}
		else {
			FacilioTimer.schedulePeriodicJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, 300, DATE_TIME_RULE_INTERVAL * 60, "facilio");
		}
		}

	}
	
	
	private static ScheduleInfo getDateSchedule(WorkflowRuleContext rule) {
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(FrequencyType.DAILY);
		info.addTime(rule.getTimeObj());
		return info;
	}
	
	public static void updateRuleJobDuringRecordUpdation (long recordId, FacilioModule module, Map<Long, List<UpdateChangeSet>> changeSet) throws Exception{
		
		List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module);
		if(CollectionUtils.isNotEmpty(allRules)) {
			for(WorkflowRuleContext rule : allRules) {
				if (rule.getEvent() != null) {
					if (EventType.SCHEDULED_RECORD_RULE.isPresent(rule.getEvent().getActivityType())) {
						if(changeSet.containsKey(rule.getDateFieldId())) {
							FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
							SingleRecordRuleAPI.addJob(rule);
						}
					}
				}
			}
		}
	}
}
