package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class DispatchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ServiceAppointmentUtil.dispatchServiceAppointment(context);


        return false;
    }
}
