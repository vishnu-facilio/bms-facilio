package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class ServiceAppointmentContext extends V3Context {
    private String code;
    private String name;
    private String description;
    private ServiceOrderContext serviceOrder;
    private TerritoryContext territory;
    private List<ServiceAppointmentTaskContext> serviceTasks;
    private List<ServiceAppointmentSkillContext> skills;
    private Long scheduledStartTime;
    private Long scheduledEndTime;
    private Long estimatedDuration;
    private Long actualStartTime;
    private Long actualEndTime;
    private Long responseDueTime;
    private Long resolutionDueTime;
    private Boolean isDefault;
    private Long actualDuration;
    private LocationContextV3 location;
    private V3SiteContext site;
    private V3PeopleContext fieldAgent;
    private Boolean isAllTasksClosed;
    private boolean mismatch;
    private ServiceAppointmentTicketStatusContext status;
    private Long responseDueDuration;
    private Long resolutionDueDuration;
    private V3ClientContext client;
    private V3SpaceContext space;
    private V3AssetContext asset;
    private VendorContext vendor;
    private V3TenantContext tenant;
    private Long slaPolicy;
    private Double estimatedCost;
    private Double actualCost;
    private PriorityContext priority;
    private AppointmentType appointmentType;
    public int getAppointmentType() {
        if (appointmentType != null) {
            return appointmentType.getIndex();
        }
        return -1;
    }
    public void setAppointmentType(int appointmentType) {
        this.appointmentType = AppointmentType.valueOf(appointmentType);
    }
    public AppointmentType getAppointmentTypeEnum() {
        return appointmentType;
    }
    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public static enum AppointmentType implements FacilioIntEnum {
        SERVICE_WORK_ORDER ("Service Workorder"),
        INSPECTION ("Inspection"),
        WORK_ORDER("Workorder");

        String name;

        AppointmentType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        public static AppointmentType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
    private InspectionResponseContext inspection;
    private V3WorkOrderContext workorder;

    private DueStatus responseDueStatus;
    private DueStatus resolutionDueStatus;
    public Integer getResponseDueStatus() {
        if (responseDueStatus != null) {
            return responseDueStatus.getIndex();
        }
        return null;
    }
    public void setResponseDueStatus(Integer responseDueStatus) {
        if(responseDueStatus != null) {
            this.responseDueStatus = DueStatus.valueOf(responseDueStatus);
        }
    }
    public DueStatus getResponseDueStatusEnum() {
        return responseDueStatus;
    }

    public Integer getResolutionDueStatus() {
        if (resolutionDueStatus != null) {
            return resolutionDueStatus.getIndex();
        }
        return null;
    }
    public void setResolutionDueStatus(Integer resolutionDueStatus) {
        if(resolutionDueStatus != null) {
            this.resolutionDueStatus = DueStatus.valueOf(resolutionDueStatus);
        }
    }
    public DueStatus getResolutionDueStatusEnum() {
        return resolutionDueStatus;
    }

    public static enum DueStatus implements FacilioIntEnum {
        DUE ("Due"),
        ON_TIME ("On Time"),
        OVERDUE ("Overdue");

        String name;

        DueStatus(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        public static DueStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private ServiceAppointmentCategory category;
    public int getCategory() {
        if (category != null) {
            return category.getIndex();
        }
        return -1;
    }
    public void setCategory(int category) {
        this.category = ServiceAppointmentCategory.valueOf(category);
    }
    public ServiceAppointmentCategory getCategoryEnum() {
        return category;
    }
    public void setCategory(ServiceAppointmentCategory category) {
        this.category = category;
    }

    public static enum ServiceAppointmentCategory implements FacilioIntEnum {
        INSTALLATION_AND_SETUP("Installation and Setup"),
        MAINTAIN_AND_REPAIRS ("Maintenance and Repairs"),
        FIXTURE_CHANGES_AND_RESETS("Fixture Changes and Resets"),
        EQUIPMENT_UPGRADES_AND_REPLACEMENTS("Equipment Upgrades and Replacements"),
        CLEANING_AND_SANITATION("Cleaning and Sanitation"),
        SECURITY_AND_SAFETY("Security and Safety"),
        INVENTORY_MANAGEMENT("Inventory Management"),
        IT_AND_TECHNICAL_SUPPORT("IT and Technical Support");

        String name;

        ServiceAppointmentCategory(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        public static ServiceAppointmentCategory valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
