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
    private TimeOffTypeContext type;
    private V3PeopleContext people;
    private String comments;
}
