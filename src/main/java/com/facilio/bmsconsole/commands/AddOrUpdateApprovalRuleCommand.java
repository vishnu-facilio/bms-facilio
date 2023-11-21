package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddOrUpdateApprovalRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApprovalRuleMetaContext approvalMeta = (ApprovalRuleMetaContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (approvalMeta != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            if (CollectionUtils.isEmpty(approvalMeta.getApprovers())) {
                throw new IllegalArgumentException("Approvers cannot be empty");
            }


            // Getting necessary status for approval state-transition
            FacilioStatus rejected = TicketAPI.getApprovalStatus("Rejected");
            if (rejected == null) {
                throw new IllegalArgumentException("No Rejected status is found");
            }
            FacilioStatus requested = TicketAPI.getApprovalStatus("Requested");
            if (requested == null) {
                throw new IllegalArgumentException("No requested status is found");
            }
            FacilioStatus exitStatus = TicketAPI.getApprovalStatus("Approved");
            if (exitStatus == null || exitStatus.getType() != FacilioStatus.StatusType.EXIT) {
                throw new IllegalArgumentException("No Exit status is found");
            }

            // Creating state-flow
            ApprovalStateFlowRuleContext approvalStateFlow = new ApprovalStateFlowRuleContext();
            approvalStateFlow.setName(approvalMeta.getName());
            approvalStateFlow.setStatus(approvalMeta.getStatus()!=null?approvalMeta.getStatus():true);
            approvalStateFlow.setDescription(approvalMeta.getDescription());
            approvalStateFlow.setRuleType(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW);
            if (approvalMeta.getEventTypeEnum() == null) {
                // temporary fix - remove when client supports it
                approvalMeta.setEventType(EventType.FIELD_CHANGE);
            }
            approvalStateFlow.setActivityType(approvalMeta.getEventTypeEnum());
            approvalStateFlow.setModule(module);
            approvalStateFlow.setDefaltStateFlow(false);
            approvalStateFlow.setCriteria(approvalMeta.getCriteria());
            approvalStateFlow.setDefaultStateId(requested.getId());
            approvalStateFlow.setId(approvalMeta.getId());
            approvalStateFlow.setConfigJson(approvalMeta.getConfigJson());
            approvalStateFlow.setActions(approvalMeta.getApprovalEntryActions());

            List<FieldChangeFieldContext> changeFields = new ArrayList<>();

            if (approvalMeta.getEventTypeEnum() == EventType.FIELD_CHANGE) {
                for (Long fieldId : approvalMeta.getFieldIds()) {
                    Map<Long, FacilioField> fieldMap = FieldFactory.getAsIdMap(modBean.getAllFields(moduleName));
                    if (!fieldMap.containsKey(fieldId)) {
                        throw new IllegalArgumentException("Invalid field");
                    }
                    FieldChangeFieldContext changeField = new FieldChangeFieldContext();
                    changeField.setFieldId(fieldId);
                    changeFields.add(changeField);
                }
                approvalStateFlow.setFields(changeFields);
            }

            boolean add = false;
            if (approvalMeta.getId() <= 0) {
                add = true;
            }

            FacilioChain ruleChain;
            if (add) {
                ruleChain = TransactionChainFactory.addWorkflowRuleChain();
                approvalStateFlow.setExecutionOrder(0);
            }
            else {
                ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
            }
            FacilioContext ruleContext = ruleChain.getContext();
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, approvalStateFlow);
            ruleChain.execute();

            if (add) {
                approvalMeta.setId(approvalStateFlow.getId());
                StateFlowRulesAPI.updateStateTransitionExecutionOrder(module, WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW);
            }

            long approvalStateFlowId = approvalStateFlow.getId();
            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(approvalStateFlowId);
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                WorkflowRuleAPI.deleteWorkFlowRules(
                        allStateTransitionList.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList())
                );
            }

            ApprovalStateTransitionRuleContext transitionRuleContext =
                    getApprovalTransition(approvalMeta.getApprovers(), approvalMeta.getApprovalOrder(),
                            approvalMeta.isAllApprovalRequired(), approvalMeta.getApprovalForm(), approvalMeta.getApprovalFormId(), approvalMeta.isShouldFormInterfaceApply(),
                            approvalMeta.getApprovalDialogTypeEnum(),
                            "Approve", module, approvalMeta.getApproveActions(), approvalStateFlowId,approvalMeta.getStatus());
            transitionRuleContext.setFromStateId(requested.getId());
            transitionRuleContext.setToStateId(exitStatus.getId());
            transitionRuleContext.setButtonType(1);
            ruleChain = TransactionChainFactory.addWorkflowRuleChain();
            ruleContext = ruleChain.getContext();
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, transitionRuleContext);
            ruleChain.execute();

            ruleChain = TransactionChainFactory.addWorkflowRuleChain();
            ruleContext = ruleChain.getContext();
            transitionRuleContext =
                    getApprovalTransition(approvalMeta.getApprovers(), approvalMeta.getApprovalOrder(),
                            false, approvalMeta.getRejectForm(), approvalMeta.getRejectFormId(), approvalMeta.isShouldFormInterfaceApply(),
                            approvalMeta.getRejectDialogTypeEnum(),
                            "Reject", module, approvalMeta.getRejectActions(), approvalStateFlowId,approvalMeta.getStatus());
            transitionRuleContext.setFromStateId(requested.getId());
            transitionRuleContext.setToStateId(rejected.getId());
            transitionRuleContext.setButtonType(2);
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, transitionRuleContext);
            ruleChain.execute();

            ruleChain = TransactionChainFactory.addWorkflowRuleChain();
            ruleContext = ruleChain.getContext();
            transitionRuleContext =
                    getApprovalTransition(approvalMeta.getResendApprovers(), ApprovalRuleContext.ApprovalOrder.PARALLEL.getValue(),
                            false, null, approvalMeta.getResendFormId(), true, null, "Re-Send", module,
                            approvalMeta.getResendActions(), approvalStateFlowId,approvalMeta.getStatus());
            transitionRuleContext.setFromStateId(rejected.getId());
            transitionRuleContext.setToStateId(requested.getId());
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, transitionRuleContext);
            ruleChain.execute();
        }
        return false;
    }

    private ApprovalStateTransitionRuleContext getApprovalTransition(List<ApproverContext> approvers, int approvalOrder,
                                                                     boolean allApprovalRequired, FacilioForm form, long formId, boolean shouldFormInterfaceApply,
                                                                     AbstractStateTransitionRuleContext.DialogType dialogType, String name, FacilioModule module, List<ActionContext> actions, long stateFlowId,Boolean ruleStatus) {
        ApprovalStateTransitionRuleContext transitionRuleContext = new ApprovalStateTransitionRuleContext();
        transitionRuleContext.setRuleType(WorkflowRuleContext.RuleType.APPROVAL_STATE_TRANSITION);
        transitionRuleContext.setApprovers((SharingContext<ApproverContext>) approvers);
        transitionRuleContext.setApprovalOrder(approvalOrder);
        transitionRuleContext.setAllApprovalRequired(allApprovalRequired);
        transitionRuleContext.setForm(form);
        transitionRuleContext.setFormId(formId);
        transitionRuleContext.setStatus(ruleStatus!=null?ruleStatus:true);
        transitionRuleContext.setShouldFormInterfaceApply(shouldFormInterfaceApply);
        transitionRuleContext.setDialogType(dialogType);
        transitionRuleContext.setName(name);
        transitionRuleContext.setModule(module);
        transitionRuleContext.setActions(actions);
        transitionRuleContext.setActivityType(EventType.STATE_TRANSITION);
        transitionRuleContext.setStateFlowId(stateFlowId);
        transitionRuleContext.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
        return transitionRuleContext;
    }
}
