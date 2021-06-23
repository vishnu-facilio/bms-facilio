package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class ChangeStateFlowStatusCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        if (rule != null) {
            StateFlowRuleContext stateFlow = (StateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(rule.getId());
            if (stateFlow == null) {
                throw new IllegalArgumentException("State Flow not found");
            }
            if (stateFlow.isDefaltStateFlow()) {
                throw new IllegalArgumentException("Cannot change status for default State Flow");
            }

            WorkflowRuleAPI.updateWorkflowRule(rule);
        }
        return false;
    }
}
