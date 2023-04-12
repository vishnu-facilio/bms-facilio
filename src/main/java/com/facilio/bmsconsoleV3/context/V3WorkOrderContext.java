package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;

public class V3WorkOrderContext extends V3TicketContext {
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    private static Logger log = LogManager.getLogger(V3WorkOrderContext.class.getName());
    private User requester;

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    private Long createdTime;

    public Long getCreatedTime() {
        return createdTime;
    }

    public String toString() {
        return this.getId() + "";
    }

    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setCreatedTime(String createdTime) {
        if (createdTime != null && !createdTime.isEmpty()) {
            try {
                this.createdTime = Long.valueOf(createdTime);
            }
            catch(NumberFormatException ex) {
                try {
                    this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
                } catch (ParseException e) {
                    try {
                        this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
                    } catch (ParseException e1) {
                        log.info("Exception occurred ", e1);
                    }
                }
            }
        }
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedTimeString() {
        if (createdTime != null) {
            return DateTimeUtil.getZonedDateTime(createdTime).format(DateTimeUtil.READABLE_DATE_FORMAT);
        }
        return null;
    }

    private Long modifiedTime;

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    private Boolean isWorkDurationChangeAllowed;

    public Boolean getIsWorkDurationChangeAllowed() {
        return isWorkDurationChangeAllowed;
    }

    public void setIsWorkDurationChangeAllowed(Boolean isWorkDurationChangeAllowed) {
        this.isWorkDurationChangeAllowed = isWorkDurationChangeAllowed;
    }

    public Boolean isWorkDurationChangeAllowed() {
        if (isWorkDurationChangeAllowed != null) {
            return isWorkDurationChangeAllowed.booleanValue();
        }
        return false;
    }

    private PreventiveMaintenance pm;

    public PreventiveMaintenance getPm() {
        return pm;
    }

    public void setPm(PreventiveMaintenance pm) {
        this.pm = pm;
    }

    @Getter
    @Setter
    private Long pmV2;

    @Getter
    @Setter
    private Long pmResourcePlanner;

    @Getter
    @Setter
    private Long pmTriggerV2;

    @Getter
    @Setter
    private Long pmPlanner;

    public String getUrl() {
//		return "http://"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".fazilio.com/app/workorders/open/summary/"+getId(); Removing subdomain temp
        if (super.getId() != -1) {
            if (approvalState == ApprovalState.REQUESTED) {
                // /app/wo/approvals/requested/summary/
                return FacilioProperties.getConfig("clientapp.url") + "/app/wo/approvals/summary/" + getId();
            } else {
                return FacilioProperties.getConfig("clientapp.url") + "/app/wo/orders/summary/" + getId();
            }
        } else {
            return null;
        }
    }

    public String getMobileUrl() {
        if (super.getId() != -1) {
            if (approvalState == ApprovalState.REQUESTED) {
                // /app/wo/approvals/requested/summary/
                return FacilioProperties.getConfig("clientapp.url") + "/mobile/approvals/summary/" + getId();
            } else {
                return FacilioProperties.getConfig("clientapp.url") + "/mobile/workorder/summary/" + getId();
            }
        } else {
            return null;
        }
    }


    private ApprovalState approvalState;

    public ApprovalState getApprovalStateEnum() {
        return approvalState;
    }

    public Integer getApprovalState() {
        if (approvalState != null) {
            return approvalState.getValue();
        }
        return null;
    }

    public void setApprovalState(Integer approvalState) {
        if(approvalState != null) {
            this.approvalState = ApprovalState.valueOf(approvalState);
        }
    }

    private StateFlowRuleContext stateFlowRule;

    public StateFlowRuleContext getStateFlowRule() {
        return stateFlowRule;
    }

    public void setStateFlowRule(StateFlowRuleContext stateFlowRule) {
        this.stateFlowRule = stateFlowRule;
    }

    private WorkflowRuleContext slaRule;

    public WorkflowRuleContext getSlaRule() {
        return slaRule;
    }

    public void setSlaRule(WorkflowRuleContext slaRule) {
        this.slaRule = slaRule;
    }

    private Long approvalRuleId;

    public Long getApprovalRuleId() {
        return approvalRuleId;
    }

    public void setApprovalRuleId(Long approvalRuleId) {
        this.approvalRuleId = approvalRuleId;
    }

    private ApprovalRuleContext approvalRule;

    public ApprovalRuleContext getApprovalRule() {
        return approvalRule;
    }

    public void setApprovalRule(ApprovalRuleContext approvalRule) {
        this.approvalRule = approvalRule;
    }

    private List<ApproverContext> waitingApprovals;

    public List<ApproverContext> getWaitingApprovals() {
        return waitingApprovals;
    }

    public void setWaitingApprovals(List<ApproverContext> waitingApprovals) {
        this.waitingApprovals = waitingApprovals;
    }

    private User requestedBy;

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    private Boolean sendForApproval;

    public Boolean getSendForApproval() {
        return sendForApproval;
    }

    public void setSendForApproval(Boolean sendForApproval) {
        this.sendForApproval = sendForApproval;
    }

    public Boolean sendForApproval() {
        if (sendForApproval != null) {
            return sendForApproval.booleanValue();
        }
        return false;
    }

    private AlarmContext alarm;

    public AlarmContext getAlarm() {
        return alarm;
    }

    public void setAlarm(AlarmContext alarm) {
        this.alarm = alarm;
    }

    private Boolean prerequisiteEnabled;

    public Boolean getPrerequisiteEnabled() {
        return prerequisiteEnabled;
    }

    public void setPrerequisiteEnabled(Boolean prerequisiteEnabled) {
        this.prerequisiteEnabled = prerequisiteEnabled;
    }

    public Boolean isPrerequisiteEnabled() {
        if (prerequisiteEnabled != null) {
            return prerequisiteEnabled.booleanValue();
        }
        return false;
    }

    private Boolean photoMandatory;

    public Boolean getPhotoMandatory() {
        return photoMandatory;
    }

    public void setPhotoMandatory(Boolean photoMandatory) {
        this.photoMandatory = photoMandatory;
    }

    private Boolean isSignatureRequired;

    public Boolean getIsSignatureRequired() {
        return isSignatureRequired;
    }

    public void setIsSignatureRequired(Boolean isSignatureRequired) {
        this.isSignatureRequired = isSignatureRequired;
    }

    public Boolean isUserSignatureRequired() {
        if (isSignatureRequired != null) {
            return isSignatureRequired.booleanValue();
        }
        return false;
    }

    private Boolean markInactive;

    public Boolean getMarkInactive() {
        return markInactive;
    }

    public void setMarkInactive(Boolean markInactive) {
        this.markInactive = markInactive;
    }

    public Boolean markInactive() {
        if (markInactive != null) {
            return markInactive.booleanValue();
        }
        return false;
    }

    private Long signatureId;

    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    private String signatureUrl;

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    private File signature;

    public File getSignature() {
        return signature;
    }

    public void setSignature(File signature) {
        this.signature = signature;
    }

    private String signatureFileName;

    public String getSignatureFileName() {
        return signatureFileName;
    }

    public void setSignatureFileName(String signatureFileName) {
        this.signatureFileName = signatureFileName;
    }

    private String signatureContentType;

    public String getSignatureContentType() {
        return signatureContentType;
    }

    public void setSignatureContentType(String signatureContentType) {
        this.signatureContentType = signatureContentType;
    }

    private V3WorkOrderContext parentWO;

    public V3WorkOrderContext getParentWO() {
        return parentWO;
    }

    public void setParentWO(V3WorkOrderContext parentWO) {
        this.parentWO = parentWO;
    }

    private Long responseDueDate;

    public Long getResponseDueDate() {
        return responseDueDate;
    }

    public void setResponseDueDate(Long responseDueDate) {
        this.responseDueDate = responseDueDate;
    }

    private V3WorkOrderContext.WOUrgency urgency;

    public V3WorkOrderContext.WOUrgency getUrgencyEnum() {
        return urgency;
    }

    public Integer getUrgency() {
        if (urgency != null) {
            return urgency.getValue();
        }
        return null;
    }

    public void setUrgency(Integer urgencyval) {
        if(urgencyval != null) {
            this.urgency = V3WorkOrderContext.WOUrgency.valueOf(urgencyval);
        }

    }

    public Long getWoCreationOffset() {
        return woCreationOffset;
    }

    public void setWoCreationOffset(Long woCreationOffset) {
        this.woCreationOffset = woCreationOffset;
    }

    public static enum WOUrgency {
        NOTURGENT,
        URGENT,
        EMERGENCY;

        public int getValue() {
            return ordinal() + 1;
        }

        public static V3WorkOrderContext.WOUrgency valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Double totalCost;

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public PMTriggerContext getTrigger() {
        return trigger;
    }

    public void setTrigger(PMTriggerContext trigger) {
        this.trigger = trigger;
    }

    private PMTriggerContext trigger;

    private V3WorkOrderContext.JobsStatus status;

    public V3WorkOrderContext.JobsStatus getJobStatusEnum() {
        return status;
    }

    public Integer getJobStatus() {
        if (status != null) {
            return status.getValue();
        }
        return null;
    }

    public void setJobStatus(Integer status) {
        if(status != null) {
            this.status = V3WorkOrderContext.JobsStatus.valueOf(status);
        }
    }

    public enum JobsStatus {
        ACTIVE,
        SCHEDULED,
        COMPLETED,
        IN_ACTIVE;

        public int getValue() {
            return ordinal() + 1;
        }

        public static V3WorkOrderContext.JobsStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private V3WorkOrderContext.PreRequisiteStatus preRequestStatus;

    public V3WorkOrderContext.PreRequisiteStatus getPreRequestStatusEnum() {
        return preRequestStatus;
    }

    public Integer getPreRequestStatus() {
        if (preRequestStatus != null) {
            return preRequestStatus.getValue();
        }
        return null;
    }

    public String getPreRequestStatusVal() {
        if (preRequestStatus != null) {
            return preRequestStatus.getStringVal();
        }
        return null;
    }

    public void setPreRequestStatus(Integer preRequestStatus) {
        if(preRequestStatus != null) {
            this.preRequestStatus = V3WorkOrderContext.PreRequisiteStatus.valueOf(preRequestStatus);
        }
    }

    public enum PreRequisiteStatus {
        PENDING(1, "Pending"),
        COMPLETED_WITH_NEGATIVE(2, "Completed With Negative values"),
        COMPLETED(3, "Completed"),
        NOT_STARTED(4, "Not started");

        private int value;
        private String strVal;

        private PreRequisiteStatus(int value, String strVal) {
            // TODO Auto-generated constructor stub
            this.value = value;
            this.strVal = strVal;
        }

        public static V3WorkOrderContext.PreRequisiteStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        public int getValue() {
            // TODO Auto-generated method stub
            return value;
        }

        public String getStringVal() {
            return strVal;
        }
    }

    private Boolean preRequisiteApproved;

    public Boolean getPreRequisiteApproved() {
        return preRequisiteApproved;
    }

    public void setPreRequisiteApproved(Boolean preRequisiteApproved) {
        this.preRequisiteApproved = preRequisiteApproved;
    }

    private V3WorkOrderContext.AllowNegativePreRequisite allowNegativePreRequisite;

    public V3WorkOrderContext.AllowNegativePreRequisite getAllowNegativePreRequisiteEnum() {
        return allowNegativePreRequisite;
    }

    public void setAllowNegativePreRequisite(Integer allowNegativePreRequisite) {
        if(allowNegativePreRequisite != null) {
            this.allowNegativePreRequisite = V3WorkOrderContext.AllowNegativePreRequisite.valueOf(allowNegativePreRequisite);
        }
    }

    public Integer getAllowNegativePreRequisite() {
        if (allowNegativePreRequisite != null) {
            return allowNegativePreRequisite.getValue();
        }
        return null;
    }

    public enum AllowNegativePreRequisite {
        NO(1),
        YES_WITH_WARNING(2),
        YES_WITH_APPROVAL(3);
        private int value;

        private AllowNegativePreRequisite(int value) {
            // TODO Auto-generated constructor stub
            this.value = value;
        }

        public static V3WorkOrderContext.AllowNegativePreRequisite valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        public int getValue() {
            // TODO Auto-generated method stub
            return value;
        }

    }

    private Long syncTime;

    public Long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Long syncTime) {
        this.syncTime = syncTime;
    }

    private Boolean qrEnabled;


    public Boolean getQrEnabled() {
        return qrEnabled;
    }

    public void setQrEnabled(Boolean qrEnabled) {
        this.qrEnabled = qrEnabled;
    }

    Boolean prerequisiteApprover;

    public Boolean isPrerequisiteApprover() throws Exception {
        return prerequisiteApprover;
    }

    public void setPrerequisiteApprover(Boolean prerequisiteApprover) {
        this.prerequisiteApprover = prerequisiteApprover;
    }

    private Boolean workPermitNeeded;

    public Boolean getWorkPermitNeeded() {
        return workPermitNeeded;
    }

    public void setWorkPermitNeeded(Boolean workPermitNeeded) {
        this.workPermitNeeded = workPermitNeeded;
    }

    private Boolean workPermitIssued;

    public Boolean getWorkPermitIssued() {
        return workPermitIssued;
    }

    public void setWorkPermitIssued(Boolean workPermitIssued) {
        this.workPermitIssued = workPermitIssued;
    }

    private Long woCreationOffset;

    private SafetyPlanContext safetyPlan;

    public SafetyPlanContext getSafetyPlan() {
        return safetyPlan;
    }

    public void setSafetyPlan(SafetyPlanContext safetyPlan) {
        this.safetyPlan = safetyPlan;
    }

    private List<Long> hazardIds;

    public List<Long> getHazardIds() {
        return hazardIds;
    }

    public void setHazardIds(List<Long> hazardIds) {
        this.hazardIds = hazardIds;
    }

    private ClientContext client;

    public ClientContext getClient() {
        return client;
    }

    public void setClient(ClientContext client) {
        this.client = client;
    }

    private String deviationTaskUniqueId;

    public String getDeviationTaskUniqueId() {
        return deviationTaskUniqueId;
    }

    public void setDeviationTaskUniqueId(String deviationTaskUniqueId) {
        this.deviationTaskUniqueId = deviationTaskUniqueId;
    }

    private String taskString;

    public String getTaskString() {
        return taskString;
    }

    public void setTaskString(String taskString) {
        this.taskString = taskString;
    }

    private List<V3TaskContext> tasksList;

    public List<V3TaskContext> getTasksList() {
        return tasksList;
    }

    public void setTasksList(List<V3TaskContext> tasksList) {
        this.tasksList = tasksList;
    }

    public Boolean getIsQuotationNeeded() {
        return isQuotationNeeded;
    }

    public void setIsQuotationNeeded(Boolean quotationNeeded) {
        isQuotationNeeded = quotationNeeded;
    }

    public Boolean isQuotationNeeded() {
        if(isQuotationNeeded != null) {
            return isQuotationNeeded.booleanValue();
        }
        return false;
    }

    private Boolean isQuotationNeeded;

    public Boolean getIsQuotationApproved() {
        return isQuotationApproved;
    }

    public void setIsQuotationApproved(Boolean quotationApproved) {
        isQuotationApproved = quotationApproved;
    }

    public Boolean isQuotationApproved() {
        if (isQuotationApproved != null) {
            return isQuotationApproved.booleanValue();
        }
        return false;
    }

    private Boolean isQuotationApproved;

    @Getter
    @Setter
    private JobPlanContext jobPlan;

    @Getter
    @Setter
    private JobPlanContext adhocJobPlan;

    @Getter
    @Setter
    private JobPlanContext prerequisiteJobPlan;

    public LinkedHashMap<String, List<V3TaskContext>> getTasksString() {
        return tasksString;
    }

    public void setTasksString(LinkedHashMap<String, List<V3TaskContext>> tasksString) {
        this.tasksString = tasksString;
    }

    private LinkedHashMap<String, List<V3TaskContext>> tasksString;

    @Getter
    @Setter
    private RoutesContext route;

    @Getter
    @Setter
    private List<String> sectionNameList;

}
