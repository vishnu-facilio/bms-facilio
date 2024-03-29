package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class DeleteSurveyRulesCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<Long> ruleIds = (List<Long>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);

		if(CollectionUtils.isNotEmpty(ruleIds)) {
			List<SurveyResponseRuleContext> surveyResponseRuleContext = SurveyUtil.fetchChildRuleId(ruleIds);
			List<Long> actionRuleIds = surveyResponseRuleContext.stream().map(SurveyResponseRuleContext::getId).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(actionRuleIds)){
				WorkflowRuleAPI.deleteWorkFlowRules(actionRuleIds);
			}
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}

		return false;
	}


}
