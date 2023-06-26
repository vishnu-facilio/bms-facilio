package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TimeOffContext extends V3Context {
    private Long startTime;
    private Long endTime;
    private Integer type;
    public String getTypeColor(){
        Color typeColorEnum = Color.valueOf(this.type);
        if(typeColorEnum != null){ return  typeColorEnum.getValue();}
        return "#FDECBA";
    }
    private V3PeopleContext people;
    public enum Color implements FacilioIntEnum {
        STAND_BY("#FDE49D"),
        HOLIDAY("#F4C8E7"),
        SICK("#E5F4FF"),
        VACATION("#D7CCF0"),
        TRUCK_BREAKDOWN("#F6D1C8"),
        TRAINING("#FCE0C3");

        private final String value;
        Color(String value) {
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

        public static Color valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
