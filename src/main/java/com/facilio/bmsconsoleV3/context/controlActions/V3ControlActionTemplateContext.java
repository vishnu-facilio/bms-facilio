package com.facilio.bmsconsoleV3.context.controlActions;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V3ControlActionTemplateContext extends V3ControlActionContext{
    private String subject;
    private V3CalendarContext calendar;
}
