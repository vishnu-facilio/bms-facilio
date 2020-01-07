package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmWorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchRuleActionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long ruleId = (long) context.get(FacilioConstants.ContextNames.RULE_ID);
        if (ruleId > 0) {
            List<WorkflowRuleContext> alarmWorkflowRules = ReadingRuleAPI.getAlarmWorkflowRules(ruleId);
            for (WorkflowRuleContext alarmWorkflowRule : alarmWorkflowRules ) {
                alarmWorkflowRule.setActions(ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), alarmWorkflowRule.getId()));
            }

            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, alarmWorkflowRules);
        }
        return false;
    }
}
