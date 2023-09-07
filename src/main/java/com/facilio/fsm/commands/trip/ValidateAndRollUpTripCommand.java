package com.facilio.fsm.commands.trip;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateAndRollUpTripCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        HashMap<String,Object> oldRecordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        List<TripContext> trips = (List<TripContext>) recordMap.get(context.get("moduleName"));
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if(CollectionUtils.isNotEmpty(trips)) {
            for (TripContext trip : trips) {
                if (trip.getStartTime()>0 && trip.getEndTime() >0) {
                    if (trip.getStartTime() > trip.getEndTime()) {
                        throw new FSMException(FSMErrorCode.TRIP_TIME_MISMATCH);
                    }
                    trip.setStatus(ServiceAppointmentUtil.getTripStatus(FacilioConstants.Trip.COMPLETED));
                    Long duration = trip.getEndTime() - trip.getStartTime();
                    trip.setTripDuration(duration/1000);
                }
                if(eventType == EventType.EDIT){
                    Map<Long,Object> oldTrips = (Map<Long,Object>) oldRecordMap.get(context.get("moduleName"));
                    TripContext oldTrip = (TripContext) oldTrips.get(trip.getId());
                    if(trip.getPeople().getId()!=oldTrip.getPeople().getId()) {
                        throw new FSMException(FSMErrorCode.UPDATE_PREVENT);
                    }
                    else if(trip.getServiceAppointment().getId()!=oldTrip.getServiceAppointment().getId()) {
                        throw new FSMException(FSMErrorCode.UPDATE_PREVENT);
                    }
                }

            }
        }
        return false;
    }
}
