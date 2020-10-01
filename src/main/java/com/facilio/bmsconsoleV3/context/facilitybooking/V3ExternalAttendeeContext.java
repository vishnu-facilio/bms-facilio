package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class V3ExternalAttendeeContext extends V3Context {

    private V3FacilityBookingContext facilityBooking;

    public V3FacilityBookingContext getFacilityBooking() {
        return facilityBooking;
    }

    public void setFacilityBooking(V3FacilityBookingContext facilityBooking) {
        this.facilityBooking = facilityBooking;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    private String phone;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
