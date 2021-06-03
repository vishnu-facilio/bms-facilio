package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApproverWorkflowRuleContext extends WorkflowRuleContext {

    private SharingContext<ApproverContext> approvers;
    public SharingContext<ApproverContext> getApprovers() {
        return approvers;
    }
    public void setApprovers(SharingContext<ApproverContext> approvers) {
        if (approvers != null) {
            this.approvers = new SharingContext<>(approvers);
        }
    }
    public void addApprover (ApproverContext approver) {
        if (this.approvers == null) {
            this.approvers = new SharingContext<>(approvers);
        }
        approvers.add(approver);
    }

    public boolean hasApprovalPermission(Object object) throws Exception {
        if (approvers == null) {
            return true;
        }
        else {
            return approvers.isAllowed(object);
        }
    }

    private Boolean allApprovalRequired = false;
    public Boolean getAllApprovalRequired() {
        return allApprovalRequired;
    }
    public void setAllApprovalRequired(Boolean allApprovalRequired) {
        this.allApprovalRequired = allApprovalRequired;
    }
    public void setAllApprovalRequired(boolean allApprovalRequired) {
        this.allApprovalRequired = allApprovalRequired;
    }
    public boolean isAllApprovalRequired() {
        if (allApprovalRequired != null) {
            return allApprovalRequired.booleanValue();
        }
        return false;
    }

    private ApprovalRuleContext.ApprovalOrder approvalOrder;
    public ApprovalRuleContext.ApprovalOrder getApprovalOrderEnum() {
        return approvalOrder;
    }
    public int getApprovalOrder() {
        if (approvalOrder != null) {
            return approvalOrder.getValue();
        }
        return -1;
    }
    public void setApprovalOrder(int approvalOrder) {
        this.approvalOrder = ApprovalRuleContext.ApprovalOrder.valueOf(approvalOrder);
    }
    public void setApprovalOrder(ApprovalRuleContext.ApprovalOrder approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    private List<ValidationContext> validations;
    public List<ValidationContext> getValidations() {
        return validations;
    }
    public void setValidations(List<ValidationContext> validations) {
        this.validations = validations;
    }

    private List<ConfirmationDialogContext> confirmationDialogs;
    public List<ConfirmationDialogContext> getConfirmationDialogs() {
        return confirmationDialogs;
    }
    public void setConfirmationDialogs(List<ConfirmationDialogContext> confirmationDialogs) {
        this.confirmationDialogs = confirmationDialogs;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        boolean result = true;
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        if (CollectionUtils.isNotEmpty(approvers)) {
            List<SingleSharingContext> matching = approvers.getMatching(record);

            List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(moduleRecord, matching);
            if (getApprovalOrderEnum() != null && getApprovalOrderEnum() == ApprovalRuleContext.ApprovalOrder.SEQUENTIAL && CollectionUtils.isNotEmpty(checkAnyPendingApprovers)) {
                // if sequential, check only next user
                checkAnyPendingApprovers = Collections.singletonList(checkAnyPendingApprovers.get(0));
            }
            List<SingleSharingContext> matchingAgain = new SharingContext<>(checkAnyPendingApprovers).getMatching(record);
            result = CollectionUtils.isNotEmpty(matchingAgain);
        }
        return result;
    }

    protected boolean validationCheck(ModuleBaseWithCustomFields moduleRecord, Context context, Map<String, Object> placeHolders) throws Exception {
        if (CollectionUtils.isNotEmpty(validations)) {
            for (ValidationContext validation : validations) {
                if (!validation.validate(moduleRecord, context, placeHolders)) {
                    throw new IllegalArgumentException(validation.getResolvedErrorMessage(moduleRecord));
                }
            }
        }
        return true;
    }

    private static List<Long> fetchPreviousApprovers(long recordId, long ruleId) throws Exception {
        FacilioModule module = ModuleFactory.getApprovalStepsModule();
        List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField recordIdField = fieldMap.get("recordId");
        FacilioField ruleIdField = fieldMap.get("ruleId");

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(ruleId), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(recordId), PickListOperators.IS))
                ;

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            return props.stream().map(p -> (Long) p.get("approverGroup")).collect(Collectors.toList());
        }
        return null;
    }
    
    protected boolean validateApproversForTrueAction(Object record) throws Exception {
        boolean shouldExecuteTrueActions = true;
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        if (CollectionUtils.isNotEmpty(approvers)) {
            List<SingleSharingContext> matching = approvers.getMatching(record);
            ApprovalRuleContext.addApprovalStep(moduleRecord.getId(), null, matching, this);

            List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(moduleRecord, matching);
            if (isAllApprovalRequired()) {
                removeAnySkippedPendingApprovals(checkAnyPendingApprovers, moduleRecord);
                shouldExecuteTrueActions = CollectionUtils.isEmpty(checkAnyPendingApprovers);
                if (shouldExecuteTrueActions) {
                    deletePreviousApprovalSteps(moduleRecord.getId());
                }
            }
        }
        return shouldExecuteTrueActions;
    }

    public void skipAnyPendingApprovals(ModuleBaseWithCustomFields record) throws Exception {
        if (isAllApprovalRequired()) {
            List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(record, null);
            removeAnySkippedPendingApprovals(checkAnyPendingApprovers, record);
        }
    }

    private void removeAnySkippedPendingApprovals(List<SingleSharingContext> checkAnyPendingApprovers, ModuleBaseWithCustomFields record) throws Exception {
        if (CollectionUtils.isNotEmpty(checkAnyPendingApprovers) && CollectionUtils.isNotEmpty(approvers)) {
            List<SingleSharingContext> skippedPendingApprovals = checkAnyPendingApprovers.stream().filter(singleSharingContext -> {
                try {
                    return singleSharingContext.shouldSkip(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(skippedPendingApprovals)) {
                // no one to be skipped
                return;
            }

            if (approvers.size() == skippedPendingApprovals.size()) {
                throw new IllegalArgumentException("At-least one approval should not be skipped");
            }

            if (getApprovalOrderEnum() == ApprovalRuleContext.ApprovalOrder.SEQUENTIAL) {
                ApprovalRuleContext.addApprovalStep(record.getId(), null, skippedPendingApprovals, this);
            }
            checkAnyPendingApprovers.removeAll(skippedPendingApprovals);
        }
    }

    private int deletePreviousApprovalSteps(long id) throws Exception {
        FacilioModule module = ModuleFactory.getApprovalStepsModule();
        List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField ruleIdField = fieldMap.get("ruleId");
        FacilioField recordIdField = fieldMap.get("recordId");

        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(getId()), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(id), PickListOperators.IS))
                ;
        return deleteBuilder.delete();
    }

    public List<SingleSharingContext> checkAnyPendingApprovers(ModuleBaseWithCustomFields moduleRecord, List<SingleSharingContext> matching) throws Exception {
        if (isAllApprovalRequired()) {
            List<Long> previousApprovers = fetchPreviousApprovers(moduleRecord.getId(), getId());
            Map<Long, SingleSharingContext> approverMap = approvers.stream().collect(Collectors.toMap(SingleSharingContext::getId, Function.identity()));
            if (previousApprovers != null && !previousApprovers.isEmpty()) {
                for (Long id : previousApprovers) {
                    approverMap.remove(id);
                }
            }
            return new ArrayList<>(approverMap.values());
        }
        else {
            return matching;	// there is not pending approvers
        }
    }
}
