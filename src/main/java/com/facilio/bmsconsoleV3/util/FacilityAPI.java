package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.awt.print.Book;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;

public class FacilityAPI {

    public static void setFacilityWeekDayAvailability(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityWeekDayModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilityWeekDayModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WeekDayAvailability> builder = new SelectRecordsBuilder<WeekDayAvailability>()
                .moduleName(facilityWeekDayModName)
                .select(fields)
                .beanClass(WeekDayAvailability.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("endDate"), String.valueOf(-1), CommonOperators.IS_EMPTY));

                ;
        List<WeekDayAvailability> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setWeekDayAvailabilities(list);
        }
    }

    public static List<WeekDayAvailability> getFacilitySpecialWeekDayAvailability(FacilityContext facility, Long startTime, Long endTime) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityWeekDayModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilityWeekDayModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WeekDayAvailability> builder = new SelectRecordsBuilder<WeekDayAvailability>()
                .moduleName(facilityWeekDayModName)
                .select(fields)
                .beanClass(WeekDayAvailability.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("startDate"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("startDate"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<WeekDayAvailability> list = builder.get();
        return list;
    }

    public static void setFacilitySpecialAvailability(FacilityContext facility, Long startTime, Long endTime) throws Exception {

        Long startDate = DateTimeUtil.getDayStartTimeOf(startTime);
        Long endDate = DateTimeUtil.getDayStartTimeOf(endTime);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilitySpecialAvailabilityModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilitySpecialAvailabilityModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilitySpecialAvailabilityContext> builder = new SelectRecordsBuilder<FacilitySpecialAvailabilityContext>()
                .moduleName(facilitySpecialAvailabilityModName)
                .select(fields)
                .beanClass(FacilitySpecialAvailabilityContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
       if(startTime != null && startTime > 0){
           builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("startDate"), String.valueOf(startDate), NumberOperators.GREATER_THAN_EQUAL));
         }
        if(endTime != null && endTime > 0){
            builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("endDate"), String.valueOf(endDate), NumberOperators.LESS_THAN_EQUAL));
        }
        List<FacilitySpecialAvailabilityContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setFacilitySpecialAvailabilities(list);
        }
    }

    public static Boolean checkForUnavailability(Long slotStartTime, Long slotEndTime, List<FacilitySpecialAvailabilityContext> unavailabilityList) throws Exception {

        if (CollectionUtils.isNotEmpty(unavailabilityList)) {
            for(FacilitySpecialAvailabilityContext unavailability : unavailabilityList){
                if(unavailability.getSpecialTypeEnum() != FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_UNAVAILABILITY) {
                    continue;
                }
                Long startTime = unavailability.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTime);

                while (startTime <= unavailability.getEndDate()) {

                    long startDateTimeOfDay = getCalendarTime(startTime, unavailability.getStartTimeAsLocalTime());
                    long endDateTimeOfDay = getCalendarTime(startTime, unavailability.getEndTimeAsLocalTime());

                    if(slotStartTime > startDateTimeOfDay && slotStartTime < endDateTimeOfDay){
                        return true;
                    }

                    cal.add(Calendar.DATE, 1);
                    startTime = cal.getTimeInMillis();
                }

            }
        }
        return false;
    }

    public static Boolean checkExistingSlots(List<SlotContext> existingSlotList, SlotContext newSlot) throws Exception {

        if (CollectionUtils.isNotEmpty(existingSlotList)) {
            for(SlotContext slot : existingSlotList){
                if((newSlot.getSlotStartTime() > slot.getSlotStartTime() && newSlot.getSlotStartTime() < slot.getSlotEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Long getCalendarTime(Long startTime, LocalTime localTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        int date = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        cal.set(year, month, date, localTime.getHour(), localTime.getMinute(), localTime.getSecond());
        long startDateTimeOfDay = cal.getTimeInMillis();
        return startDateTimeOfDay;

    }


    public static List<SlotContext> getFacilitySlots(FacilityContext facilityContext, Long startDateTime, Long endDateTime) throws Exception {

        FacilityAPI.setFacilitySpecialAvailability(facilityContext, startDateTime, endDateTime);
        FacilityAPI.setFacilityWeekDayAvailability(facilityContext);
        List<SlotContext> slotList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {
            for (FacilitySpecialAvailabilityContext splAvailability : facilityContext.getFacilitySpecialAvailabilities()) {
                Long startTime = splAvailability.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTime);

                while (startTime <= splAvailability.getEndDate()) {
                    long startDateTimeOfDay = FacilityAPI.getCalendarTime(startTime, splAvailability.getStartTimeAsLocalTime());
                    long endDateTimeOfDay = FacilityAPI.getCalendarTime(startTime, splAvailability.getEndTimeAsLocalTime());

                    while (startDateTimeOfDay <= endDateTimeOfDay && DateTimeUtil.getDayStartTimeOf(startDateTimeOfDay) <= endDateTime) {
                        SlotContext slot = new SlotContext();
                        slot.setSlotCost(splAvailability.getCost());
                        slot.setSlotStartTime(startDateTimeOfDay);
                        slot.setFacilityId(facilityContext.getId());
                        slot.setSlotEndTime((startDateTimeOfDay + facilityContext.getSlotDuration()));
                        if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !FacilityAPI.checkExistingSlots(slotList, slot)) {
                            slotList.add(slot);
                        }
                        //need to consider the slot intervals before starting other slot
                        startDateTimeOfDay = slot.getSlotEndTime();
                    }
                    cal.add(Calendar.DATE, 1);
                    startTime = cal.getTimeInMillis();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(facilityContext.getWeekDayAvailabilities())) {
            Map<Integer, List<WeekDayAvailability>> weekDayMap = new HashMap<>();
            for (WeekDayAvailability weekDay : facilityContext.getWeekDayAvailabilities()) {
                if (!weekDayMap.containsKey(weekDay.getDayOfWeek())) {
                    weekDayMap.put(weekDay.getDayOfWeek(), Collections.singletonList(weekDay));
                } else {
                    List<WeekDayAvailability> weekDays = new ArrayList<>(weekDayMap.get(weekDay.getDayOfWeek()));
                    weekDays.add(weekDay);
                    weekDayMap.put(weekDay.getDayOfWeek(), weekDays);
                }
            }
            Long startDay = startDateTime;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startDay);

            while (startDay <= endDateTime) {
                int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (weekDayMap.containsKey(day)) {
                        List<WeekDayAvailability> weekDaysForDay = weekDayMap.get(day);
                        if (CollectionUtils.isNotEmpty(weekDaysForDay)) {
                            for (WeekDayAvailability wk : weekDaysForDay) {
                                long startDateTimeOfDay = FacilityAPI.getCalendarTime(startDay, wk.getStartTimeAsLocalTime());
                                long endDateTimeOfDay = FacilityAPI.getCalendarTime(startDay, wk.getEndTimeAsLocalTime());

                                while (startDateTimeOfDay < endDateTimeOfDay && DateTimeUtil.getDayStartTimeOf(startDateTimeOfDay) <= endDateTime) {
                                    SlotContext slot = new SlotContext();
                                    slot.setSlotCost(wk.getCost());
                                    slot.setSlotStartTime(startDateTimeOfDay);
                                    slot.setSlotEndTime((startDateTimeOfDay + facilityContext.getSlotDuration()));
                                    slot.setFacilityId(facilityContext.getId());
                                    if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !FacilityAPI.checkExistingSlots(slotList, slot)) {
                                        slotList.add(slot);
                                    }
                                    //need to consider the slot intervals before starting other slot
                                    startDateTimeOfDay = slot.getSlotEndTime();
                                }
                                facilityContext.setSlotGeneratedUpto(startDateTimeOfDay);
                            }
                        }
                    }
               // }
                cal.add(Calendar.DAY_OF_MONTH, 1);
                startDay = cal.getTimeInMillis();

            }
        }
        return slotList;

    }

    public static void updateGeneratedUptoInFacilityAndAddSlots(FacilityContext facility) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(facility.getSlots())) {
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.SLOTS);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.SLOTS);
            V3RecordAPI.addRecord(false, facility.getSlots(), module, fields);
        }

        FacilioModule facilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        List<FacilioField> facilityModuleFields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        RecordAPI.updateRecord(facility, facilityModule, facilityModuleFields);
    }

    public static List<SlotContext> getAvailabilitySlots(FacilityContext facility, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.SLOTS;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SlotContext> builder = new SelectRecordsBuilder<SlotContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(SlotContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));

        List<SlotContext> list = builder.get();
        return  list;
    }

    public static List<FacilityContext> getFacilityList() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.FACILITY;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilityContext> builder = new SelectRecordsBuilder<FacilityContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(FacilityContext.class);

        List<FacilityContext> list = builder.get();
        return  list;
    }

    public static void createSlots(FacilityContext facility, long startTime, Long endTime) throws Exception {
        List<SlotContext> slots = FacilityAPI.getFacilitySlots(facility, startTime, endTime);
        facility.setSlots(slots);
        FacilityAPI.updateGeneratedUptoInFacilityAndAddSlots(facility);
    }

    public static List<WeekDayAvailability> getWeekDayAvailabilityForDay(Long facilityId, Integer dayOfWeek) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = V3Context.class;
        }
        SelectRecordsBuilder<WeekDayAvailability> builder = new SelectRecordsBuilder<WeekDayAvailability>()
                .module(module)
                .beanClass(beanClassName)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("FACILITY_ID", "facilityId", String.valueOf(facilityId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("DAY_OF_WEEK", "dayOfWeek", String.valueOf(dayOfWeek), NumberOperators.EQUALS));

        ;

        List<WeekDayAvailability> records = builder.get();
        return records;
    }

    public static List<BookingSlotsContext> getBookingSlots(Long bookingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String bookingSlots = FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS;
        List<FacilioField> fields = modBean.getAllFields(bookingSlots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<BookingSlotsContext> builder = new SelectRecordsBuilder<BookingSlotsContext>()
                .moduleName(bookingSlots)
                .select(fields)
                .beanClass(BookingSlotsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("booking"), String.valueOf(bookingId), NumberOperators.EQUALS));

        List<BookingSlotsContext> list = builder.get();
        return  list;
    }


}
