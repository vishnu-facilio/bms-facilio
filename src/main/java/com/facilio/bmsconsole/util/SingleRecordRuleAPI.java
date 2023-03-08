package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.util.DBConf;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduledRuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SingleRecordRuleAPI extends WorkflowRuleAPI{

	public static Logger LOGGER = LogManager.getLogger(SingleRecordRuleAPI.class.getName());

	protected static void validateRecordSpecificScheduledRule(WorkflowRuleContext rule, boolean isUpdate) throws Exception {
		
		if (EventType.SCHEDULED.isPresent(rule.getActivityType())) {
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
	
	public static List<? extends WorkflowRuleContext> getAllWorkFlowRule(long parentId, FacilioModule module,List<EventType> eventTypes) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.orderBy("EXECUTION_ORDER");
		if(CollectionUtils.isNotEmpty(eventTypes)) {
			 List<Long> eventVals = new ArrayList<Long>();
				for(EventType type : eventTypes) {
					eventVals.add((long)type.getValue());
				}
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("activityType"), eventVals, NumberOperators.EQUALS));
		}
			
		List<Map<String, Object>> list = builder.get();
		List<WorkflowRuleContext> singleRecordRuleList = getWorkFlowsFromMapList(list, true, true);
		return singleRecordRuleList;
	}
	
	private static final int DATE_TIME_RULE_INTERVAL = 30; //In Minutes //Only 5, 10, 15, 20, 30
	
	public static void addJob(WorkflowRuleContext rule) throws Exception{
		long startTime = ZonedDateTime.now().truncatedTo(new SecondsChronoUnit(DATE_TIME_RULE_INTERVAL * 60)).toInstant().toEpochMilli() - 1;
		if(rule.getSchedule() == null) {
			Long fieldVal = getDateFieldVal(rule.getParentId(), rule.getModule(), rule.getDateFieldId());

			if (fieldVal == null) {
				try{
					LOGGER.info("Field value is null => Rule name : " + rule.getName() + " Rule id : " + rule.getId() + " Module Name : " + rule.getModuleName() + " Record Id :" + rule.getParentId());
				}catch (Exception e){
				}
				return;
			}

			fieldVal = fieldVal / 1000;
			if(rule.getTimeObj() != null) {
			  fieldVal += rule.getTimeObj().toSecondOfDay();
			}
			if(fieldVal != -1) {
				//assuming date field val in millis,rule interval in seconds
				long nextExecutionTime = 0l;
				if(rule.getScheduleType() == ScheduledRuleType.BEFORE.getValue()) {
					nextExecutionTime = fieldVal - rule.getInterval();
				}
				else if(rule.getScheduleType() == ScheduledRuleType.AFTER.getValue()) {
					nextExecutionTime = fieldVal + rule.getInterval();
				}
				else {
					nextExecutionTime = fieldVal ; 
				}
			   FacilioTimer.scheduleOneTimeJobWithTimestampInSec(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, nextExecutionTime, "priority");
			}else{
				try{
					LOGGER.info("Field value is -1 => Rule name : " + rule.getName() + " Rule id : " + rule.getId() + " Module Name : " + rule.getModuleName() + " Record Id :" + rule.getParentRule());
				}catch (Exception e){
				}
			}
		}
		else {
			if(rule.getSchedule() == null) {
				throw new IllegalArgumentException("Periodic jobs should have schedule info");
			}
			FacilioTimer.scheduleCalendarJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, startTime, rule.getSchedule(), "priority");
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
		LOGGER.info("Query for SLA record fetch => "+selectBuilder.toString());
		// LOGGER.info(selectBuilder.toString());
		if(CollectionUtils.isNotEmpty(records)) {
			ModuleBaseWithCustomFields record = records.get(0);
			Map<String,Object> map = FieldUtil.getAsProperties(record);
			FacilioField field = modBean.getField(dateFieldId, module.getName());
			LOGGER.info("Record value while adding SLA jobs for date field ("+field+") => \n"+map);
			fieldVal = (Long)map.get(field.getName());
		}
		return fieldVal;
		
	}
	
	
	public static void updateRuleJobDuringRecordUpdation (long recordId, FacilioModule module, List<UpdateChangeSet> changeSet) throws Exception{
		
		List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module, null);
		if(CollectionUtils.isNotEmpty(allRules)) {
			for(WorkflowRuleContext rule : allRules) {
				if (EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleTypeEnum() == RuleType.RECORD_SPECIFIC_RULE) {
					if(rule.getScheduleTypeEnum() != null && rule.getDateFieldId() > 0 && !changeSet.isEmpty()) {
						for(UpdateChangeSet changes : changeSet) {
							if(changes.getFieldId() == rule.getDateFieldId()) {
								FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
								SingleRecordRuleAPI.addJob(rule);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public static void deleteRuleJobDuringRecordDeletion (List<Long> recordIds, FacilioModule module) throws Exception{
		
		for(Long recordId : recordIds) {
			List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module, null);
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
