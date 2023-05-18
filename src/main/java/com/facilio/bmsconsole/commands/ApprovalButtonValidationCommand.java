package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalStateTransitionRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ApprovalButtonValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long approvalId = (Long) context.get(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (approvalId == null || approvalId <= 0){
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        ApprovalStateTransitionRuleContext approvalTransition = (ApprovalStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(approvalId);
        if (approvalTransition == null) {
            throw new IllegalArgumentException("Invalid approval transition");
        }

        context.put(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, true);

        if (CollectionUtils.isNotEmpty(records)){

        for (ModuleBaseWithCustomFields record : records) {

            if (record != null) {

                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
                boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(approvalTransition,
                        moduleName, record, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, record.getId()),
                        recordPlaceHolders, (FacilioContext) context, false);
                if (shouldChangeState) {
                    FacilioStatus newState = StateFlowRulesAPI.getStateContext(approvalTransition.getToStateId());
                    if (newState == null) {
                        throw new Exception("Invalid state");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid permission to approve");
                }

            }
         }

        }

        return false;
    }
}
