package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApproverWorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetConfirmationDialogFromApproverWorkflowRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (ruleId != null && ruleId > 0) {
            WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);
            if (workflowRule instanceof ApproverWorkflowRuleContext) {
                List<ConfirmationDialogContext> confirmationDialogs = ((ApproverWorkflowRuleContext) workflowRule).getConfirmationDialogs();
                context.put(FacilioConstants.ContextNames.CONFIRMATION_DIALOGS, confirmationDialogs);
            }
        }
        return false;
    }
}
