package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceAppointmentSkillContext extends V3Context {
    private ServiceAppointmentContext left;
    private ServiceSkillsContext right;
}
