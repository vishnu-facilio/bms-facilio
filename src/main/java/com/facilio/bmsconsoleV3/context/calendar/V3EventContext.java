package com.facilio.bmsconsoleV3.context.calendar;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.enums.EventTypeEnum;
import com.facilio.modules.FacilioIntEnum;
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
    private Integer scheduledMonth;
    private Integer scheduledDate;
    private Integer scheduledWeekNumber;
    private Integer scheduledDay;
    private EventTypeEnum eventType;
    private Boolean isSpecific;
    private Integer seasonStartMonth;
    private Integer seasonStartDate;
    private Integer seasonEndMonth;
    private Integer seasonEndDate;
    private Long specialCaseMilliSecond;
    private List<V3EventTimeSlotContext> timeSlotList;
    private List<String> timeSlotString;

    public List<String> getTimeSlotString() {
        return Arrays.asList("09:30 - 12:30","15:00 - 17:30");
    }

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
        return scheduledMonth;
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
}
