package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class BookingPaymentContext extends V3Context {

    private V3FacilityBookingContext booking;
    private Long paymentDate;
    private Double amount;
    private BookingPaymentContext.PaymentMode paymentMode;
    private BookingPaymentContext.PaymentStatus paymentStatus;


    public static enum PaymentMode implements FacilioEnum {
        CASH, CARD, OTHERS;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static PaymentMode valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static enum PaymentStatus implements FacilioEnum {
        DUE, PAID, CANCELLED, OTHERS;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static PaymentStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public V3FacilityBookingContext getBooking() {
        return booking;
    }

    public void setBooking(V3FacilityBookingContext booking) {
        this.booking = booking;
    }

    public Long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentMode(Integer mode) {
        if (mode != null) {
            this.paymentMode = BookingPaymentContext.PaymentMode.valueOf(mode);
        }
    }

    public BookingPaymentContext.PaymentMode getPaymentModeEnum() {
        return paymentMode;
    }
    public Integer getPaymentMode() {
        if (paymentMode != null) {
            return paymentMode.getIndex();
        }
        return null;
    }

    public void setPaymentStatus(Integer status) {
        if (status != null) {
            this.paymentStatus = BookingPaymentContext.PaymentStatus.valueOf(status);
        }
    }

    public BookingPaymentContext.PaymentStatus getPaymentStatusEnum() {
        return paymentStatus;
    }
    public Integer getPaymentStatus() {
        if (paymentStatus != null) {
            return paymentStatus.getIndex();
        }
        return null;
    }

}
