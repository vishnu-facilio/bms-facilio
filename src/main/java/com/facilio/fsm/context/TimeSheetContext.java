package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class TimeSheetContext extends V3Context {
    private String code;
    private Long startTime;
    private Long endTime;
    private Long duration;
    private V3PeopleContext fieldAgent;
    private ServiceAppointmentContext serviceAppointment;
    private List<TimeSheetTaskContext> serviceTasks;
    private ServiceOrderContext serviceOrder;
    private TimeSheetStatusContext status;

    public Type getTypeEnum() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getType(){
        if(type != null){
            return type.getIndex();
        }
        return -1;
    }
    public void setType(int type){
        this.type = Type.valueOf(type);
    }
    private Type type;



    public static enum Type implements FacilioIntEnum {
        APPOINTMENT ("Appointment"),
        MANUAL ("Manual");

        String name;

        Type(String name) {
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

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
