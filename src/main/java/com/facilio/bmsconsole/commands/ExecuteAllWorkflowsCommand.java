package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.FormulaAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.util.WorkflowUtil;

public class ExecuteAllWorkflowsCommand implements Command 
{
	RuleType[] ruleTypes;
	public ExecuteAllWorkflowsCommand(RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.ruleTypes = ruleTypes;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List records = (List) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(records == null) {
			Object record = context.get(FacilioConstants.ContextNames.RECORD);
			if(record != null) {
				records = Collections.singletonList(record);
			}
		}
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//		records = new LinkedList<>(records);
		if(records != null && !records.isEmpty()) {
			records = new LinkedList<>(records);
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			if(activityType != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(activityType), ruleTypes);
				
				if(workflowRules != null && workflowRules.size() > 0) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					
					for(WorkflowRuleContext workflowRule : workflowRules)
					{
						Map<String, Object> rulePlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "rule", FieldUtil.getAsProperties(workflowRule), rulePlaceHolders);
						Iterator<Integer> it = records.iterator();
						while (it.hasNext()) {
							Object record = it.next();
							if(workflowRule.getRuleTypeEnum() == RuleType.READING_RULE && ((ReadingRuleContext)workflowRule).getResourceId() != ((ReadingContext)record).getParentId()) { //Reading Rule check for specific assets used in the rule
								continue;
							}
							Map<String, Object> recordPlaceHolders = new HashMap<>(rulePlaceHolders);
							CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), recordPlaceHolders);
							boolean criteriaFlag = true;
							Criteria criteria = workflowRule.getCriteria();
							if(criteria != null) {
								if(workflowRule.getRuleTypeEnum() == RuleType.READING_RULE || workflowRule.getRuleTypeEnum() == RuleType.PM_READING_RULE) {
									criteriaFlag = criteria.computePredicate(recordPlaceHolders).evaluate(record);
									
									if(criteriaFlag) {
										updateLastValueForReadingRule((ReadingRuleContext) workflowRule, (ModuleBaseWithCustomFields) record);
									}
								}
								else {
									if(criteria.getFormulaId() != null) {
										Object record1 = FormulaAPI.getFormulaValue(criteria.getFormulaId());
										criteriaFlag = criteria.computePredicate().evaluate(record1);
									}
									else {
										criteriaFlag = criteria.computePredicate().evaluate(record);
									}
								}
							}
							boolean workflowFlag = true;
							if (workflowRule.getWorkflow() != null && workflowRule.getWorkflow().isBooleanReturnWorkflow()) {
								double result = (double) WorkflowUtil.getWorkflowExpressionResult(workflowRule.getWorkflow().getWorkflowString(), placeHolders);
								criteriaFlag = result == 1;
							}
							
							if(criteriaFlag && workflowFlag) {
								long workflowRuleId = workflowRule.getId();
								List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(orgId, workflowRuleId);
								if(actions != null) {
									context.put(FacilioConstants.ContextNames.CURRENT_WORKFLOW_RULE, workflowRule);
									context.put(FacilioConstants.ContextNames.CURRENT_RECORD, record);
									for(ActionContext action : actions)
									{
										action.executeAction(recordPlaceHolders, context);
									}
								}
								if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
									it.remove();
								}
							}
						}
					}
				}	
			}
		}
		return false;
	}
	
	private void updateLastValueForReadingRule(ReadingRuleContext readingRule, ModuleBaseWithCustomFields record) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Criteria criteria = readingRule.getCriteria();
		Condition condition = criteria.getConditions().get(1);
		long lastValue = new Double(record.getDatum(condition.getFieldName()).toString()).longValue();
		WorkflowRuleAPI.updateLastValueInReadingRule(readingRule.getId(), lastValue);
	}
}
