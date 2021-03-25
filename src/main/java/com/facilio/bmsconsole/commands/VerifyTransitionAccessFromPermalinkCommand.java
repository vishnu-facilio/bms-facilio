package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class VerifyTransitionAccessFromPermalinkCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        AbstractStateTransitionRuleContext stateTransition = (AbstractStateTransitionRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
        if (stateTransition != null) {
            boolean shouldExecuteFromPermalink = stateTransition.isShouldExecuteFromPermalink();
            if (!shouldExecuteFromPermalink) {
                throw new IllegalArgumentException("Cannot access from Permalink");
            }
        }
        return false;
    }
}
