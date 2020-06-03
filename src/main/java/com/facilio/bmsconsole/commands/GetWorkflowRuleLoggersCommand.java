package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetWorkflowRuleLoggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long ruleId = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		Integer ruleJobType = (Integer) context.get(FacilioConstants.ContextNames.RULE_JOB_TYPE);
		ruleJobType = (ruleJobType == null) ? RuleJobType.READING_ALARM.getIndex() : ruleJobType;
		RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);
		Integer preAlarmRuleJobType = null;

		if(ruleJobTypeEnum == RuleJobType.READING_ALARM) {
			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() > 0 || triggerRule.getOccurences() > 1) {
				preAlarmRuleJobType = RuleJobType.PRE_ALARM.getIndex();
			}	
		}
			
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleLoggerFields());	
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleLoggerFields())
				.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), "" +ruleId, NumberOperators.EQUALS));
			
		if(preAlarmRuleJobType != null) {
			Criteria criteria = new Criteria();
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("ruleJobType"), "" +ruleJobType, NumberOperators.EQUALS));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("ruleJobType"), "" +preAlarmRuleJobType, NumberOperators.EQUALS));
			builder.andCriteria(criteria);
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleJobType"), "" +ruleJobType, NumberOperators.EQUALS));
		}
				
		builder.orderBy("STATUS,CREATED_TIME DESC");
		
//		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
//		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
//		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
//		if (filterCriteria != null) {
//			builder.andCriteria(filterCriteria);
//		}
//		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
//		
//		if ((filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
//			builder.andCriteria(view.getCriteria());
//		}
//
//		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
//		if (searchCriteria != null) {
//			builder.andCriteria(searchCriteria);
//		}
//		
//		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
//		if (criteriaIds != null) {
//			String[] ids = criteriaIds.split(",");
//			for(int i = 0; i < ids.length; i++) {
//				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
//				builder.andCriteria(criteria);
//			}
//		}
//		
//		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
//		if(scopeCriteria != null)
//		{
//			builder.andCriteria(scopeCriteria);
//		}
//
//		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
//		if (orderBy != null && !orderBy.isEmpty()) {
//			builder.orderBy(orderBy);
//		}
//		
//		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
//		if (pagination != null) {
//			int page = (int) pagination.get("page");
//			int perPage = (int) pagination.get("perPage");
//			
//			int offset = ((page-1) * perPage);
//			if (offset < 0) {
//				offset = 0;
//			}
//			
//			builder.offset(offset);
//			builder.limit(perPage);
//		}
//		
//		Long entityId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ENTITY_ID);
//		if(entityId != null && entityId != -1)
//		{
//			builder.andCustomWhere("Alarms.ENTITY_ID = ?", entityId);
//		}
		
		List<Map<String, Object>> props = builder.get();
		List<WorkflowRuleLoggerContext> workflowRuleLoggerContextList = new ArrayList<WorkflowRuleLoggerContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleLoggerContextList  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleLoggerContext.class);		
		}	

		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGGERS, workflowRuleLoggerContextList);
			
		return false;
		
	}

}
