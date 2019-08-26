package com.facilio.bmsconsole.context.reservation;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ExternalAttendeeContext extends ModuleBaseWithCustomFields {
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
