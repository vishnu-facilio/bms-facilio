package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class StateTransitionFieldScheduleRuleContext extends WorkflowRuleContext {

    @Override
    public String getSchedulerJobName() {
        return "PreviousRecordRuleExecution";
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        long stateTransitionId = getParentRuleId();
        StateflowTransitionContext stateTransition = (StateflowTransitionContext) StateFlowRulesAPI.getStateTransition(stateTransitionId);
        if (stateTransition == null) {
            // invalid transition
            return false;
        }

        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        if (stateTransition.getStateFlowId() == moduleRecord.getStateFlowId()
                && stateTransition.getFromStateId() == moduleRecord.getModuleState().getId()) {
            return true;
        }

        return false;
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        long stateTransitionId = getParentRuleId();
        StateflowTransitionContext stateTransition = (StateflowTransitionContext) StateFlowRulesAPI.getStateTransition(stateTransitionId);
        stateTransition.executeTrueActions(record, context, placeHolders);

        super.executeTrueActions(record, context, placeHolders);
    }
}
