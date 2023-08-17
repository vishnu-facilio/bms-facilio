package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class EndTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        LocationContext endLocation = (LocationContext) context.get(FacilioConstants.Trip.END_LOCATION);
        ServiceAppointmentUtil.endTripForAppointment(recordId,endLocation);
        return false;
    }
}
