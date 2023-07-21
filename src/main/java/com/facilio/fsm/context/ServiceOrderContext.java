package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ServiceOrderContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private V3SiteContext site;
    private ClientContext client;
    private ServiceOrderMaintenanceType maintenanceType;
    private List<ServiceTaskContext> serviceTask;
    private ServiceOrderCategory category;
    private ServiceOrderPriority priority;
    private SpaceContext space;
    private AssetContext asset;
    private ServiceOrderStatus status;
    private ServiceOrderRequestResponseStatus requestDueStatus;
    private ServiceOrderRequestResponseStatus responseDueStatus;
    private VendorContext vendor;
    private Long preferredStartTime;
    private Long preferredEndTime;
    private Boolean autoCreateSa;
    private Boolean isAllSACompleted;
    private Boolean isTaskInitiated;
    private Long responseDueDate;
    private Long resolutionDueDate;
    private Long resolvedTime;
    private StateFlowRuleContext stateflow;
    private ServiceOrderSourceType sourceType;
    private V3PeopleContext createdBy;
    private Long createdTime;
    private V3PeopleContext modifiedBy;
    private Long modifiedTime;
    private V3PeopleContext sysdeletedby;
//    private DateTime sysDeletedTime;
    private Boolean sysDeleted;
    private Long actualStartTime;
    private Long actualEndTime;
    private Long actualDuration;
    private V3PeopleContext assignedTo;
    private V3PeopleContext assignedBy;
    private Long dueDate;
    private Long estimatedStartTime;
    private Long estimatedEndTime;
    private Long estimatedDuration;
//    private Long moduleState;
    private Long noOfAttachments;
    private Long noOfClosedTasks;
    private Long noOfNotes;
    private Long noOfTasks;
//    private SLAPolicyContext slaPolicyId;
    private Long approvalRuleId;
    private Long approvalState;
    private Long parentSo;
    private PlannedMaintenance pm;
    private ServiceOrderPrerequisiteStatus prerequestStatus;
    private Boolean prerequisiteApproved;
    private Boolean prerequisiteEnabled;
    private Boolean qrEnabled;
    private V3PeopleContext requestedBy;
    private V3PeopleContext fieldAgent;
    private TerritoryContext territory;
//    private Long localId;

    public int getPrerequeststatus() {
        if (prerequestStatus != null) {
            return prerequestStatus.getIndex();
        }
        return -1;
    }
    public void setPrerequeststatus(int prerequeststatus) {
        this.prerequestStatus = ServiceOrderPrerequisiteStatus.valueOf(prerequeststatus);
    }
    public ServiceOrderPrerequisiteStatus getServiceOrderPrerequisiteStatusEnum() {
        return prerequestStatus;
    }
    public void setPrerequeststatus(ServiceOrderPrerequisiteStatus prerequeststatus) {
        this.prerequestStatus = prerequeststatus;
    }

    public static enum ServiceOrderPrerequisiteStatus implements FacilioIntEnum {
        BREAKDOWN("Breakdown"),
        COMPLIANCE("Compliance"),
        CORRECTIVE("Corrective"),
        PREVENTIVE("Preventive"),
        ROUNDS("Rounds");

        private String name;

        ServiceOrderPrerequisiteStatus(String name) {
            this.name = name;
        }

        public static ServiceOrderPrerequisiteStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getMaintenanceType() {
        if (maintenanceType != null) {
            return maintenanceType.getIndex();
        }
        return -1;
    }
    public void setMaintenanceType(int maintenancetype) {
        this.maintenanceType = ServiceOrderMaintenanceType.valueOf(maintenancetype);
    }
    public ServiceOrderMaintenanceType getServiceOrderMaintenanceTypeEnum() {
        return maintenanceType;
    }
    public void setMaintenanceType(ServiceOrderMaintenanceType maintenancetype) {
        this.maintenanceType = maintenancetype;
    }

    public static enum ServiceOrderMaintenanceType implements FacilioIntEnum {
        BREAKDOWN("Breakdown"),
        COMPLIANCE("Compliance"),
        CORRECTIVE("Corrective"),
        PREVENTIVE("Preventive"),
        ROUNDS("Rounds");

        private String name;

        ServiceOrderMaintenanceType(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderMaintenanceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getCategory() {
        if (category != null) {
            return category.getIndex();
        }
        return -1;
    }
    public void setCategory(int category) {
        this.category = ServiceOrderCategory.valueOf(category);
    }
    public ServiceOrderCategory getServiceOrderCategoryEnum() {
        return category;
    }
    public void setCategory(ServiceOrderCategory categoryType) {
        this.category = categoryType;
    }

    public static enum ServiceOrderCategory implements FacilioIntEnum {
        ELECTRICAL("Electrical"),
        ENERGY("Energy"),
        FIRE_SAFETY("Fire Safety"),
        HOUSE_KEEPING("House Keeping"),
        HVAC("HVAC"),
        PLUMBING("Plumbing");

        private String name;

        ServiceOrderCategory(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderCategory valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getPriority() {
        if (priority != null) {
            return priority.getIndex();
        }
        return -1;
    }
    public void setPriority(int priority) {
        this.priority = ServiceOrderPriority.valueOf(priority);
    }
    public ServiceOrderPriority getServiceOrderPriorityEnum() {
        return priority;
    }
    public void setPriority(ServiceOrderPriority priorityType) {
        this.priority = priorityType;
    }

    public static enum ServiceOrderPriority implements FacilioIntEnum {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private String name;

        ServiceOrderPriority(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderPriority valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getSourceType() {
        if (sourceType != null) {
            return sourceType.getIndex();
        }
        return -1;
    }
    public void setSourceType(int sourceType) {
        this.sourceType = ServiceOrderSourceType.valueOf(sourceType);
    }
    public ServiceOrderSourceType getServiceOrderSourceTypeEnum() {
        return sourceType;
    }
    public void setSourceType(ServiceOrderSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public static enum ServiceOrderSourceType implements FacilioIntEnum {
        WEB("Web"),
        Mobile("Mobile");

        private String name;

        ServiceOrderSourceType(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderSourceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int sourceType) {
        this.status = ServiceOrderStatus.valueOf(sourceType);
    }
    public ServiceOrderStatus getServiceOrderStatusEnum() {
        return status;
    }
    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }

    public static enum ServiceOrderStatus implements FacilioIntEnum {
        NEW("New"),
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        CLOSED("Closed");
//        ON_HOLD("On Hold"),
//        CANNOT_COMPLETE("Cannot Complete"),



        private String name;

        ServiceOrderStatus(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public int getRequestDueStatus() {
        if (requestDueStatus != null) {
            return requestDueStatus.getIndex();
        }
        return -1;
    }
    public void setRequestDueStatus(int sourceType) {
        this.requestDueStatus = ServiceOrderRequestResponseStatus.valueOf(sourceType);
    }
    public ServiceOrderRequestResponseStatus getServiceRequestStatusEnum() {
        return requestDueStatus;
    }
    public void setRequestDueStatus(ServiceOrderRequestResponseStatus status) {
        this.requestDueStatus = status;
    }


    public int getResponseDueStatus() {
        if (responseDueStatus != null) {
            return responseDueStatus.getIndex();
        }
        return -1;
    }
    public void setResponseDueStatus(int sourceType) {
        this.responseDueStatus = ServiceOrderRequestResponseStatus.valueOf(sourceType);
    }
    public ServiceOrderRequestResponseStatus getServiceResponseStatusEnum() {
        return responseDueStatus;
    }
    public void setResponseDueStatus(ServiceOrderRequestResponseStatus status) {
        this.responseDueStatus = status;
    }


    public static enum ServiceOrderRequestResponseStatus implements FacilioIntEnum {
        NONE("None"),
        ON_TIME("On Time"),
        OVERDUE("Overdue");

        private String name;

        ServiceOrderRequestResponseStatus(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderRequestResponseStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public List<ServiceTaskContext> getServiceTask() {
        return serviceTask;
    }

    public void setServiceTask(List<ServiceTaskContext> serviceTask) {
        this.serviceTask = serviceTask;
    }
}
