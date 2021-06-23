package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetControlActionRulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
		
		FacilioModule workflowRuleModule = ModuleFactory.getWorkflowRuleModule();
		
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		
		fetchCount = fetchCount != null ? fetchCount : Boolean.FALSE;
		
		List<FacilioField> fields = null;
		if (fetchCount != null && fetchCount) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT("+workflowRuleModule.getTableName()+".ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = FieldFactory.getWorkflowRuleFields();
		}
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName());
		
		Criteria rulesTypeToBeFetchedCriteria = getRuleTypesToBeFetched();
		
		ruleBuilder.andCriteria(rulesTypeToBeFetchedCriteria);

		ruleBuilder.select(fields);
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			ruleBuilder.andCriteria(filterCriteria);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			ruleBuilder.andCriteria(view.getCriteria());
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			ruleBuilder.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				ruleBuilder.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(workflowRuleModule.getName());
		if(scopeCriteria != null)
		{
			ruleBuilder.andCriteria(scopeCriteria);
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			ruleBuilder.orderBy(orderBy);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			ruleBuilder.offset(offset);
			ruleBuilder.limit(perPage);
		}
		
		List<Map<String, Object>> props = ruleBuilder.get();
		
		
		if(fetchCount == null || !fetchCount) {
			List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkFlowsFromMapList(props, true, true);
			
			if(rules != null) {
				for(WorkflowRuleContext rule :rules) {
					List<ActionContext> actionList = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
					rule.setActions(actionList);
				}
			}
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULES, rules);
		}
		else {
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULES_COUNT, props.get(0).get("count"));
		}
		
		return false;
	}

	private Criteria getRuleTypesToBeFetched() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule reservationModule = modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATION);
		
		Criteria criteria = new Criteria();
		Criteria criteria1 = new Criteria();
		
		Condition condition1 = CriteriaAPI.getCondition("RULE_TYPE", "ruleType", RuleType.CONTROL_ACTION_READING_ALARM_RULE.getIntVal()+","+RuleType.CONTROL_ACTION_SCHEDULED_RULE.getIntVal(), NumberOperators.EQUALS);
		
		Condition condition2 = CriteriaAPI.getCondition("RULE_TYPE", "ruleType", RuleType.RECORD_SPECIFIC_RULE.getIntVal()+"", NumberOperators.EQUALS);
		
		Condition condition3 = CriteriaAPI.getCondition("MODULEID", "moduleId", reservationModule.getModuleId()+"", NumberOperators.EQUALS);
		
		criteria1.addAndCondition(condition2);
		criteria1.addAndCondition(condition3);
		
		
		criteria.addAndCondition(condition1);
		criteria.orCriteria(criteria1);
		
		return criteria;
	}
	
	
	
	

}