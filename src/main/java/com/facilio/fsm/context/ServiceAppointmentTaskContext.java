package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ServiceAppointmentTaskContext extends V3Context {
    private ServiceAppointmentContext left;
    private ServiceTaskContext right;
}
