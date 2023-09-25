package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class RescheduleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ServiceAppointmentUtil.rescheduleServiceAppointment(context);

        return false;
    }
}
