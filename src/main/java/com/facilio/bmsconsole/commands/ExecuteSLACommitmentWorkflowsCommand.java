package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.Collections;
import java.util.List;

public class ExecuteSLACommitmentWorkflowsCommand extends ExecuteAllWorkflowsCommand {

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
        activities.add(EventType.SLA);

//        Criteria parentCriteria = getCriteria(records);
        List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, Collections.singletonList(EventType.SLA), null, WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE);
        return workflowRules;
    }
}
