package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
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
    private ServiceOrderContext serviceWorkOrder;
    private List<ServiceAppointmentTaskContext> serviceTasks;
    private Long scheduledStartTime;
    private Long scheduledEndTime;
    private Long estimatedDuration;
    private Long actualStartTime;
    private Long actualEndTime;
    private Long actualDuration;
    private LocationContextV3 location;
    private List<ServiceSkillsContext> skills;
    private List<V3ItemTypesContext> itemTypes;
    private List<V3ToolTypesContext> toolType;
    private List<V3ServiceContext> service;
    private TerritoryContext territory;
    private V3PeopleContext fieldAgent;
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
    private V3WorkOrderContext workOrder;
}
