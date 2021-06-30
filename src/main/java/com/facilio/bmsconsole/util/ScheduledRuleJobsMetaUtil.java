package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class ScheduledRuleJobsMetaUtil {
	
	public static boolean checkNewOrOldScheduleRuleExecution() throws Exception{
		
		Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_NEW_SCHEDULE_RULE_EXECUTION);
    	if (orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
    		String isNewScheduleRuleExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_NEW_SCHEDULE_RULE_EXECUTION);
			if (isNewScheduleRuleExecutionProp != null && !isNewScheduleRuleExecutionProp.isEmpty() && StringUtils.isNotEmpty(isNewScheduleRuleExecutionProp) && Boolean.valueOf(isNewScheduleRuleExecutionProp)) {
				return true;
			}
    	}
		return false;
	}
	
	public static void addScheduledRuleJobsMeta(ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext) throws Exception{
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.fields(FieldFactory.getScheduledRuleJobsMetaFields());
		Map<String, Object> props = FieldUtil.getAsProperties(scheduledRuleJobsMetaContext);
		insertBuilder.addRecord(props);
		
		insertBuilder.save();
		scheduledRuleJobsMetaContext.setId((Long) props.get("id"));
	}
	
	public static List<WorkflowRuleContext> fetchMatchedScheduledRulesFromModule(long moduleId) throws Exception{
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", ""+moduleId, NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition("SCHEDULE_TYPE","scheduleType", "", CommonOperators.IS_NOT_EMPTY))
													.andCondition(CriteriaAPI.getCondition("DATE_FIELD_ID", "dateFieldId", "", CommonOperators.IS_NOT_EMPTY));

		List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkFlowsFromMapList(ruleBuilder.get(), true, true);
		if(rules != null && !rules.isEmpty()) {
			return rules;
		}
		return null;
	}
	
	public static List<ScheduledRuleJobsMetaContext> fetchAlreadyPresentScheduledRuleJobsMeta(long ruleId, long moduleId, long recordId) throws Exception{
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledRuleJobsMetaFields())
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", ""+true, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", ""+moduleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", ""+recordId, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<ScheduledRuleJobsMetaContext> scheduledRuleJobsMetaContextList = FieldUtil.getAsBeanListFromMapList(props, ScheduledRuleJobsMetaContext.class);
			if(scheduledRuleJobsMetaContextList != null && scheduledRuleJobsMetaContextList.isEmpty()) {
				return scheduledRuleJobsMetaContextList;	
			}
		}	
		return null;
	}
	
	public static void disableScheduledRuleJobsMetaFromRuleId(long ruleId, DateRange dateRange) throws Exception{
		
		FacilioModule module = ModuleFactory.getScheduledRuleJobsMetaModule();
		List<FacilioField> fields = FieldFactory.getScheduledRuleJobsMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		FacilioField isActiveField = fieldMap.get("isActive");
		updatedfields.add(isActiveField);
		
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("isActive", false);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.fields(updatedfields)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", ""+true, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("EXECUTION_TIME", "executionTime", dateRange.toString(), DateOperators.BETWEEN));


		Map<String, Object> props = FieldUtil.getAsProperties(updateMap);
		updateBuilder.update(props);
	}
	
	public static void updateScheduledRuleJobsMeta(ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext) throws Exception{
	
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.fields(FieldFactory.getScheduledRuleJobsMetaFields())
				.andCondition(CriteriaAPI.getIdCondition(scheduledRuleJobsMetaContext.getId(), ModuleFactory.getScheduledRuleJobsMetaModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(scheduledRuleJobsMetaContext);
		updateBuilder.update(props);
	}

}
