package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class BookingSlotsContext extends V3Context {

    private V3FacilityBookingContext booking;
    private SlotContext slot;


    public V3FacilityBookingContext getBooking() {
        return booking;
    }

    public void setBooking(V3FacilityBookingContext booking) {
        this.booking = booking;
    }

    public SlotContext getSlot() {
        return slot;
    }

    public void setSlot(SlotContext slot) {
        this.slot = slot;
    }

}
