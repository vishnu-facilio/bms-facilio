package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.List;

public class ExecuteStateFlowCommand extends ExecuteAllWorkflowsCommand {

    public ExecuteStateFlowCommand() {
        super(WorkflowRuleContext.RuleType.STATE_FLOW);
    }

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
        List<WorkflowRuleContext> workflowRules = super.getWorkflowRules(module, activities, records);
        List<WorkflowRuleContext> newWorkflowRules = new ArrayList<>();
        // Re-arrange execution order
        for (WorkflowRuleContext workflowRuleContext : workflowRules) {
            StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) workflowRuleContext;
            if (stateFlowRuleContext.getFormId() > 0) {
                newWorkflowRules.add(0, workflowRuleContext);
            }
            else {
                newWorkflowRules.add(workflowRuleContext);
            }
        }
        return newWorkflowRules;
    }
}
