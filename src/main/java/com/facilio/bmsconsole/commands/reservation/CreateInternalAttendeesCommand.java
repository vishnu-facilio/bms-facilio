package com.facilio.bmsconsole.commands.reservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.User;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.reservation.InternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.constants.FacilioConstants;

public class CreateInternalAttendeesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReservationContext reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE);
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
        context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, false);
        if (CollectionUtils.isEmpty(reservation.getInternalAttendees())) {
            context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.EMPTY_LIST); //To avoid no record exception
            context.remove(FacilioConstants.ContextNames.RECORD);
        }
        else {
            List<InternalAttendeeContext> attendees = new ArrayList<>();
            for (User attendee : reservation.getInternalAttendees()) {
                InternalAttendeeContext internalAttendee = new InternalAttendeeContext();
                internalAttendee.setReservation(reservation);
                internalAttendee.setAttendee(attendee);
                attendees.add(internalAttendee);
            }
            context.put(FacilioConstants.ContextNames.RECORD_LIST, attendees);
        }
        return false;
    }
}
