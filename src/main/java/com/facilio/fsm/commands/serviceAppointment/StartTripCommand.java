package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class StartTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        LocationContext startLocation = (LocationContext) context.get(FacilioConstants.Trip.START_LOCATION);
        TripContext trip = ServiceAppointmentUtil.startTripForAppointment(recordId,startLocation);
        context.put(FacilioConstants.ContextNames.DATA,trip);
        JSONObject info = new JSONObject();
        info.put("trip",trip.getCode());
        CommonCommandUtil.addActivityToContext(recordId, -1, ServiceAppointmentActivityType.START_TRIP, info, (FacilioContext) context);
        return false;
    }
}
