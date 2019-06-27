package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
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

public class SingleRecordRuleAPI extends WorkflowRuleAPI{

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

		if(rule.getSchedule() == null) {
			Long fieldVal = getDateFieldVal(rule.getParentId(), rule.getEvent().getModule(), rule.getDateFieldId());
			if(fieldVal != -1) {
				//assuming date field val in millis,rule interval in seconds
			   FacilioTimer.scheduleOneTimeJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, fieldVal + (rule.getInterval() * 1000), "facilio");
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
	
	
	public static void updateRuleJobDuringRecordUpdation (long recordId, FacilioModule module, Map<Long, List<UpdateChangeSet>> changeSet) throws Exception{
		
		List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module);
		if(CollectionUtils.isNotEmpty(allRules)) {
			for(WorkflowRuleContext rule : allRules) {
				if (rule.getEvent() != null) {
					if (EventType.SCHEDULED.isPresent(rule.getEvent().getActivityType()) && rule.getRuleTypeEnum() == RuleType.RECORD_SPECIFIC_RULE) {
						if(changeSet.containsKey(rule.getDateFieldId())) {
							FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
							SingleRecordRuleAPI.addJob(rule);
						}
					}
				}
			}
		}
	}
	
	public static void deleteRuleJobDuringRecordDeletion (long recordId, FacilioModule module) throws Exception{
		
		List<WorkflowRuleContext> allRules = (List<WorkflowRuleContext>) getAllWorkFlowRule(recordId, module);
		if(CollectionUtils.isNotEmpty(allRules)) {
			for(WorkflowRuleContext rule : allRules) {
				WorkflowRuleAPI.deleteWorkflowRule(rule.getId());
			}
		}
	}
	
	protected static void deleteRecordSpecificRuleJob(WorkflowRuleContext rule) throws Exception {
		FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
	}
}
