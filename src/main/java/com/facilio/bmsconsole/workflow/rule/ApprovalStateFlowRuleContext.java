package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ApprovalStateFlowRuleContext extends AbstractStateFlowRuleContext {

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        // Skip if the existing approval flow is same as this
        if (moduleRecord.getApprovalFlowId() > -1 && moduleRecord.getApprovalFlowId() == getId()) {
            return;
        }

        moduleRecord.setApprovalFlowId(getId());
        FacilioStatus status = TicketAPI.getStatus(getDefaultStateId());
        moduleRecord.setApprovalStatus(status);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = getModule();

        List<FacilioField> fields = new ArrayList<>();
        FacilioField approvalFlowIdField = modBean.getField("approvalFlowId", module.getName());
        FacilioField approvalStatusField = modBean.getField("approvalStatus", module.getName());
        fields.add(approvalFlowIdField);
        fields.add(approvalStatusField);
        updateModuleRecordApprovalFields(module, fields, moduleRecord);

        // add activities
        ActivityType activityType = CommonActivityType.APPROVAL_ENTRY;
        JSONObject info = new JSONObject();
        info.put("status", status.getDisplayName());
        info.put("name", getName());
        info.put("enteredIntoApprovalProcess", true);
        info.put("ruleId", getId());
        if (AccountUtil.getCurrentUser() != null) {
            info.put("user", AccountUtil.getCurrentUser().getId());
        }
        CommonCommandUtil.addActivityToContext(moduleRecord.getId(), moduleRecord.getCurrentTime(), activityType, info, (FacilioContext) context);

        Map<String, Long> stateId = new HashMap<>();
        stateId.put("fromStateId", status.getId());
        stateId.put("stateFlowId", moduleRecord.getApprovalFlowId());
        List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getStateTransitions(Collections.singletonList(stateId), AbstractStateTransitionRuleContext.TransitionType.NORMAL);
        if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
            for (WorkflowRuleContext rule : allStateTransitionList) {
                if (rule instanceof ApproverWorkflowRuleContext) {
                    boolean noValidApproversFound = ((ApproverWorkflowRuleContext) rule).handleSkippablePendingApprovers(moduleRecord);
                    if (noValidApproversFound && rule instanceof ApprovalStateTransitionRuleContext) {
                        FacilioStatus toStatus = TicketAPI.getStatus(((ApprovalStateTransitionRuleContext) rule).getToStateId());
                        if (toStatus.getType() == FacilioStatus.StatusType.EXIT) {
                            // there is no valid approvers. so auto-approve this approval
                            moduleRecord.setApprovalFlowId(-99);
                            FacilioStatus facilioStatus = new FacilioStatus();
                            facilioStatus.setId(-99);
                            moduleRecord.setApprovalStatus(facilioStatus);
                            updateModuleRecordApprovalFields(module, fields, moduleRecord);

                            ((ApprovalStateTransitionRuleContext) rule).addActivity(moduleRecord, context, true);
                        }
                    }
                }
            }
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    private void updateModuleRecordApprovalFields(FacilioModule module, List<FacilioField> fields, ModuleBaseWithCustomFields moduleRecord) throws Exception {
        UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
        updateBuilder.update(moduleRecord);
    }
}
