package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class StartTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        LocationContext startLocation = (LocationContext) context.get(FacilioConstants.Trip.START_LOCATION);
        TripContext trip = ServiceAppointmentUtil.startTripForAppointment(recordId,startLocation);
        context.put(FacilioConstants.ContextNames.DATA,trip);
        return false;
    }
}
