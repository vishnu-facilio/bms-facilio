package com.facilio.fsm.commands.trip;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.TripContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class RollUpTripFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> recordMap = (Map<String, Object>) context.get(Constants.RECORD_MAP);
        List<TripContext> trips = (List<TripContext>) recordMap.get(context.get("moduleName"));
        if (CollectionUtils.isNotEmpty(trips)) {
            for (TripContext trip: trips) {
                if(trip.getServiceAppointment() != null) {
                    ServiceAppointmentContext appointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, trip.getServiceAppointment().getId(), ServiceAppointmentContext.class);
                    trip.setServiceOrder(appointment.getServiceOrder());
                    Map < String, Object > bodyParams = Constants.getBodyParams(context);
                    trip.setType(TripContext.Type.MANUAL);
                    if (!MapUtils.isEmpty(bodyParams) && bodyParams.containsKey("system")) {
                        if ((boolean) bodyParams.get("system")) {
                            trip.setType(TripContext.Type.SYSTEM);
                        }
                    }
                } else{
                    throw new FSMException(FSMErrorCode.TRIP_NOT_ENOUGH_DETAILS);
                }
            }
        }
        return false;
    }
}
