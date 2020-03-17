package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.db.criteria.Criteria;

import java.io.Serializable;
import java.util.List;

public class ApprovalRuleMetaContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private List<Long> fieldIds;
    public List<Long> getFieldIds() {
        return fieldIds;
    }
    public void setFieldIds(List<Long> fieldIds) {
        this.fieldIds = fieldIds;
    }

    private SharingContext<ApproverContext> approvers;
    public List<ApproverContext> getApprovers() {
        return approvers;
    }
    public void setApprovers(SharingContext<ApproverContext> approvers) {
        this.approvers = approvers;
    }

    private Boolean allApprovalRequired;
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

    private List<ActionContext> approveActions;
    public List<ActionContext> getApproveActions() {
        return approveActions;
    }
    public void setApproveActions(List<ActionContext> approveActions) {
        this.approveActions = approveActions;
    }

    private List<ActionContext> rejectActions;
    public List<ActionContext> getRejectActions() {
        return rejectActions;
    }
    public void setRejectActions(List<ActionContext> rejectActions) {
        this.rejectActions = rejectActions;
    }

    private FacilioForm approvalForm;
    public FacilioForm getApprovalForm() {
        return approvalForm;
    }
    public void setApprovalForm(FacilioForm approvalForm) {
        this.approvalForm = approvalForm;
    }

    private FacilioForm rejectForm;
    public FacilioForm getRejectForm() {
        return rejectForm;
    }
    public void setRejectForm(FacilioForm rejectForm) {
        this.rejectForm = rejectForm;
    }
}
