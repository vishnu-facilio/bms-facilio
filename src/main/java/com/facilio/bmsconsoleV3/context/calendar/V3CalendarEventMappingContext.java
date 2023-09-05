package com.facilio.bmsconsoleV3.context.calendar;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V3CalendarEventMappingContext extends V3Context {
    private V3CalendarContext calendar;
    private V3EventContext event;
    private Boolean isEventEdited;
    private List<V3CalendarTimeSlotContext> calendarTimeSlotContextList;
}
