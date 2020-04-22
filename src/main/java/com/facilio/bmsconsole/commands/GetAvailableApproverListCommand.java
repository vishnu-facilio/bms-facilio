package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetAvailableApproverListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioStatus currentState = (FacilioStatus) context.get("currentState");
        if (currentState != null) {
            List<WorkflowRuleContext> availableStates = (List<WorkflowRuleContext>) context.get("availableStates");
            ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
            WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(moduleData.getApprovalFlowId());
            context.put(FacilioConstants.ContextNames.APPROVAL_RULE, workflowRule);
            if (CollectionUtils.isEmpty(availableStates)) {
                Map<String, Long> map = new HashMap<>();
                map.put("fromState", currentState.getId());
                map.put("stateFlowId", moduleData.getApprovalFlowId());

                List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getStateTransitions(Collections.singletonList(map), AbstractStateTransitionRuleContext.TransitionType.NORMAL);
                if (CollectionUtils.isNotEmpty(stateTransitions)) {
                    ApproverWorkflowRuleContext approverWorkflowRuleContext = (ApproverWorkflowRuleContext) stateTransitions.get(0);
                    if (CollectionUtils.isEmpty(approverWorkflowRuleContext.getApprovers())) {
                        approverWorkflowRuleContext.setApprovers(SharingAPI.getSharing(approverWorkflowRuleContext.getId(), ModuleFactory.getApproversModule(), ApproverContext.class));
                    }

                    if (CollectionUtils.isNotEmpty(approverWorkflowRuleContext.getApprovers())) {
                        List<SingleSharingContext> list = approverWorkflowRuleContext.checkAnyPendingApprovers(moduleData, (List) approverWorkflowRuleContext.getApprovers());
                        if (CollectionUtils.isNotEmpty(list) && approverWorkflowRuleContext.getApprovalOrderEnum() == ApprovalRuleContext.ApprovalOrder.SEQUENTIAL) {
                            list = Collections.singletonList(list.get(0));
                        }
                        if (CollectionUtils.isNotEmpty(list)) {
                            context.put(FacilioConstants.ContextNames.PENDING_APPROVAL_LIST, SharingContext.getSharingDetails(list, moduleData));
                        }
                    }
                }
            }
        }
        return false;
    }
}
