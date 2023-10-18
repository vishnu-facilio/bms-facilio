package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.SilentPushNotificationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.util.SilentNotificationUtilForFsm;
import com.facilio.fsm.util.TripUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class EndTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        LocationContext endLocation = (LocationContext) context.get(FacilioConstants.Trip.END_LOCATION);
        List<TripContext> trips = TripUtil.endTrip(recordId,endLocation);
        if(CollectionUtils.isNotEmpty(trips)){
            for(TripContext trip : trips){
                JSONObject info = new JSONObject();
                info.put("doneBy", AccountUtil.getCurrentUser().getName());
                info.put("trip",trip.getCode());
                CommonCommandUtil.addActivityToContext(trip.getServiceAppointment().getId(), -1, ServiceAppointmentActivityType.END_TRIP, info, (FacilioContext) context);
            }
            List<V3PeopleContext> peopleList = trips.stream().map(TripContext::getPeople).collect(Collectors.toList());
            List<Long> recordIds = peopleList.stream().map(V3PeopleContext::getId).collect(Collectors.toList());
            SilentNotificationUtilForFsm.sendNotificationForFsm( recordIds, SilentPushNotificationContext.ActionType.END_TRIP,300000L,120000L);


        }
        context.put(FacilioConstants.ContextNames.DATA,trips);
        return false;
    }
}
