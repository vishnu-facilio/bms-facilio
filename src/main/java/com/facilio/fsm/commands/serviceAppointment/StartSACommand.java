package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;


public class StartSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ServiceAppointmentUtil.startServiceAppointment(recordId);
        return false;
    }
}
