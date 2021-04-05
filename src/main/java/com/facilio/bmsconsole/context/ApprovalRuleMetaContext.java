package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.db.criteria.Criteria;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    private EventType eventType;
    public int getEventType() {
        if (eventType != null) {
            return eventType.getValue();
        }
        return -1;
    }
    public void setEventType(int eventType) {
        this.eventType = EventType.valueOf(eventType);;
    }
    public EventType getEventTypeEnum() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    private List<Long> fieldIds;
    public List<Long> getFieldIds() {
        return fieldIds;
    }
    public void setFieldIds(List<Long> fieldIds) {
        this.fieldIds = fieldIds;
    }

    private JSONObject configJson;
    public String getConfigJson() {
        if (configJson != null) {
            return configJson.toJSONString();
        }
        return null;
    }
    public void setConfigJson(String json) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                JSONParser parser = new JSONParser();
                configJson = (JSONObject) parser.parse(json);
            } catch (Exception e) {}
        }
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

    private SharingContext<ApproverContext> resendApprovers;
    public SharingContext<ApproverContext> getResendApprovers() {
        return resendApprovers;
    }
    public void setResendApprovers(SharingContext<ApproverContext> resendApprovers) {
        this.resendApprovers = resendApprovers;
    }

    private List<ActionContext> approvalEntryActions;
    public List<ActionContext> getApprovalEntryActions() {
        return approvalEntryActions;
    }
    public void setApprovalEntryActions(List<ActionContext> approvalEntryActions) {
        this.approvalEntryActions = approvalEntryActions;
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

    private AbstractStateTransitionRuleContext.DialogType approvalDialogType = AbstractStateTransitionRuleContext.DialogType.MODULE;
    public int getApprovalDialogType() {
        if (approvalDialogType != null) {
            return approvalDialogType.getIndex();
        }
        return -1;
    }
    public AbstractStateTransitionRuleContext.DialogType getApprovalDialogTypeEnum() {
        return approvalDialogType;
    }
    public void setApprovalDialogType(AbstractStateTransitionRuleContext.DialogType approvalDialogType) {
        this.approvalDialogType = approvalDialogType;
    }
    public void setApprovalDialogType(int approvalDialogTypeInt) {
        this.approvalDialogType = AbstractStateTransitionRuleContext.DialogType.valueOf(approvalDialogTypeInt);
    }

    private FacilioForm approvalForm;
    public FacilioForm getApprovalForm() {
        return approvalForm;
    }
    public void setApprovalForm(FacilioForm approvalForm) {
        this.approvalForm = approvalForm;
    }

    private AbstractStateTransitionRuleContext.DialogType rejectDialogType = AbstractStateTransitionRuleContext.DialogType.MODULE;
    public int getRejectDialogType() {
        if (rejectDialogType != null) {
            return rejectDialogType.getIndex();
        }
        return -1;
    }
    public AbstractStateTransitionRuleContext.DialogType getRejectDialogTypeEnum() {
        return rejectDialogType;
    }
    public void setRejectDialogType(AbstractStateTransitionRuleContext.DialogType rejectDialogType) {
        this.rejectDialogType = rejectDialogType;
    }
    public void setRejectDialogType(int approvalDialogTypeInt) {
        this.rejectDialogType = AbstractStateTransitionRuleContext.DialogType.valueOf(approvalDialogTypeInt);
    }
    private FacilioForm rejectForm;
    public FacilioForm getRejectForm() {
        return rejectForm;
    }
    public void setRejectForm(FacilioForm rejectForm) {
        this.rejectForm = rejectForm;
    }
}
