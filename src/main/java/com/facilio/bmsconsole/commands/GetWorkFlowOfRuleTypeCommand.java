package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetWorkFlowOfRuleTypeCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String count = (String)context.get(FacilioConstants.ContextNames.RULE_COUNT);
		RuleType ruleType = (RuleType) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE);
		
		Boolean fetchEvent = (Boolean) context.get(FacilioConstants.ContextNames.WORKFLOW_FETCH_EVENT);
		if (fetchEvent == null) {
			fetchEvent = true;
		}
		
		Boolean fetchChildren = (Boolean) context.get(FacilioConstants.ContextNames.WORKFLOW_FETCH_CHILDREN);
		if (fetchChildren == null) {
			fetchChildren = true;
		}
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		JSONObject serachQuery = (JSONObject) context.get(FacilioConstants.ContextNames.SEARCH);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		String query = null;
		if (( serachQuery != null)) {
			query = (String) serachQuery.get("query");
		}
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		Criteria criteria = new Criteria();
		if (filterCriteria != null) {
			criteria.andCriteria(filterCriteria);
		}
		if (( filters == null || includeParentCriteria) && view != null) {
			Criteria viewCriteria = view.getCriteria();
			if (viewCriteria != null) {
				criteria.andCriteria(viewCriteria);
			}
		}
//		if(ruleType != null){
//			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRules(ruleType, fetchEvent, fetchChildren, criteria, query, pagination, false));
//
//			if (count != null) {
//				context.put(FacilioConstants.ContextNames.RULE_COUNT, WorkflowRuleAPI.getWorkflowRules(ruleType, fetchEvent, fetchChildren, criteria, query, null, true ));
//			}
////			info =  (ArrayList) context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRules(ruleType, fetchEvent, fetchChildren, criteria, query, null, true ));
////			long ruleCount = -1;
////			if (info != null ) {
////			ruleCount = info.size();
////			context.put(FacilioConstants.ContextNames.RULE_COUNT, ruleCount);
////			}
////			else {
////				context.put(FacilioConstants.ContextNames.RULE_COUNT, ruleCount);
////			}
//		}
		
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getWorkflowEventFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleTypeField = fieldMap.get("ruleType");
		FacilioField latestVersionField = fieldMap.get("latestVersion");
		FacilioField ruleNameField = FieldFactory.getAsMap(fields).get("name");
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		List<FacilioField> queryFields = null;
		if (count != null) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(Workflow_Rule.ID)");
			countFld.setDataType(FieldType.NUMBER);
			queryFields = Collections.singletonList(countFld);
		}
		else {
			queryFields = fields;
		}
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(queryFields)
				.innerJoin(eventModule.getTableName())
				.on(module.getTableName()+".EVENT_ID = "+ eventModule.getTableName() +".ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(ruleTypeField, String.valueOf(ruleType.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(latestVersionField, String.valueOf(true), BooleanOperators.IS))
				;
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		if (query!= null) {
			builder.andCondition(CriteriaAPI.getCondition(ruleNameField, query, StringOperators.CONTAINS));
		}
		if(criteria != null && !criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}		
		if (count != null) {
			context.put(FacilioConstants.ContextNames.RULE_COUNT, builder.get().get(0).get("count"));
		} else {
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkFlowsFromMapList(builder.get(), fetchEvent, fetchChildren, true));

		}
		return false;
	}

}