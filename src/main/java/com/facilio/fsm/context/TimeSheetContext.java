package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class TimeSheetContext extends V3Context {
    private Long actualStartTime;
    private Long actualEndTime;
    private Long actualDuration;
    private LocationContextV3 actualStartLocation;
    private LocationContextV3 actualEndLocation;
    private V3PeopleContext fieldAgent;
    private ServiceAppointmentContext serviceAppointment;
    private List<TimeSheetTaskContext> serviceTasks;
}
