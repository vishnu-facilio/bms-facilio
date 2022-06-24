package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteOldSurveyRuleActionCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		WorkflowRuleContext workFlowRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		List<ActionContext> createActions = (List<ActionContext>) context.get(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS);
		List<ActionContext> responseActions = (List<ActionContext>) context.get(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS);

		SurveyResponseRuleContext responseRule = SurveyUtil.fetchChildRuleId(Collections.singletonList(workFlowRule.getId()));

		if(workFlowRule !=null && workFlowRule.getId() > 0) {
			if(CollectionUtils.isNotEmpty(workFlowRule.getActions())){
				ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(workFlowRule.getId()));
			}
		}

		if(responseRule != null){
			Long createRuleId = responseRule.getExecuteCreateRuleId();
			if(createRuleId != null && createRuleId > 0){
				if(CollectionUtils.isNotEmpty(createActions)){
					context.put("createRuleId", createRuleId);
					ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(createRuleId));
				}
			}

			Long submitRuleId = responseRule.getExecuteSubmitRuleId();
			if(submitRuleId != null && submitRuleId > 0){
				if(CollectionUtils.isNotEmpty(responseActions)){
					context.put("submitRuleId", submitRuleId);
					ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(submitRuleId));
				}
			}
		}

		return false;
	}

}
