package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TripContext extends V3Context {

    private V3PeopleContext people;
    private LocationContext startLocation;
    private LocationContext endLocation;
    private ServiceAppointmentContext serviceAppointment;
    private long startTime;
    private long endTime;

}
