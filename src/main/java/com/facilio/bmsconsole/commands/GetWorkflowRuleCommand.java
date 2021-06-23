package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetWorkflowRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long ruleId = (long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (ruleId > 0) {
            WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        }
        return false;
    }
}
