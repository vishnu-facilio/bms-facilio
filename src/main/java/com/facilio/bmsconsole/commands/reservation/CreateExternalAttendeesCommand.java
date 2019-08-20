package com.facilio.bmsconsole.commands.reservation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.reservation.ExternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

public class CreateExternalAttendeesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReservationContext reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE);
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
        if (CollectionUtils.isEmpty(reservation.getExternalAttendees())) {
            context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.EMPTY_LIST); //To avoid no record exception
            context.remove(FacilioConstants.ContextNames.RECORD);
        }
        else {
            ReservationContext plainReservation = new ReservationContext();
            plainReservation.setId(reservation.getId());
            for (ExternalAttendeeContext attendee : reservation.getExternalAttendees()) {
                attendee.setReservation(plainReservation);
            }
            context.put(FacilioConstants.ContextNames.RECORD_LIST, reservation.getExternalAttendees());
        }
        return false;
    }
}
