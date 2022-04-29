package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyResponseContext;
import com.facilio.chain.FacilioContext;
import com.facilio.qa.context.ResponseContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SurveyResponseRuleContext extends WorkflowRuleContext{

	private long executeCreateRuleId;
	private long executeSubmitRuleId;
	private boolean isResponseSubmitted;
	private long ruleId;

	@Override
	public boolean evaluateMisc (String moduleName, Object record, Map< String, Object > placeHolders, FacilioContext context) throws Exception {

		SurveyResponseContext template = (SurveyResponseContext) record;
		if(template.getResStatus() == ResponseContext.ResponseStatus.NOT_ANSWERED || template.getResStatus() == ResponseContext.ResponseStatus.COMPLETED){
			return (template.getRuleId() == getRuleId());
		}
		return false;
	}
}
