package com.facilio.readingrule.faulttowo.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SetWorkflowDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ReadingRuleWorkOrderRelContext> ruleWoDetails = (List<ReadingRuleWorkOrderRelContext>) context.get(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER);
        if (CollectionUtils.isNotEmpty(ruleWoDetails)) {
            for (ReadingRuleWorkOrderRelContext ruleCtx : ruleWoDetails) {
                if (ruleCtx.getId() != -1) {
                    getWorkflowRuleAndAction(ruleCtx.getId(),ruleCtx);
                    ActionContext action=ruleCtx.getActions().get(0);
                    if(action.getActionTypeEnum().equals(ActionType.CREATE_WO_FROM_ALARM)){
                        context.put("woCreation",ruleCtx);
                    }
                    if(action.getActionTypeEnum().equals(ActionType.CLOSE_WO_FROM_ALARM)){
                        context.put("closeWo",ruleCtx);
                    }
                }
            }
            context.remove(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER);
        }
        return false;
    }

    private WorkflowRuleContext getWorkflowRuleAndAction(Long workflowRuleId,ReadingRuleWorkOrderRelContext ruleWorkOrderRelContext) throws Exception {
        List<ActionContext> actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), workflowRuleId);
        ruleWorkOrderRelContext.setActions(actions);
        return ruleWorkOrderRelContext;
    }
}
