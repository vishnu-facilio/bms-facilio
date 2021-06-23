package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class AddDefaultStateFlowCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);

        FacilioStatus openState = new FacilioStatus();
        openState.setDisplayName("Open");
        openState.setStatus("open");
        openState.setTypeCode(1);
        TicketAPI.addStatus(openState, module);

        FacilioStatus closeState = new FacilioStatus();
        closeState.setDisplayName("Close");
        closeState.setStatus("close");
        closeState.setTypeCode(2);
        TicketAPI.addStatus(closeState, module);

        StateFlowRuleContext defaultRule = new StateFlowRuleContext();
        defaultRule.setName("Default " + module.getDisplayName() + " Stateflow");
        defaultRule.setDefaltStateFlow(true);
        defaultRule.setDefaultStateId(openState.getId());
        defaultRule.setModuleId(module.getModuleId());
        context.put(FacilioConstants.ContextNames.RECORD, defaultRule);
//        context.put(FacilioConstants.ContextNames.DEFAULT_STATEFLOW, true);
        FacilioChain addStateFlowTransition = TransactionChainFactory.getAddOrUpdateStateFlow();
        addStateFlowTransition.execute(context);

        StateflowTransitionContext transitionContext = new StateflowTransitionContext();
        transitionContext.setFromStateId(openState.getId());
        transitionContext.setToStateId(closeState.getId());
        transitionContext.setName("Close");
        transitionContext.setType(StateflowTransitionContext.TransitionType.NORMAL);
        transitionContext.setModuleId(module.getModuleId());
        transitionContext.setStateFlowId(defaultRule.getId());
        transitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, transitionContext);
        FacilioChain transitionChain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        transitionChain.execute(context);

        return false;
    }
}
