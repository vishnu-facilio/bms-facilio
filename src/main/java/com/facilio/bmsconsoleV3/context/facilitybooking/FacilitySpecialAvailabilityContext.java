package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.text.DecimalFormat;
import java.time.LocalTime;

public class FacilitySpecialAvailabilityContext extends V3Context {

    private FacilityContext facility;
    private Long startDate;
    private Long endDate;
    private Double cost;

    private SpecialType specialType;
    public Integer getSpecialType() {
        if (specialType != null) {
            return specialType.getIndex();
        }
        return null;
    }
    public void setSpecialType(Integer specialType) {
        if(specialType != null) {
            this.specialType = SpecialType.valueOf(specialType);
        }
    }
    public String getSpecialTypeEnum() {
        if (specialType != null) {

            return specialType.getValue();
        }
        return null;
    }

    public static enum SpecialType implements FacilioIntEnum {
//        SPECIAL_COST("Special Cost"), removing this as we are not supporting now
        SPECIAL_AVAILABILITY("Special Availability"),
        SPECIAL_UNAVAILABILITY("Special Unavailability");

        private String name;

        SpecialType(String name) {
            this.name = name;
        }

        public static SpecialType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public FacilityContext getFacility() {
        return facility;
    }

    public void setFacility(FacilityContext facility) {
        this.facility = facility;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if(cost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.cost = Double.valueOf(df.format(cost));
        }
    }

    public java.lang.Long getStartDate() {
        return startDate;
    }

    public void setStartDate(java.lang.Long startDate) {
        this.startDate = startDate;
    }

    public java.lang.Long getEndDate() {
        return endDate;
    }

    public void setEndDate(java.lang.Long endDate) {
        this.endDate = endDate;
    }

    private LocalTime startTime;

    private LocalTime actualStartTime;
    public LocalTime getActualStartTimeAsLocalTime() {
        return actualStartTime;
    }
    public LocalTime getActualStartTimeOrDefault() {
        if (actualStartTime != null) {
            return actualStartTime;
        }
        return LocalTime.MIN;
    }
    public int getActualStartTime() {
        if (actualStartTime != null) {
            return actualStartTime.toSecondOfDay();
        }
        return -1;
    }
    public void setActualStartTime(LocalTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    public void setActualStartTime(int value) {
        actualStartTime = LocalTime.ofSecondOfDay(value);
    }
    public String getStartTime() {
        if (actualStartTime != null) {
            return actualStartTime.toString();
        }
        return null;
    }
    public void setStartTime(String value) {
        if (value != null && value.trim().length() > 0) {
            this.actualStartTime = LocalTime.parse(value);
        }
    }

    private LocalTime actualEndTime;
    public LocalTime getActualEndTimeAsLocalTime() {
        return actualEndTime;
    }
    public LocalTime getActualEndTimeOrDefault() {
        if (actualEndTime != null) {
            return actualEndTime;
        }
        return LocalTime.MIN;
    }
    public int getActualEndTime() {
        if (actualEndTime != null) {
            return actualEndTime.toSecondOfDay();
        }
        return -1;
    }
    public void setActualEndTime(LocalTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
    public void setActualEndTime(int value) {
        actualEndTime = LocalTime.ofSecondOfDay(value);
    }
    public String getEndTime() {
        if (actualEndTime != null) {
            return actualEndTime.toString();
        }
        return null;
    }
    public void setEndTime(String value) {
        if (value != null && value.trim().length() > 0) {
            this.actualEndTime = LocalTime.parse(value);
        }
    }


    public String getSlotCostString() {
        if(cost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.cost);
        }
        return null;
    }

    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getCancelOnCostChange() {
        return cancelOnCostChange;
    }

    public void setCancelOnCostChange(Boolean cancelOnCostChange) {
        this.cancelOnCostChange = cancelOnCostChange;
    }

    private Boolean cancelOnCostChange=false;

    public Long getBookingCanceledCount() {
        return bookingCanceledCount;
    }

    public void setBookingCanceledCount(Long bookingCanceledCount) {
        this.bookingCanceledCount = bookingCanceledCount;
    }

    private Long bookingCanceledCount;
}
