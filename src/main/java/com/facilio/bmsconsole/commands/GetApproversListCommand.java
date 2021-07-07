package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApproverWorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetApproversListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApprovalRuleMetaContext approvalRule = (ApprovalRuleMetaContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        if (approvalRule != null && moduleRecord != null) {
            List<Map<String, Long>> stateIds = new ArrayList<>();
            Map<String, Long> stateId = new HashMap<>();
            stateId.put("stateFlowId", approvalRule.getId());
            stateId.put("fromStateId", moduleRecord.getApprovalStatus().getId());
            stateIds.add(stateId);
            List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getStateTransitions(stateIds, AbstractStateTransitionRuleContext.TransitionType.NORMAL);
            if (CollectionUtils.isNotEmpty(stateTransitions)) {
                Optional<WorkflowRuleContext> optional = stateTransitions.stream().filter(transition -> transition.getName().equals("Approve")).findFirst();
                if (optional.isPresent()) {
                    ApproverWorkflowRuleContext workflowRule = (ApproverWorkflowRuleContext) optional.get();

                    List<Map<String, Object>> previousSteps = ApprovalRulesAPI.getPreviousSteps(workflowRule.getId(), moduleRecord.getId());
                    Map<Long, Map<String, Object>> previousStepMap = previousSteps.stream().collect(Collectors.toMap(t -> (Long) t.get("approverGroup"), Function.identity()));

                    if (CollectionUtils.isNotEmpty(workflowRule.getApprovers())) {
                        SharingContext sharingContext = new SharingContext(workflowRule.getApprovers());
                        List<Map<String, Object>> sharingDetails = sharingContext.getSharingDetails(moduleRecord);
                        for (Map<String, Object> details : sharingDetails) {
                            Object id = details.get("approverGroup");
                            if (previousStepMap.containsKey(id)) {
                                details.putAll(previousStepMap.get(id));
                            }
                        }
                        context.put(FacilioConstants.ContextNames.APPROVAL_LIST, sharingDetails);
                    }
                }
            }
        }
        return false;
    }
}
