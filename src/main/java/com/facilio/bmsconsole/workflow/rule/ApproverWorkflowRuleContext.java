package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
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
    public void setApprovalOrder(ApprovalRuleContext.ApprovalOrder approvalOrder) {
        this.approvalOrder = approvalOrder;
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

    private List<ValidationContext> validations;
    public List<ValidationContext> getValidations() {
        return validations;
    }
    public void setValidations(List<ValidationContext> validations) {
        this.validations = validations;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        boolean result = true;
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        if (CollectionUtils.isNotEmpty(approvers)) {
            List<SingleSharingContext> matching = approvers.getMatching(record);

            List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(moduleRecord, matching);
            List<SingleSharingContext> matchingAgain = new SharingContext<>(checkAnyPendingApprovers).getMatching(record);
            result = CollectionUtils.isNotEmpty(matchingAgain);
        }
        return result;
    }

    protected boolean validationCheck(ModuleBaseWithCustomFields moduleRecord) throws Exception {
        if (CollectionUtils.isNotEmpty(validations)) {
            for (ValidationContext validation : validations) {
                if (!validation.validate(moduleRecord)) {
                    throw new IllegalArgumentException(validation.getErrorMessage());
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
                shouldExecuteTrueActions = CollectionUtils.isEmpty(checkAnyPendingApprovers);
            }
        }
        return shouldExecuteTrueActions;
    }

    private List<SingleSharingContext> checkAnyPendingApprovers(ModuleBaseWithCustomFields moduleRecord, List<SingleSharingContext> matching) throws Exception {
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
