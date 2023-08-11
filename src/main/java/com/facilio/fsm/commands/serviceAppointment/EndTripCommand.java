package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class EndTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        TripContext trip = (TripContext) context.get(FacilioConstants.Trip.TRIP);
        ServiceAppointmentUtil.endTripForAppointment(recordId,trip);
        return false;
    }
}
