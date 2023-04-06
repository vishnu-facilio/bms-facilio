package com.facilio.readingrule.faulttowo.command;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FaultToWorkorderStatusChangeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (ruleId != null) {
            List<ReadingRuleWorkOrderRelContext> ruleWoDetails = RuleWoAPI.getRuleWoDetailsFromRuleId(ruleId);
            if (CollectionUtils.isNotEmpty(ruleWoDetails)) {
                for (ReadingRuleWorkOrderRelContext ruleWo : ruleWoDetails) {
                    WorkflowRuleContext wfRule =WorkflowRuleAPI.getWorkflowRule(ruleWo.getWorkFlowRuleId());
                    wfRule.setStatus(!wfRule.getStatus());
                    WorkflowRuleAPI.updateWorkflowRule(wfRule);
                }
            }
        }
        return false;
    }
}
