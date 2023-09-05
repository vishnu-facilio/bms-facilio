package com.facilio.bmsconsoleV3.context.calendar;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3CalendarTimeSlotContext extends V3Context {
    private V3CalendarContext calendar;
    private V3EventContext event;
    private Integer startMin;
    private Integer endMin;
}
