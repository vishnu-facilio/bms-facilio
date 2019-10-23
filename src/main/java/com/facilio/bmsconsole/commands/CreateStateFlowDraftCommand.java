package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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

            FacilioChain ruleChain = TransactionChainFactory.addWorkflowRuleChain();
            FacilioContext ruleContext = ruleChain.getContext();
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlowContext);
            ruleChain.execute();
            long createdStateFlowId = stateFlowContext.getId();
            context.put(FacilioConstants.ContextNames.STATE_FLOW, stateFlowContext);

            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(originalStateFlowId);
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                for (WorkflowRuleContext workflowRuleContext : allStateTransitionList) {
                    StateflowTransitionContext stateflowTransitionContext = (StateflowTransitionContext) workflowRuleContext;
                    stateflowTransitionContext.setStateFlowId(createdStateFlowId);

                    ruleChain = TransactionChainFactory.addWorkflowRuleChain();
                    ruleContext = ruleChain.getContext();
                    ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateflowTransitionContext);
                    ruleChain.execute();
                }
            }
            context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, allStateTransitionList);
        }
        return false;
    }
}
