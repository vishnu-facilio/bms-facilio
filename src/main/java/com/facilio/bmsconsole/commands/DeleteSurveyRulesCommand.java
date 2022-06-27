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

@Log4j
public class DeleteSurveyRulesCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<Long> ruleIds = (List<Long>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);

		if(CollectionUtils.isNotEmpty(ruleIds)){
			SurveyResponseRuleContext surveyResponseRuleContext = SurveyUtil.fetchChildRuleId(ruleIds);
			if(surveyResponseRuleContext != null){
				Long createRuleId = surveyResponseRuleContext.getExecuteCreateRuleId();
				Long submitRuleId = surveyResponseRuleContext.getExecuteSubmitRuleId();

				if(createRuleId != null && createRuleId > 0){
					WorkflowRuleAPI.deleteWorkFlowRules(Collections.singletonList(createRuleId));
				}
				if(submitRuleId !=null && submitRuleId > 0){
					WorkflowRuleAPI.deleteWorkFlowRules(Collections.singletonList(submitRuleId));
				}
			}
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}

		return false;
	}


}
