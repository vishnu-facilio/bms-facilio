package com.facilio.fsm.commands.trip;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CheckForExistingTripsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TripContext> trips = (List<TripContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(trips)){
            for(TripContext trip : trips){
                if(trip.getPeople() != null && trip.getStartTime() > 0){
                    List<TripContext> records = ServiceAppointmentUtil.getTripsForTimeRange(trip.getPeople().getId(), trip.getStartTime(), trip.getEndTime());
                    if(CollectionUtils.isNotEmpty(records)){
                        JSONObject errorData = new JSONObject();
                        errorData.put(FacilioConstants.Trip.TRIP,records);
                        throw new FSMException(FSMErrorCode.SA_TRIP_ALREADY_RUNNING).setRelatedData(errorData);
                    }
                } else {
                    throw new FSMException(FSMErrorCode.TRIP_NOT_ENOUGH_DETAILS);
                }
            }
        }
        return false;
    }
}
