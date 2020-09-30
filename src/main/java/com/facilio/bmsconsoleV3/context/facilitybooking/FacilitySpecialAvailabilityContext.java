package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class FacilitySpecialAvailabilityContext extends V3Context {

    private FacilityContext facility;
    private Long specialDate;
    private Long startTime;
    private Long endTime;
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
    public SpecialType getSpecialTypeEnum() {
        return specialType;
    }

    public static enum SpecialType implements FacilioEnum {
        SPECIAL_COST, SPACIAL_AVAILABILITY, SPCIAL_UNAVAILABILITY;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static SpecialType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public FacilityContext getFacility() {
        return facility;
    }

    public void setFacility(FacilityContext facility) {
        this.facility = facility;
    }

    public Long getSpecialDate() {
        return specialDate;
    }

    public void setSpecialDate(Long specialDate) {
        this.specialDate = specialDate;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
