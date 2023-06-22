package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.fw.validators.DateTime;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String subject;
    private String description;
    private SiteContext site;
    private ClientContext client;
    private ServiceOrderMaintenanceType maintenancetype;
    private ServiceOrderCategory category;
    private ServiceOrderPriority priority;
    private SpaceContext space;
    private AssetContext asset;
    private ServiceOrderStatus status;
    private VendorContext vendor;
    private DateTime preferredstarttime;
    private DateTime preferredendtime;
    private Boolean autocreatesa;
    private DateTime responseduedate;
    private DateTime resolutionduedate;
    private DateTime resolvedtime;
    private StateFlowRuleContext stateflow;
    private ServiceOrderSourceType sourceType;
    private V3PeopleContext createdby;
    private DateTime createdtime;
    private V3PeopleContext modifiedby;
    private DateTime modifiedtime;
    private V3PeopleContext sysdeletedby;
    private DateTime sysdeletedtime;
    private Boolean sysdeleted;
    private DateTime actualstarttime;
    private DateTime actualendtime;
    private DateTime actualduration;
    private V3PeopleContext assignedto;
    private V3PeopleContext assignedby;
    private DateTime duedate;
    private DateTime estimatedstarttime;
    private DateTime estimatedendtime;
    private DateTime estimatedduration;
    private Long modulestate;
    private Long noOfAttachments;
    private Long noOfClosedTasks;
    private Long noOfNotes;
    private Long noOfTasks;
    private SLAPolicyContext slapolicyid;
    private Long approvalruleid;
    private Long approvalState;
    private Long parentso;
    private PlannedMaintenance pm;
    private ServiceOrderPrerequisiteStatus prerequeststatus;
    private Boolean prerequisiteapproved;
    private Boolean prerequisiteenabled;
    private Boolean qrenabled;
    private V3PeopleContext requestedby;
    private Long localid;

    public int getPrerequeststatus() {
        if (prerequeststatus != null) {
            return prerequeststatus.getIndex();
        }
        return -1;
    }
    public void setPrerequeststatus(int prerequeststatus) {
        this.prerequeststatus = ServiceOrderPrerequisiteStatus.valueOf(prerequeststatus);
    }
    public ServiceOrderPrerequisiteStatus getServiceOrderPrerequisiteStatusEnum() {
        return prerequeststatus;
    }
    public void setPrerequeststatus(ServiceOrderPrerequisiteStatus prerequeststatus) {
        this.prerequeststatus = prerequeststatus;
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

    public int getMaintenancetype() {
        if (maintenancetype != null) {
            return maintenancetype.getIndex();
        }
        return -1;
    }
    public void setMaintenancetype(int maintenancetype) {
        this.maintenancetype = ServiceOrderMaintenanceType.valueOf(maintenancetype);
    }
    public ServiceOrderMaintenanceType getServiceOrderMaintenanceTypeEnum() {
        return maintenancetype;
    }
    public void setMaintenanceType(ServiceOrderMaintenanceType maintenancetype) {
        this.maintenancetype = maintenancetype;
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
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        CANNOT_COMPLETE("Cannot Complete"),
        COMPLETED("Completed"),
        CLOSED("Closed"),
        CANCELLED("Cancelled");

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

}
