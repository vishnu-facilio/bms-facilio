package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.accounts.dto.User;
import com.facilio.v3.context.V3Context;

public class V3InternalAttendeeContext extends V3Context {
    private V3FacilityBookingContext facilityBooking;

    public V3FacilityBookingContext getFacilityBooking() {
        return facilityBooking;
    }

    public void setFacilityBooking(V3FacilityBookingContext facilityBooking) {
        this.facilityBooking = facilityBooking;
    }

    private User attendee;
    public User getAttendee() {
        return attendee;
    }
    public void setAttendee(User attendee) {
        this.attendee = attendee;
    }
}
