package com.facilio.bmsconsoleV3.context.calendar;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.events.context.EventContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V3CalendarContext extends V3Context {
    private String name;
    private String description;
    private CalendarTypeEnum calendarType;
    private V3ClientContext client;
    private List<V3CalendarEventMappingContext> calendarEventMappingContextList;
    public Integer getCalendarType() {
        if (calendarType == null) {
            return null;
        }
        return calendarType.getIndex();
    }

    public void setCalendarType(Integer calendarType) {
        if (calendarType != null) {
            this.calendarType = CalendarTypeEnum.valueOf(calendarType);
        } else {
            this.calendarType = null;
        }
    }
    public void setCalendarTypeEnum(CalendarTypeEnum calendarType) {
        this.calendarType = calendarType;
    }

    public CalendarTypeEnum getCalendarTypeEnum() {
        return calendarType;
    }
    @AllArgsConstructor
    public static enum CalendarTypeEnum implements FacilioIntEnum {
        OPERATIONAL("Operational"),
        CONTROL_ACTION("Control Action")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final CalendarTypeEnum[] CREATION_TYPES = CalendarTypeEnum.values();
        public static CalendarTypeEnum valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
}
