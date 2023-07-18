package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
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
    public boolean canAddOneTimeJob(ModuleBaseWithCustomFields moduleRecord) throws Exception {
        long stateTransitionId = getParentRuleId();
        StateflowTransitionContext stateTransition = (StateflowTransitionContext) StateFlowRulesAPI.getStateTransition(stateTransitionId);
        if (stateTransition == null) {
            // invalid transition
            return false;
        }
        if (stateTransition.getStateFlowId() == moduleRecord.getStateFlowId()
                && stateTransition.getFromStateId() == moduleRecord.getModuleState().getId()) {
            return true;
        }
        return false;
    }

    @Override
    public Long validatedExecutionTime(Long executionTime){
        if(executionTime == null){
            return null;
        }

        long currentTime = (System.currentTimeMillis() / 1000);
        if(executionTime < currentTime) {
            executionTime = currentTime + 30;
        }
        return executionTime;
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        long stateTransitionId = getParentRuleId();
        StateflowTransitionContext stateTransition = (StateflowTransitionContext) StateFlowRulesAPI.getStateTransition(stateTransitionId);
        stateTransition.executeTrueActions(record, context, placeHolders);

        super.executeTrueActions(record, context, placeHolders);
    }
}
