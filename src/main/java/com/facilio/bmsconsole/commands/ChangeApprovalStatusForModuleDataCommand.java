package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ChangeApprovalStatusForModuleDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
        Long approvalTransitionId = (Long) context.get(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<ModuleBaseWithCustomFields> moduleRecord = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(moduleRecord) && approvalTransitionId != null) {
            ApprovalStateTransitionRuleContext approvalTransition = (ApprovalStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(approvalTransitionId);
            if (approvalTransition == null) {
                throw new IllegalArgumentException("Invalid approval transition");
            }
            // skip approval check for update, if approvalTransition is valid
            context.put(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, true);

            for (ModuleBaseWithCustomFields record : moduleRecord) {
                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
                boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(approvalTransition,
                        moduleName, record, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, record.getId()),
                        recordPlaceHolders, (FacilioContext) context, false);
                if (shouldChangeState) {
                    FacilioStatus newState = StateFlowRulesAPI.getStateContext(approvalTransition.getToStateId());
                    if (newState == null) {
                        throw new Exception("Invalid state");
                    }
                    approvalTransition.executeTrueActions(record, context, recordPlaceHolders);
                }
                else {
                    throw new IllegalArgumentException("Invalid permission to approve");
                }
            }
        }
        return false;
    }
}
