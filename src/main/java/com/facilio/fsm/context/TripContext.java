package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class TripContext extends V3Context {

    private String code;
    private V3PeopleContext people;
    private LocationContext startLocation;
    private LocationContext endLocation;
    private ServiceOrderContext serviceOrder;
    private ServiceAppointmentContext serviceAppointment;
    private long startTime;
    private long endTime;
    private long tripDuration;
    private Double tripDistance;
    private long estimatedDuration;
    private Double estimatedDistance;
    private File journey;
    private long journeyId;
    private String journeyUrl;
    private String journeyFileName;
    private  String journeyContentType;
    private TripStatusContext status;

}
