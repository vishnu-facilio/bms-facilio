package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class FacilityAPI {

    public static void setFacilityAmenities(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityAmenityModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_AMENITIES;
        List<FacilioField> fields = modBean.getAllFields(facilityAmenityModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilityAmenitiesContext> builder = new SelectRecordsBuilder<FacilityAmenitiesContext>()
                .moduleName(facilityAmenityModName)
                .select(fields)
                .beanClass(FacilityAmenitiesContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<FacilityAmenitiesContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setAmenities(list);
        }
    }

    public static void setFacilityWeekDayAvailability(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityWeekDayModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilityWeekDayModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WeekDayAvailability> builder = new SelectRecordsBuilder<WeekDayAvailability>()
                .moduleName(facilityWeekDayModName)
                .select(fields)
                .beanClass(WeekDayAvailability.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<WeekDayAvailability> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setWeekDayAvailabilities(list);
        }
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

}
