package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import org.apache.commons.chain.Context;

public class CreateClonedStateFlowCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
        if (criteria == null || criteria.isEmpty()) {
            throw new IllegalArgumentException("Criteria cannot be empty");
        }

        StateFlowRuleContext stateFlow = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
        stateFlow.setName("Copy of " + stateFlow.getName());
        stateFlow.setDefaltStateFlow(false);
        stateFlow.setDraft(true);
        stateFlow.setPublishedDate(-1);
        stateFlow.setFormLevel(false);
        stateFlow.setCriteria(criteria);
        stateFlow.setExecutionOrder(0);

        AddOrUpdateStateFlowCommand.updateStateTransitionExecutionOrder(stateFlow.getModule());

        context.put(FacilioConstants.ContextNames.STATE_FLOW, stateFlow);
        return false;
    }
}
