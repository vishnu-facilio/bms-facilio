package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.v3.context.V3Context;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class WeekDayAvailability extends V3Context {

    private Integer dayOfWeek;

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
    public DayOfWeek getDayOfWeekEnum() {
        if(dayOfWeek != null){
            return DayOfWeek.of(dayOfWeek);
        }
        return null;
    }

    private LocalTime startTime;

    public LocalTime getStartTimeAsLocalTime() {
        return startTime;
    }

    public LocalTime getStartTimeOrDefault() {
        if (startTime != null) {
            return startTime;
        }
        return LocalTime.MIN;
    }

    public String getStartTime() {
        if (startTime != null) {
            return startTime.toString();
        }
        return null;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(String value) {
        if(value!=null&&value.trim().length()>0){
            this.startTime = LocalTime.parse(value);
        }
    }

    private LocalTime endTime;

    public LocalTime getEndTimeAsLocalTime() {
        return endTime;
    }
    public LocalTime getEndTimeOrDefault() {
        if (endTime != null) {
            return endTime;
        }
        return LocalTime.MAX;
    }

    public String getEndTime() {
        if (endTime != null) {
            return endTime.toString();
        }
        return null;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setEndTime(String value) {
        if(value!=null&&value.trim().length()>0){
            this.endTime = LocalTime.parse(value);
        }
    }

    private FacilityContext facility;

    public FacilityContext getFacility() {
        return facility;
    }

    public void setFacility(FacilityContext facility) {
        this.facility = facility;
    }

    private Double cost;

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if(cost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.cost = Double.valueOf(df.format(cost));
        }
    }

    public String getSlotCostString() {
        if(cost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.cost);
        }
        return null;
    }

    private Long startDate;
    private Long endDate;

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
