package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class CreateStateFlowDraftCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (stateFlowId != null && stateFlowId > 0) {
            StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
            if (stateFlowContext.isDraft() && stateFlowContext.getDraftParentId() == -1) {
                // newly created stateflow, and that is not live yet
                context.put(FacilioConstants.ContextNames.STATE_FLOW, stateFlowContext);
                context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext));
                return false;
            }

            long originalStateFlowId = stateFlowContext.getId();

            StateFlowRuleContext draftStateFlow = StateFlowRulesAPI.getDraftStateFlowForParent(originalStateFlowId);
            if (draftStateFlow != null) {
                context.put(FacilioConstants.ContextNames.STATE_FLOW, draftStateFlow);
                context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, StateFlowRulesAPI.getAllStateTransitionList(draftStateFlow));
                return false;
            }

            stateFlowContext.setDraftParentId(originalStateFlowId);
            stateFlowContext.setDraft(true);

            context.put(FacilioConstants.ContextNames.STATE_FLOW, stateFlowContext);
        }
        return false;
    }
}
