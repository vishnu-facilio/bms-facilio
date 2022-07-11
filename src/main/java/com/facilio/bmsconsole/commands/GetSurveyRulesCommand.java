package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util	.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j
public class GetSurveyRulesCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		Long parentRuleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		List<ActionContext> executeCreateActions = new ArrayList<>();
		List<ActionContext> executeSubmitActions = new ArrayList<>();
		WorkflowRuleContext workflowRule = null;

		if(parentRuleId != null && parentRuleId > 0){
			workflowRule = WorkflowRuleAPI.getWorkflowRule(parentRuleId);
			Long workFlowRuleId = workflowRule.getId();

			if(workflowRule != null && workFlowRuleId > 0){
				workflowRule.setActions(ActionAPI.getActiveActionsFromWorkflowRule(workFlowRuleId));
				List<SurveyResponseRuleContext> surveyResponseRule = SurveyUtil.fetchChildRuleId(Collections.singletonList(workFlowRuleId));
				if(CollectionUtils.isNotEmpty(surveyResponseRule)){
					for(SurveyResponseRuleContext rule : surveyResponseRule){
						if(rule.getActionType().equals(SurveyResponseRuleContext.SurveyActionType.ON_CREATE)){
							executeCreateActions = ActionAPI.getActiveActionsFromWorkflowRule(rule.getId());
						}else {
							executeSubmitActions = ActionAPI.getActiveActionsFromWorkflowRule(rule.getId());
						}

					}
				}
			}
		}

		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
		context.put(FacilioConstants.Survey.EXECUTE_RESPONSE_ACTIONS, executeSubmitActions);
		context.put(FacilioConstants.Survey.EXECUTE_CREATE_ACTIONS, executeCreateActions);

		return false;
	}

}
