package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetScoringRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (ruleId != null) {
            ScoringRuleContext scoringRuleContext = (ScoringRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
            if (scoringRuleContext == null) {
                throw new IllegalArgumentException("Invalid rule id");
            }
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scoringRuleContext);
        }
        return false;
    }
}
