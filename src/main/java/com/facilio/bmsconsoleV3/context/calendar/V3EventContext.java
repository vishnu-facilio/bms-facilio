package com.facilio.bmsconsoleV3.context.calendar;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.enums.EventTypeEnum;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.commands.SaveCommand;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Getter
@Setter
public class V3EventContext extends V3Context {
    private String name;
    private String description;
    private Long validityStartTime;
    private Long validityEndTime;
    private Integer eventSequence;
    private Integer scheduledYear;
    private MonthValueEnum scheduledMonth;
    private Integer scheduledDate;
    private Integer scheduledWeekNumber;
    private WeekDayEnum scheduledDay;
    private EventTypeEnum eventType;
    private Boolean isSpecific;
    private MonthValueEnum seasonStartMonth;
    private Integer seasonStartDate;
    private MonthValueEnum seasonEndMonth;
    private Integer seasonEndDate;
    private Long specialCaseMilliSecond;
    private List<V3EventTimeSlotContext> timeSlotList;
    private List<String> timeSlotString;


    public Integer getEventType() {
        return eventType == null ? null : eventType.getIntVal();
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType == null ? null : EventTypeEnum.valueOf(eventType);
    }


    public EventTypeEnum getEventTypeEnum() {
        return eventType;
    }
    public void setEventTypeEnum(String eventType){
        this.eventType = EventTypeEnum.valueOf(eventType);
    }

    private EventFrequencyEnum eventFrequency;
    public Integer getEventFrequency() {
        if (eventFrequency == null) {
            return null;
        }
        return eventFrequency.getIndex();
    }

    public void setEventFrequency(Integer eventFrequency) {
        if (eventFrequency != null) {
            this.eventFrequency = EventFrequencyEnum.valueOf(eventFrequency);
        } else {
            this.eventFrequency = null;
        }
    }

    public Integer getScheduledDate() {
        if(getSpecialCaseMilliSecond() != null){
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(getSpecialCaseMilliSecond()),ZoneId.systemDefault());
            return dateTime.getDayOfMonth();
        }
        return scheduledDate;
    }
    public Integer getScheduledMonth(){
        if(getSpecialCaseMilliSecond() != null){
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(getSpecialCaseMilliSecond()),ZoneId.systemDefault());
            return dateTime.getMonthValue();
        }
        if(scheduledMonth == null) {
           return null;
        }
        return scheduledMonth.getIndex();
    }
    public Integer getScheduledYear(){
        if(getSpecialCaseMilliSecond() != null){
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(getSpecialCaseMilliSecond()),ZoneId.systemDefault());
            return dateTime.getYear();
        }
        return scheduledYear;
    }
    public void setEventFrequencyEnum(EventFrequencyEnum eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    public EventFrequencyEnum getEventFrequencyEnum() {
        return eventFrequency;
    }
    @AllArgsConstructor
    public static enum EventFrequencyEnum implements FacilioIntEnum {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final EventFrequencyEnum[] CREATION_TYPES = EventFrequencyEnum.values();
        public static EventFrequencyEnum valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
    public Integer getScheduledDay(){
        if(scheduledDay == null){
            return null;
        }
        return scheduledDay.getIndex();
    }
    public void setScheduledDay(Integer scheduledDay) {
        if (scheduledDay != null) {
            this.scheduledDay = WeekDayEnum.valueOf(scheduledDay);
        } else {
            this.scheduledDay = null;
        }
    }
    public void setScheduledDayEnum(WeekDayEnum scheduledDay) {
        this.scheduledDay = scheduledDay;
    }

    public WeekDayEnum getScheduledDayEnum() {
        return scheduledDay;
    }
    @AllArgsConstructor
    public static enum WeekDayEnum implements FacilioIntEnum {
        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday"),
        SATURDAY("Saturday"),
        SUNDAY("Sunday")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final WeekDayEnum[] CREATION_TYPES = WeekDayEnum.values();
        public static WeekDayEnum valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
    public void setScheduledMonth(Integer scheduledMonth) {
        if (scheduledMonth != null) {
            this.scheduledMonth = MonthValueEnum.valueOf(scheduledMonth);
        } else {
            this.scheduledMonth = null;
        }
    }
    public void setScheduledMonthEnum(MonthValueEnum scheduledMonth) {
        this.scheduledMonth = scheduledMonth;
    }

    public MonthValueEnum getScheduledMonthEnum() {
        return scheduledMonth;
    }
    public void setSeasonStartMonth(Integer seasonStartMonth) {
        if (seasonStartMonth != null) {
            this.seasonStartMonth = MonthValueEnum.valueOf(seasonStartMonth);
        } else {
            this.seasonStartMonth = null;
        }
    }
    public Integer getSeasonStartMonth(){
        if(seasonStartMonth != null){
            return seasonStartMonth.getVal();
        }
        return -1;
    }
    public void setSeasonStartMonthEnum(MonthValueEnum seasonStartMonth) {
        this.seasonStartMonth = seasonStartMonth;
    }

    public MonthValueEnum getSeasonStartMonthEnum() {
        return seasonStartMonth;
    }
    public void setSeasonEndMonth(Integer seasonEndMonth) {
        if (seasonEndMonth != null) {
            this.seasonEndMonth = MonthValueEnum.valueOf(seasonEndMonth);
        } else {
            this.seasonEndMonth = null;
        }
    }
    public Integer getSeasonEndMonth(){
        if(seasonEndMonth != null){
            return seasonEndMonth.getVal();
        }
        return -1;
    }
    public void setSeasonEndMonthEnum(MonthValueEnum seasonEndMonth) {
        this.seasonEndMonth = seasonEndMonth;
    }

    public MonthValueEnum getSeasonEndMonthEnum() {
        return seasonEndMonth;
    }

    @AllArgsConstructor
    public static enum MonthValueEnum implements FacilioIntEnum {
        JANUARY("January"),
        FEBRUARY("February"),
        MARCH("March"),
        APRIL("April"),
        MAY("May"),
        JUNE("June"),
        JULY("July"),
        AUGUST("August"),
        SEPTEMBER("September"),
        OCTOBER("October"),
        NOVEMBER("November"),
        DECEMBER("December")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final MonthValueEnum[] CREATION_TYPES = MonthValueEnum.values();
        public static MonthValueEnum valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
}
