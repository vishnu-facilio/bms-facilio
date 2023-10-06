package com.facilio.bmsconsoleV3.enums;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.db.util.DBConf;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioIntEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventTypeEnum implements FacilioEnum {
    SPECIAL_TYPE(10, "Special Type", "backgroundNeutralGrey01Subtle", "backgroundNeutralGrey01Medium") {
        @Override
        public boolean isMatch(V3EventContext eventContext, LocalDate localDate) throws Exception {
            boolean check = checkForValidity(localDate,eventContext);
            check = checkForYear(localDate,eventContext);
            if(check){
                check = checkForMonth(localDate,eventContext);
                if(check){
                    check = checkForDate(localDate,eventContext);
                }
            }
            return check;
        }
    },
    SPECIAL_MAINTENANCE(20, "Special Maintenance", "backgroundNeutralGrey01Subtle", "backgroundNeutralGrey01Medium") {
        @Override
        public boolean isMatch(V3EventContext eventContext, LocalDate localDate) throws Exception {
            boolean check = checkForValidity(localDate, eventContext);
            if (check) {
                return checkForYearlyFrequency(localDate, eventContext);
            }
            return check;

        }
    },

        HOLIDAY(30,"Holiday","backgroundAccentCyanLight","backgroundAccentCyanMedium") {
            @Override
            public boolean isMatch (V3EventContext eventContext, LocalDate localDate) throws Exception {
                boolean check = checkForValidity(localDate,eventContext);
                if(check) {
                    return checkForYearlyFrequency(localDate, eventContext);
                }
                return check;
            }

        },

        REGULAR_MAINTENANCE(40,"Regular Maintenance","backgroundAccentVioletLight","backgroundAccentVioletMedium") {
            @Override
            public boolean isMatch (V3EventContext eventContext, LocalDate localDate) throws Exception {
                boolean ckeck = checkForValidity(localDate,eventContext);
                if(ckeck) {
                    return checkForMonthFrequency(localDate, eventContext);
                }
                return ckeck;
            }

        },

        MONTHLY(50,"Monthly","backgroundAccentPinkLight","backgroundAccentPinkMedium") {
            @Override
            public boolean isMatch (V3EventContext eventContext, LocalDate localDate) throws Exception {
                boolean check = checkForValidity(localDate,eventContext);
                if(check) {
                    return checkForMonthFrequency(localDate, eventContext);
                }
                return check;
            }

        },

        SEASON(60,"Season","backgroundAccentYellowLight","backgroundAccentYellowMedium") {
            @Override
            public boolean isMatch (V3EventContext eventContext, LocalDate localDate) throws Exception {
                boolean check = checkForValidity(localDate,eventContext);
                check = checkForSeasonRange(localDate, eventContext);
                if (check) {
                    return checkForWeeklyFrequency(localDate, eventContext);
                }
                return check;
            }

        },

        DAY_OF_THE_WEEK(70,"Day of the Week","backgroundAccentBlueLight","backgroundAccentBlueMedium") {
            @Override
            public boolean isMatch (V3EventContext eventContext, LocalDate localDate) throws Exception {
                boolean check = checkForValidity(localDate,eventContext);
                if(check) {
                    return checkForWeeklyFrequency(localDate, eventContext);
                }
                return check;
            }
        }

        ;
        private int intVal;
        private String strVal;
        private String bgColor;
        private String borderColor;

        private EventTypeEnum(int intVal, String strVal, String bgColor, String borderColor) {
            this.intVal = intVal;
            this.strVal = strVal;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
        }

        public int getIntVal() {
            return intVal;
        }

        public String getStringVal() {
            return strVal;
        }

        public String getBgColor() {
            return bgColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        private static final Map<Integer, EventTypeEnum> EVENT_TYPE_ENUM_MAP = Collections.unmodifiableMap(initEventTypeEnumMap());

        public static EventTypeEnum valueOf(Integer eventType) {
            return EVENT_TYPE_ENUM_MAP.get(eventType);
        }

        @Override
        public String getValue() {
            return this.strVal;
        }

        @Override
        public Object getIndex() {
            return this.intVal;
        }
        private static Map<Integer, EventTypeEnum> initEventTypeEnumMap() {
            Map<Integer, EventTypeEnum> eventTypeEnumMap = new HashMap<>();

            for (EventTypeEnum eventTypeEnum : values()) {
                eventTypeEnumMap.put(eventTypeEnum.getIntVal(), eventTypeEnum);
            }
            return eventTypeEnumMap;
        }

        abstract public boolean isMatch(V3EventContext eventContext, LocalDate localDate) throws Exception;

        public boolean checkForWeeklyFrequency(LocalDate localDate, V3EventContext calendarEventContext) {
            return checkForDay(localDate, calendarEventContext);
        }

        public boolean checkForMonthFrequency(LocalDate localDate, V3EventContext calendarEventContext) {
            boolean include = false;
            if (calendarEventContext.getIsSpecific()) {
                include = checkForDate(localDate, calendarEventContext);
            } else {
                include = checkForWeek(localDate, calendarEventContext);
                if (include) {
                    include = checkForDay(localDate, calendarEventContext);
                }
            }
            return include;
        }

        public boolean checkForEventValidity(Long startTime, Long endTime, LocalDate localDate) {
            if (startTime == null && endTime == null) {
                return true;
            }
            ZonedDateTime StartTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), DBConf.getInstance().getCurrentZoneId());
            ZonedDateTime EndTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), DBConf.getInstance().getCurrentZoneId());
            LocalDate startDate = LocalDate.of(StartTime.getYear(), StartTime.getMonthValue(), StartTime.getDayOfMonth());
            LocalDate endDate = LocalDate.of(EndTime.getYear(), EndTime.getMonthValue(), EndTime.getDayOfMonth());
            if (localDate.isAfter(startDate) && localDate.isBefore(endDate)) {
                return true;
            }
            return false;
        }

        public boolean checkForYearlyFrequency(LocalDate localDate, V3EventContext calendarEventContext) {
            boolean include = false;
            include = checkForMonth(localDate, calendarEventContext);
            if (calendarEventContext.getIsSpecific()) {
                if (include) {
                    include = checkForDate(localDate, calendarEventContext);
                }
            } else {
                if (include) {
                    include = checkForWeek(localDate, calendarEventContext);
                    if (include) {
                        include = checkForDay(localDate, calendarEventContext);
                    }
                }
            }
            return include;
        }

        public boolean checkForDay(LocalDate localDate, V3EventContext calendarEventContext) {
            if (calendarEventContext.getScheduledDay() != null) {
                if (calendarEventContext.getScheduledDay() == localDate.getDayOfWeek().getValue()) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkForWeek(LocalDate localDate, V3EventContext calendarEventContext) {
            if (calendarEventContext.getScheduledWeekNumber() != null) {
                if (calendarEventContext.getScheduledWeekNumber() == localDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkForDate(LocalDate localDate, V3EventContext calendarEventContext) {
            if (calendarEventContext.getScheduledDate() != null) {
                if (calendarEventContext.getScheduledDate() == localDate.getDayOfMonth()) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkForMonth(LocalDate localDate, V3EventContext calendarEventContext) {
            if (calendarEventContext.getScheduledMonth() != null) {
                if (calendarEventContext.getScheduledMonth() == localDate.getMonthValue()) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkForYear(LocalDate localDate, V3EventContext calendarEventContext) {
            if (calendarEventContext.getScheduledYear() != null) {
                if (calendarEventContext.getScheduledYear() == localDate.getYear()) {
                    return true;
                }
            }
            return false;
        }
        public boolean checkForStartMonth(LocalDate localDate,Integer startDate){
            if(localDate.getDayOfMonth() >= startDate){
                return true;
            }
            return false;
        }
        public boolean checkForEndMonth(LocalDate localDate, Integer endDate){
            if(localDate.getDayOfMonth() <= endDate){
                return true;
            }
            return false;
        }

        public boolean checkForSeasonRange(LocalDate localDate, V3EventContext calendarEventContext) {
            boolean check = false;
            Integer startMonth = calendarEventContext.getSeasonStartMonth();
            Integer startDate = calendarEventContext.getSeasonStartDate();
            Integer endMonth = calendarEventContext.getSeasonEndMonth();
            Integer endDate = calendarEventContext.getSeasonEndDate();

            if(startMonth < endMonth){
                if(localDate.getMonthValue() == startMonth){
                    if(localDate.getDayOfMonth() >= startDate && localDate.getDayOfMonth() <= 31){
                        check = true;
                    }
                }
                else if(localDate.getMonthValue() == endDate){
                    if(localDate.getDayOfMonth() >= 1 && localDate.getDayOfMonth() <= endDate){
                        check = true;
                    }
                }
               else if(localDate.getMonthValue() > startMonth && localDate.getMonthValue() < endMonth){
                    if(localDate.getDayOfMonth() >= 1 && localDate.getDayOfMonth() <= 31){
                        check = true;
                    }
                }
            }
            else if(startMonth == endMonth){
                if(localDate.getMonthValue() == startMonth && (localDate.getDayOfMonth() > startDate || localDate.getDayOfMonth() < endDate)){
                    check = true;
                }
                if(localDate.getMonthValue() != startMonth && startDate > endDate){
                    check = true;
                }
            }
            else if(startMonth > endMonth){
                if(localDate.getMonthValue() == startMonth){
                    if(localDate.getDayOfMonth() >= startDate && localDate.getDayOfMonth() <= 31){
                        check = true;
                    }
                }
                else if(localDate.getMonthValue() == endDate){
                    if(localDate.getDayOfMonth() >= 1 && localDate.getDayOfMonth() <= endDate){
                        check = true;
                    }
                }
               else if(((localDate.getMonthValue() > startMonth && localDate.getMonthValue() < 12) ||
                        (localDate.getMonthValue() > 1 && localDate.getMonthValue() < endDate)) &&
                        (localDate.getDayOfMonth() >= 1 && localDate.getDayOfMonth() <= 31)){
                    check = true;
                }
            }
            return check;
        }

        public boolean checkForValidity(LocalDate localDate, V3EventContext calendarEventContext) {
            boolean check = false;
            if(calendarEventContext.getValidityStartTime() == null && calendarEventContext.getValidityEndTime() == null){
                return true;
            }
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth(),0,0,0,0,DBConf.getInstance().getCurrentZoneId());
            if (calendarEventContext.getValidityStartTime() != null) {
                ZonedDateTime startDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendarEventContext.getValidityStartTime()), DBConf.getInstance().getCurrentZoneId());
                if (zonedDateTime.isAfter(startDate) || zonedDateTime.isEqual(startDate)) {
                    check = true;
                } else {
                    check = false;
                }

            }
            if (check) {
                if (calendarEventContext.getValidityEndTime() != null) {
                    ZonedDateTime endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendarEventContext.getValidityEndTime()), DBConf.getInstance().getCurrentZoneId());
                    if (zonedDateTime.isBefore(endDate) || zonedDateTime.isEqual(endDate)) {
                        check = true;
                    } else {
                        check = false;
                    }

                }
            }
            return check;

        }
    }
