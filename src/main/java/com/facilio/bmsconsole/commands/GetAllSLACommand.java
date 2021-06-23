package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long slaPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if ((slaPolicyId != null && slaPolicyId > 0)) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", String.valueOf(slaPolicyId), NumberOperators.EQUALS));
            List<WorkflowRuleContext> slaRules = WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE, true, criteria, null, null);
            context.put(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, slaRules);
        }
        return false;
    }
}
