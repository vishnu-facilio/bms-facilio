package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
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
    private V3ClientContext client;
    private V3TenantContext tenant;
    private ServiceOrderMaintenanceType maintenanceType;
    private List<ServiceTaskContext> serviceTask;
    private ServiceAppointmentContext.ServiceAppointmentCategory category;
    private PriorityContext priority;
    private V3BaseSpaceContext space;
    private V3AssetContext asset;
    private ServiceOrderTicketStatusContext status;
    private ServiceOrderRequestResponseStatus resolutionDueStatus;
    private VendorContext vendor;
    private Long preferredStartTime;
    private Long preferredEndTime;
    private boolean autoCreateSa;
    private Boolean isAllSACompleted;
    private Boolean isTaskInitiated;
    private Long resolutionDueDate;
    private Long resolutionDueDuration;
    private Long resolvedTime;
    private StateFlowRuleContext stateflow;
    private ServiceOrderSourceType sourceType;
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
//    private Long moduleState;
    private Long noOfAttachments;
    private Long noOfClosedTasks;
    private Long noOfNotes;
    private Long noOfTasks;
//    private SLAPolicyContext slaPolicyId;
    private Long approvalRuleId;
    private Long approvalState;
    private Long parentSo;
    private ServicePlannedMaintenanceContext servicePlannedMaintenance;
    private ServiceOrderPrerequisiteStatus prerequestStatus;
    private Boolean prerequisiteApproved;
    private Boolean prerequisiteEnabled;
    private Boolean qrEnabled;
    private V3PeopleContext requestedBy;
    private V3PeopleContext fieldAgent;
    private TerritoryContext territory;
    private ServicePMTriggerContext servicePMTrigger;
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
        this.category = ServiceAppointmentContext.ServiceAppointmentCategory.valueOf(category);
    }
    public ServiceAppointmentContext.ServiceAppointmentCategory getServiceOrderCategoryEnum() {
        return category;
    }
    public void setCategory(ServiceAppointmentContext.ServiceAppointmentCategory categoryType) {
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
        PLANNED("Planned"),
        REACTIVE("Reactive");

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

//    public int getStatus() {
//        if (status != null) {
//            return status.getIndex();
//        }
//        return -1;
//    }
//    public void setStatus(int sourceType) {
//        this.status = ServiceOrderStatus.valueOf(sourceType);
//    }
//    public ServiceOrderStatus getServiceOrderStatusEnum() {
//        return status;
//    }
//    public void setStatus(ServiceOrderStatus status) {
//        this.status = status;
//    }

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

    public int getResolutionDueStatus() {
        if (resolutionDueStatus != null) {
            return resolutionDueStatus.getIndex();
        }
        return -1;
    }
    public void setResolutionDueStatus(int sourceType) {
        this.resolutionDueStatus = ServiceOrderRequestResponseStatus.valueOf(sourceType);
    }
    public ServiceOrderRequestResponseStatus getServiceRequestStatusEnum() {
        return resolutionDueStatus;
    }
    public void setResolutionDueStatus(ServiceOrderRequestResponseStatus status) {
        this.resolutionDueStatus = status;
    }


    public static enum ServiceOrderRequestResponseStatus implements FacilioIntEnum {
        DUE("Due"),
        ON_TIME("On Time"),
        BREACHED("Breached");

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

    public V3AssetContext getAsset() {
        return asset;
    }

    public void setAsset(V3AssetContext asset) {
        this.asset = asset;
    }
}
