package com.facilio.bmsconsole.commands;

import java.sql.Array;
import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.instrumentation.SizeOf;

public class GetWorkFlowOfRuleTypeCommand implements Command {

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
		if(ruleType != null && count != null){
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRules(ruleType, fetchEvent, fetchChildren, criteria, query, pagination ));
			ArrayList info;
			info =  (ArrayList) context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRules(ruleType, fetchEvent, fetchChildren, criteria, query, pagination ));
			long ruleCount = -1;
			if (info != null ) {
			ruleCount = info.size();
			context.put(FacilioConstants.ContextNames.RULE_COUNT, ruleCount);
			}
			else {
				context.put(FacilioConstants.ContextNames.RULE_COUNT, ruleCount);
			}
		}
		return false;
	}

}