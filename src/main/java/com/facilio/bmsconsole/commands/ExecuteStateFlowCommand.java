package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class ExecuteStateFlowCommand extends ExecuteAllWorkflowsCommand {

    public ExecuteStateFlowCommand() {
        super(WorkflowRuleContext.RuleType.STATE_FLOW);
    }

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
        List<WorkflowRuleContext> workflowRules = super.getWorkflowRules(module, activities, records);
        List<WorkflowRuleContext> newWorkflowRules = null;

        // Re-arrange execution order
        if (CollectionUtils.isNotEmpty(workflowRules)) {
        	newWorkflowRules = new ArrayList<>();
	        for (WorkflowRuleContext workflowRuleContext : workflowRules) {
	            StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) workflowRuleContext;
	            if (stateFlowRuleContext.isFormLevel()) {
                    StateFlowRuleContext cloned = FieldUtil.cloneBean(stateFlowRuleContext, StateFlowRuleContext.class);
                    cloned.setShouldCheckOnlyFormBased(true);
	                newWorkflowRules.add(0, cloned);
	            }
	            newWorkflowRules.add(stateFlowRuleContext);
	        }
        }
        return newWorkflowRules;
    }
}
