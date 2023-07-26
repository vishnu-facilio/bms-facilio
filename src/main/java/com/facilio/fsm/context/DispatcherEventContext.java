package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DispatcherEventContext {
    private Long startTime;
    private Long endTime;
    private TimeOffContext timeOff;
    private ServiceAppointmentContext serviceAppointmentContext;
    private boolean allowResize = false;
    private boolean allowReschedule = false;
    private String backgroundColor = "#058545";
    private EventType eventType;
    public Integer getEventType() {
        if (eventType != null) {
            return eventType.getIndex();
        }
        return null;
    }
    public void setEventType(Integer eventType) {
        if (eventType != null) {
            this.eventType = EventType.valueOf(eventType);
        }
    }
    public EventType getEventTypeEnum() {
        return eventType;
    }
    public enum EventType implements FacilioIntEnum {
        TIME_OFF("Time Off"),
        SERVICE_APPOINTMENT("Service Appointment");
        private final String value;
        EventType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static EventType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
