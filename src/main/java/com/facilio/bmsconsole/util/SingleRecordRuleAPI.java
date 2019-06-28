package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.SecondsChronoUnit;

public class SingleRecordRuleAPI extends WorkflowRuleAPI{

	protected static void validateRecordSpecificScheduledRule(WorkflowRuleContext rule, boolean isUpdate) throws Exception {
		
		if (EventType.SCHEDULED.isPresent(rule.getEvent().getActivityType())) {
			if (rule.getScheduleTypeEnum() != null) {
				if (rule.getDateFieldId() == -1) {
					throw new IllegalArgumentException("Date Field Id cannot be null for Record specific Scheduled Rule");
				}
				switch (rule.getScheduleTypeEnum()) {
					case BEFORE:
					case AFTER:
						if (rule.getInterval() == -1) {
							throw new IllegalArgumentException("Interval cannot be null for Record specific Scheduled Rule with type BEFORE/ AFTER");
						}
						break;
					case ON:
						break;
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
						throw new IllegalArgumentException("Only DATE/ DATE_TIME field can be used for Record specific Scheduled Rules");
				   }
			   }
			}
			else {
				if(rule.getSchedule() == null) {
					throw new IllegalArgumentException("Either datefield schedule/periodic schedule info has to be present");
				}
				if(rule.getDateFieldId() != -1) {
					throw new IllegalArgumentException("No datefield needed for periodic jobs");
				}
			}
		}
	}
	
	public static List<? extends WorkflowRuleContext> getAllWorkFlowRule(long parentId, FacilioModule module) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		fields.addAll(FieldFactory.getWorkflowEventFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.innerJoin("Workflow_Event")
				.on("Workflow_Rule.EVENT_ID = Workflow_Event.ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.orderBy("EXECUTION_ORDER");
		List<Map<String, Object>> list = builder.get();
		List<WorkflowRuleContext> singleRecordRuleList = getWorkFlowsFromMapList(list, false, true, true);
		return singleRecordRuleList;
	}
	
	private static final int DATE_TIME_RULE_INTERVAL = 30; //In Minutes //Only 5, 10, 15, 20, 30
	
	public static void addJob(WorkflowRuleContext rule) throws Exception{
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;
		WorkflowEventContext event = getWorkflowEvent(rule.getEventId());
		if(rule.getSchedule() == null) {
			Long fieldVal = getDateFieldVal(rule.getParentId(), event.getModule(), rule.getDateFieldId());
			fieldVal = fieldVal / 1000;
			if(rule.getTimeObj() != null) {
			  fieldVal += rule.getTimeObj().toSecondOfDay();
			}
			if(fieldVal != -1) {
				//assuming date field val in millis,rule interval in seconds
			   FacilioTimer.scheduleOneTimeJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, fieldVal + rule.getInterval(), "facilio");
			}
		}
		else {
			if(rule.getSchedule() == null) {
				throw new IllegalArgumentException("Periodic jobs should have schedule info");
			}
			FacilioTimer.scheduleCalendarJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, startTime, rule.getSchedule(), "facilio");
		}

	}
	
	private static Long getDateFieldVal(long recordId, FacilioModule module, long dateFieldId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long fieldVal = -1L;
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(module.getName());
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(modBean.getAllFields(module.getName()))
																				.module(module)
																			//	.andCondition(CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN))
																				.beanClass(beanClassName)
																				;

		
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(recordId, module));
		
		List<ModuleBaseWithCustomFields> records = selectBuilder.get();
		// LOGGER.info(selectBuilder.toString());
		if(CollectionUtils.isNotEmpty(records)) {
			ModuleBaseWithCustomFields record = records.get(0);
			Map<String,Object> map = FieldUtil.getAsProperties(record);
			FacilioField field = modBean.getField(dateFieldId, module.getName());
			fieldVal = (Long)map.get(field.getName());
		}
		return fieldVal;
		
	}
	
	
	public static void updateRuleJobDuringRecordUpdation (long recordId, FacilioModule module, List<UpdateChangeSet> changeSet) throws Exception{
		
		List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module);
		if(CollectionUtils.isNotEmpty(allRules)) {
			for(WorkflowRuleContext rule : allRules) {
				WorkflowEventContext event = getWorkflowEvent(rule.getEventId());
				if (event != null) {
					if (EventType.SCHEDULED.isPresent(event.getActivityType()) && rule.getRuleTypeEnum() == RuleType.RECORD_SPECIFIC_RULE) {
						if(rule.getScheduleTypeEnum() != null && rule.getDateFieldId() > 0 && !changeSet.isEmpty()) {
							for(UpdateChangeSet changes : changeSet) {
								if(changes.getFieldId() == rule.getDateFieldId()) {
									FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
									SingleRecordRuleAPI.addJob(rule);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void deleteRuleJobDuringRecordDeletion (List<Long> recordIds, FacilioModule module) throws Exception{
		
		for(Long recordId : recordIds) {
			List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module);
			if(CollectionUtils.isNotEmpty(allRules)) {
				for(WorkflowRuleContext rule : allRules) {
					WorkflowRuleAPI.deleteWorkflowRule(rule.getId());
				}
			}
		}
	}
	
	protected static void deleteRecordSpecificRuleJob(WorkflowRuleContext rule) throws Exception {
		FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
	}
}
