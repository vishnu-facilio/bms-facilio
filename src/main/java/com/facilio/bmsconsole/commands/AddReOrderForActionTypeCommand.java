package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowTransitionSequenceContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddReOrderForActionTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       long stateTransitionId = (long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
       List<StateFlowTransitionSequenceContext> stateFlowTransitionSequences = (List<StateFlowTransitionSequenceContext>) context.get(FacilioConstants.ContextNames.STATEFLOW_TRANSITION_SEQUENCE);

       if (CollectionUtils.isEmpty(stateFlowTransitionSequences)){
           return false;
       }

       for (StateFlowTransitionSequenceContext sequence : stateFlowTransitionSequences){
           sequence.setStateTransitionId(stateTransitionId);
       }

        StateflowTransitionContext transition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(stateTransitionId);
        transition.setId(stateTransitionId);
        transition.setStateFlowTransitionSequence(stateFlowTransitionSequences);
        WorkflowRuleAPI.updateTransitionActionSequence(transition);

        context.put(FacilioConstants.ContextNames.TRANSITION_ACTION_SEQUENCE,stateFlowTransitionSequences);

       return false;
    }
}
