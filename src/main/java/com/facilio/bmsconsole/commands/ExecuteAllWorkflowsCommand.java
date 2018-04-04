package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.wms.endpoints.SessionManager;

public class ExecuteAllWorkflowsCommand implements Command 
{
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	RuleType[] ruleTypes;
	public ExecuteAllWorkflowsCommand(RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.ruleTypes = ruleTypes;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		if (recordMap == null) {
			List records = (List) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if(records == null) {
				Object record = context.get(FacilioConstants.ContextNames.RECORD);
				if(record != null) {
					records = Collections.singletonList(record);
				}
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
				logger.log(Level.WARNING, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+records);
				return false;
			}
			
			recordMap = Collections.singletonMap(moduleName, records);
		}
		
		if(recordMap != null && !recordMap.isEmpty()) {
			for (Map.Entry<String, List> entry : recordMap.entrySet()) {
				String moduleName = entry.getKey();
				if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
					logger.log(Level.WARNING, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
					continue;
				}
				
				List records = new LinkedList<>(entry.getValue());
				ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if(activityType != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					long moduleId = modBean.getModule(moduleName).getModuleId();
					List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(activityType), ruleTypes);
					
					if(workflowRules != null && workflowRules.size() > 0) {
						Map<String, Object> placeHolders = new HashMap<>();
						CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
						
						for(WorkflowRuleContext workflowRule : workflowRules) {
							Map<String, Object> rulePlaceHolders = new HashMap<>(placeHolders);
							CommonCommandUtil.appendModuleNameInKey(null, "rule", FieldUtil.getAsProperties(workflowRule), rulePlaceHolders);
							Iterator it = records.iterator();
							while (it.hasNext()) {
								Object record = it.next();
								Map<String, Object> recordPlaceHolders = workflowRule.getPlaceHolders(moduleName, record, rulePlaceHolders, (FacilioContext) context);
								boolean miscFlag = false, criteriaFlag = false, workflowFlag = false;
								miscFlag = workflowRule.evaluateMisc(moduleName, record, recordPlaceHolders, (FacilioContext) context);
								if (miscFlag) {
									criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, recordPlaceHolders, (FacilioContext) context);
									if (criteriaFlag) {
										workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, recordPlaceHolders, (FacilioContext) context);
									}
								}
								if(criteriaFlag && workflowFlag && miscFlag) {
									executeWorkflowActions(workflowRule, record, context, recordPlaceHolders);
									if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
										it.remove();
									}
								}
							}
						}
					}	
				}
			}
		}
		return false;
	}
	
	private void executeWorkflowActions(WorkflowRuleContext rule, Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = rule.getId();
		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), ruleId);
		if(actions != null) {
			context.put(FacilioConstants.ContextNames.CURRENT_WORKFLOW_RULE, rule);
			context.put(FacilioConstants.ContextNames.CURRENT_RECORD, record);
			for(ActionContext action : actions)
			{
				action.executeAction(placeHolders, context);
			}
		}
	}
}
