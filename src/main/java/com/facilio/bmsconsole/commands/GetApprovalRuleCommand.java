package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GetApprovalRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if (id > 0) {
            ApprovalStateFlowRuleContext stateFlowContext = (ApprovalStateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(id);

            ApprovalRuleMetaContext approvalMeta = new ApprovalRuleMetaContext();
            approvalMeta.setName(stateFlowContext.getName());
            approvalMeta.setDescription(stateFlowContext.getDescription());
            approvalMeta.setCriteria(stateFlowContext.getCriteria());
            approvalMeta.setEventType(stateFlowContext.getActivityTypeEnum());

            if (CollectionUtils.isNotEmpty(stateFlowContext.getFields())) {
                List<Long> fieldIds = stateFlowContext.getFields().stream().map(FieldChangeFieldContext::getFieldId).collect(Collectors.toList());
                approvalMeta.setFieldIds(fieldIds);
            }
            approvalMeta.setId(stateFlowContext.getId());

            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext.getId());
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                for (WorkflowRuleContext rule : allStateTransitionList) {
                    ApprovalStateTransitionRuleContext stateTransitionRule = (ApprovalStateTransitionRuleContext) rule;
                    if (rule.getName().equals("Approve")) {
                        approvalMeta.setApprovers(stateTransitionRule.getApprovers());
                        approvalMeta.setApprovalOrder(stateTransitionRule.getApprovalOrder());
                        approvalMeta.setAllApprovalRequired(stateTransitionRule.getAllApprovalRequired());
                        approvalMeta.setApprovalForm(stateTransitionRule.getForm());
                        if (stateTransitionRule.getActions() == null) {
                            List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateTransitionRule.getId());
                            stateTransitionRule.setActions(actions);
                        }
                        approvalMeta.setApproveActions(stateTransitionRule.getActions());
                    }
                    else if (rule.getName().equals("Reject")) {
                        approvalMeta.setRejectForm(stateTransitionRule.getForm());
                        if (stateTransitionRule.getActions() == null) {
                            List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateTransitionRule.getId());
                            stateTransitionRule.setActions(actions);
                        }
                        approvalMeta.setRejectActions(stateTransitionRule.getActions());
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.APPROVAL_RULE, approvalMeta);
        }
        return false;
    }
}
