package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3PhotosContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.V3Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FacilityAPI {
    private static Logger log = LogManager.getLogger(FacilityAPI.class.getName());

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

    public static Boolean checkForUnavailability(Long slotStartTime, Long slotEndTime, List<FacilitySpecialAvailabilityContext> unavailabilityList) {

        if (CollectionUtils.isNotEmpty(unavailabilityList)) {
            for(FacilitySpecialAvailabilityContext unavailability : unavailabilityList){
                if(unavailability.getSpecialType() != null && unavailability.getSpecialType() != FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_UNAVAILABILITY.getIndex()) {
                    continue;
                }
                Long startTime = unavailability.getStartDate();
                ZonedDateTime cal = DateTimeUtil.getDateTime(startTime);

                while (startTime <= unavailability.getEndDate()) {

                    long startDateTimeOfDay = getZonedTime(startTime, unavailability.getActualStartTimeAsLocalTime());
                    long endDateTimeOfDay = getZonedTime(startTime, unavailability.getActualEndTimeAsLocalTime());

                    if(slotStartTime > startDateTimeOfDay && slotStartTime < endDateTimeOfDay){
                        return true;
                    }

                    cal = cal.plusDays(1);
                    startTime = DateTimeUtil.getMillis(cal, false);
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

    public static Long getZonedTime(Long startTime, LocalTime localTime) {
        ZonedDateTime cal = DateTimeUtil.getDateTime(startTime);

        cal = cal.withHour(localTime.getHour())
                .withMinute(localTime.getMinute())
                .withSecond(localTime.getSecond());
        long startDateTimeOfDay = DateTimeUtil.getMillis(cal, false);
        return startDateTimeOfDay;

    }


    public static List<SlotContext> getFacilitySlots(FacilityContext facilityContext, Long startDateTime, Long endDateTime) throws Exception {

        FacilityAPI.setFacilitySpecialAvailability(facilityContext, startDateTime, endDateTime);
        FacilityAPI.setFacilityWeekDayAvailability(facilityContext);
        List<SlotContext> slotList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {
            for (FacilitySpecialAvailabilityContext splAvailability : facilityContext.getFacilitySpecialAvailabilities()) {
                Long startTime = splAvailability.getStartDate();
                ZonedDateTime cal = DateTimeUtil.getDateTime(startTime);

                while (startTime <= splAvailability.getEndDate()) {
                    long startDateTimeOfDay = FacilityAPI.getZonedTime(startTime, splAvailability.getActualEndTimeAsLocalTime());
                    long endDateTimeOfDay = FacilityAPI.getZonedTime(startTime, splAvailability.getActualEndTimeAsLocalTime());

                    while (startDateTimeOfDay <= endDateTimeOfDay && DateTimeUtil.getDayStartTimeOf(startDateTimeOfDay) <= endDateTime) {
                        SlotContext slot = new SlotContext();
                        slot.setSlotCost(splAvailability.getCost());
                        slot.setSlotStartTime(startDateTimeOfDay);
                        slot.setFacilityId(facilityContext.getId());
                        slot.setSlotEndTime((startDateTimeOfDay + (facilityContext.getSlotDuration() * 1000)));
                        if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !FacilityAPI.checkExistingSlots(slotList, slot)) {
                            slotList.add(slot);
                        }
                        //need to consider the slot intervals before starting other slot
                        startDateTimeOfDay = slot.getSlotEndTime();
                    }
                    cal = cal.plusDays(1);
                    startTime = DateTimeUtil.getMillis(cal, false);
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
            ZonedDateTime cal = DateTimeUtil.getDateTime(startDay);

            while (startDay <= endDateTime) {
                int day = cal.get(ChronoField.DAY_OF_WEEK);
                if (weekDayMap.containsKey(day)) {
                        List<WeekDayAvailability> weekDaysForDay = weekDayMap.get(day);
                        if (CollectionUtils.isNotEmpty(weekDaysForDay)) {
                            for (WeekDayAvailability wk : weekDaysForDay) {
                                long startDateTimeOfDay = FacilityAPI.getZonedTime(startDay, wk.getActualStartTimeAsLocalTime());
                                long endDateTimeOfDay = FacilityAPI.getZonedTime(startDay, wk.getActualEndTimeAsLocalTime());

                                while (startDateTimeOfDay < endDateTimeOfDay && DateTimeUtil.getDayStartTimeOf(startDateTimeOfDay) <= endDateTime) {
                                    SlotContext slot = new SlotContext();
                                    slot.setSlotCost(wk.getCost());
                                    slot.setSlotStartTime(startDateTimeOfDay);
                                    slot.setSlotEndTime((startDateTimeOfDay + (facilityContext.getSlotDuration() * 1000)));
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
                cal = cal.plus(1, ChronoUnit.DAYS);
                startDay = DateTimeUtil.getMillis(cal, false);

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
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facilityId"), String.valueOf(facility.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));

        List<SlotContext> list = builder.get();
        return  list;
    }
    public static List<SlotContext> getFacilityBookedSlotsForTimeRange(List<Long> facilityIds, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.SLOTS;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SlotContext> builder = new SelectRecordsBuilder<SlotContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(SlotContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facilityId"), StringUtils.join(facilityIds,","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("bookingCount"), String.valueOf(0), NumberOperators.GREATER_THAN));

        if(startTime != null && endTime != null) {
            DateRange range = new DateRange(startTime,endTime);
            builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), range.toString(), DateOperators.BETWEEN))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotEndTime"), range.toString(), DateOperators.BETWEEN));
        }

        List<SlotContext> slotList = builder.get();
        return  slotList;
    }
    public static List<SlotContext> getFacilitySlotsForTimeRange(List<Long> facilityIds, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.SLOTS;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SlotContext> builder = new SelectRecordsBuilder<SlotContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(SlotContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facilityId"), StringUtils.join(facilityIds,","), NumberOperators.EQUALS));
        if(startTime != null && endTime != null) {
        	DateRange range = new DateRange(startTime,endTime);
        	builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), range.toString(), DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotEndTime"), range.toString(), DateOperators.BETWEEN));
        }

        List<SlotContext> slotList = builder.get();
        return  slotList;
    }
    
    public static List<SlotContext> getFacilitySlotsForTimeRangeWithSupplements(List<Long> facilityIds, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.SLOTS;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SlotContext> builder = new SelectRecordsBuilder<SlotContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(SlotContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facilityId"), StringUtils.join(facilityIds,","), NumberOperators.EQUALS));
        if(startTime != null && endTime != null) {
        	DateRange range = new DateRange(startTime,endTime);
        	builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotStartTime"), range.toString(), DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("slotEndTime"), range.toString(), DateOperators.BETWEEN));
        }
        
//		builder.fetchSupplement((LookupField) fieldsAsMap.get("bookingRequestedBy"));

        List<SlotContext> slotList = builder.get();
        return  slotList;
    }
    public static List<FacilityContext> getFacilityList(Long parentId, Long parentModuleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slots = FacilioConstants.ContextNames.FacilityBooking.FACILITY;
        List<FacilioField> fields = modBean.getAllFields(slots);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilityContext> builder = new SelectRecordsBuilder<FacilityContext>()
                .moduleName(slots)
                .select(fields)
                .beanClass(FacilityContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("parentModuleId"), String.valueOf(parentModuleId), NumberOperators.EQUALS));

        List<FacilityContext> list = builder.get();
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
    

    public static List<BookingSlotsContext> getFacilityBookingListWithSlots(List<Long> slotIds) throws Exception {
    	
    	List<BookingSlotsContext> list = new ArrayList<>();
    	
    	if(CollectionUtils.isNotEmpty(slotIds)) {
    		
    		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	String bookingSlots = FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS;
            FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
            List<FacilioField> slotfields = modBean.getAllFields(bookingSlots);
            Map<String, FacilioField> slotfieldsAsMap = FieldFactory.getAsMap(slotfields);
            
            
        		List<LookupField> additionaLookups = new ArrayList<LookupField>();
        		LookupFieldMeta moduleStateField = new LookupFieldMeta((LookupField) slotfieldsAsMap.get("booking"));

        	    FacilioField requestedFor = FieldFactory.getField("bookingRequestedBy", "BOOKING_REQUESTED_BY", bookingModule, FieldType.LOOKUP);

                List<FacilioField> selectFieldsList = new ArrayList<>();
                selectFieldsList.add(requestedFor);
                
                moduleStateField.setSelectFields(selectFieldsList);
                additionaLookups.add(moduleStateField);

                
    
            SelectRecordsBuilder<BookingSlotsContext> slotbuilder = new SelectRecordsBuilder<BookingSlotsContext>()
                    .moduleName(bookingSlots)
                    .select(slotfields)
                    .beanClass(BookingSlotsContext.class)
                    .andCondition(CriteriaAPI.getCondition(slotfieldsAsMap.get("slot"), StringUtils.join(slotIds,","), NumberOperators.EQUALS))
                    .fetchSupplements(additionaLookups);
    

    		
            list = slotbuilder.get();
        }
    	
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
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("booking"), String.valueOf(bookingId), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) fieldsAsMap.get("slot"));

        List<BookingSlotsContext> list = builder.get();
        try {
            Collections.sort(list, new Comparator<BookingSlotsContext>() {
                @Override
                public int compare(BookingSlotsContext obj1, BookingSlotsContext obj2) {
                    return Long.compare(obj1.getSlot().getSlotStartTime(), obj2.getSlot().getSlotStartTime());
                }
            });
        }catch (Exception e)
        {
            log.info("Error occurred in fetching and sorting slots"+ e);
        }
        return  list;
    }

    public static List<V3ExternalAttendeeContext> getExternalAttendees(Long bookingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String externalAttendees = FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE;
        List<FacilioField> fields = modBean.getAllFields(externalAttendees);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3ExternalAttendeeContext> builder = new SelectRecordsBuilder<V3ExternalAttendeeContext>()
                .moduleName(externalAttendees)
                .select(fields)
                .beanClass(V3ExternalAttendeeContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facilityBooking"), String.valueOf(bookingId), NumberOperators.EQUALS));

        List<V3ExternalAttendeeContext> list = builder.get();
        return  list;
    }


    public static List<BookingPaymentContext> getBookingPayments(Long bookingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String payments = FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_PAYMENTS;
        List<FacilioField> fields = modBean.getAllFields(payments);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<BookingPaymentContext> builder = new SelectRecordsBuilder<BookingPaymentContext>()
                .moduleName(payments)
                .select(fields)
                .beanClass(BookingPaymentContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("booking"), String.valueOf(bookingId), NumberOperators.EQUALS));

        List<BookingPaymentContext> list = builder.get();
        return  list;
    }

    public static List<V3PhotosContext> getFacilityPhotoId(Long facilityId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_PHOTOS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_PHOTOS);
        Condition idCondition = new Condition();
        idCondition.setField(modBean.getField("parentId", module.getName()));
        idCondition.setOperator(NumberOperators.EQUALS);
        idCondition.setValue(String.valueOf(facilityId));

        SelectRecordsBuilder<V3PhotosContext> selectBuilder = new SelectRecordsBuilder<V3PhotosContext>()
                .moduleName(module.getName())
                .beanClass(V3PhotosContext.class)
                .select(fields)
                .table(module.getTableName())
                .andCondition(idCondition);
        List<V3PhotosContext> photos = selectBuilder.get();
        return photos;

    }
	

	public static List<V3FacilityBookingContext> getFacilityBooking(List<Long> facilityIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String facilitybooking = FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING;
		List<FacilioField> fields = modBean.getAllFields(facilitybooking);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3FacilityBookingContext> builder = new SelectRecordsBuilder<V3FacilityBookingContext>().moduleName(facilitybooking)
				.select(fields).beanClass(V3FacilityBookingContext.class).andCondition(CriteriaAPI.getCondition(
						fieldsAsMap.get("facility"), StringUtils.join(facilityIds, ","), NumberOperators.EQUALS)).andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isCancelled"),String.valueOf(false), BooleanOperators.IS));
		List<V3FacilityBookingContext> booking = builder.get();
		return booking;
	}

}
