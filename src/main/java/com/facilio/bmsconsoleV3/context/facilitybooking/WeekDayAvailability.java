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
