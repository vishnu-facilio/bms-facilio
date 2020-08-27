package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ExecuteSpecificWorkflowsCommand extends ExecuteAllWorkflowsCommand {

    private List<Long> workflowIds;

    public ExecuteSpecificWorkflowsCommand(WorkflowRuleContext.RuleType... ruleTypes) {
        super(ruleTypes);
    }

    public ExecuteSpecificWorkflowsCommand(List<Long> workflowIds, WorkflowRuleContext.RuleType... ruleTypes) {
        super(ruleTypes);
        this.workflowIds = workflowIds;
    }

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
        Criteria criteria = getCriteria(records);
        if (criteria == null) {
            criteria = new Criteria();
        }
        if (CollectionUtils.isNotEmpty(workflowIds)) {
            criteria.addAndCondition(CriteriaAPI.getIdCondition(workflowIds, ModuleFactory.getWorkflowRuleModule()));
        }
        List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST);
        if (CollectionUtils.isNotEmpty(ids)) {
            criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getWorkflowRuleModule()));
        }

        List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, activities, criteria, getRuleTypes());
        return workflowRules;
    }
}
