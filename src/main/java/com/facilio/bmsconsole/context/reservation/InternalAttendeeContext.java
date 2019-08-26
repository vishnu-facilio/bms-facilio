package com.facilio.bmsconsole.context.reservation;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InternalAttendeeContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ReservationContext reservation;
    public ReservationContext getReservation() {
        return reservation;
    }
    public void setReservation(ReservationContext reservation) {
        this.reservation = reservation;
    }

    private User attendee;
    public User getAttendee() {
        return attendee;
    }
    public void setAttendee(User attendee) {
        this.attendee = attendee;
    }
}
