package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class ServiceAppointmentContext extends V3Context {
    private String name;
    private String description;
    private ServiceOrderContext serviceOrder;
    private ServiceOrderContext.ServiceOrderPriority priority;
    private TerritoryContext territory;
    private List<ServiceAppointmentTaskContext> serviceTasks;
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
    public int getPriority() {
        if (priority != null) {
            return priority.getIndex();
        }
        return -1;
    }
    public void setPriority(int priority) {
        this.priority = ServiceOrderContext.ServiceOrderPriority.valueOf(priority);
    }
    public ServiceOrderContext.ServiceOrderPriority getServiceOrderPriorityEnum() {
        return priority;
    }
    public void setPriority(ServiceOrderContext.ServiceOrderPriority priorityType) {
        this.priority = priorityType;
    }
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
    public int getResponseDueStatus() {
        if (responseDueStatus != null) {
            return responseDueStatus.getIndex();
        }
        return -1;
    }
    public void setResponseDueStatus(int responseDueStatus) {
        this.responseDueStatus = DueStatus.valueOf(responseDueStatus);
    }
    public DueStatus getResponseDueStatusEnum() {
        return responseDueStatus;
    }
    public void setResponseDueStatus(DueStatus responseDueStatus) {
        this.responseDueStatus = responseDueStatus;
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
}
