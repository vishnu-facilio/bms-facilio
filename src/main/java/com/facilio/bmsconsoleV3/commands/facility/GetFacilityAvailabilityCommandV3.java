package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetFacilityAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> facilityIds = (List<Long>)Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        Long startDateTime = -1L;
        Long endDateTime = -1L;
        Boolean fetchAvailability = false;
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("startTime") && queryParams.containsKey("endTime")) {
            fetchAvailability = true;
            startDateTime = Long.valueOf(((List<String>)queryParams.get("startTime")).get(0));
            endDateTime = Long.valueOf(((List<String>)queryParams.get("endTime")).get(0));

        }

        if(CollectionUtils.isEmpty(facilityIds)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility Id cannot be null");
        }
            for(Long id : facilityIds) {

                FacilityContext facilityContext = (FacilityContext) CommandUtil.getModuleData(context, moduleName,id);
                FacilityAPI.setFacilitySpecialAvailability(facilityContext, startDateTime, endDateTime);
                FacilityAPI.setFacilityWeekDayAvailability(facilityContext);

                if(fetchAvailability) {

                    List<SlotContext> slotList = new ArrayList<>();

                    if (CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {
                        for (FacilitySpecialAvailabilityContext splAvailability : facilityContext.getFacilitySpecialAvailabilities()) {
                            Long startTime = splAvailability.getStartDate();
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(startTime);

                            while (startTime <= splAvailability.getEndDate()) {
                                long startDateTimeOfDay = FacilityAPI.getCalendarTime(startTime, splAvailability.getStartTimeAsLocalTime());
                                long endDateTimeOfDay = FacilityAPI.getCalendarTime(startTime, splAvailability.getEndTimeAsLocalTime());

                                while (startDateTimeOfDay <= endDateTimeOfDay && startDateTimeOfDay <= endDateTime) {
                                    SlotContext slot = new SlotContext();
                                    slot.setSlotCost(splAvailability.getCost());
                                    slot.setSlotStartTime(startDateTimeOfDay);
                                    slot.setSlotEndTime(startDateTimeOfDay + facilityContext.getSlotDuration());
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

                                        while (startDateTimeOfDay < endDateTimeOfDay && startDateTimeOfDay < endDateTime) {
                                            SlotContext slot = new SlotContext();
                                            slot.setSlotCost(wk.getCost());
                                            slot.setSlotStartTime(startDateTimeOfDay);
                                            slot.setSlotEndTime(startDateTimeOfDay + facilityContext.getSlotDuration());
                                            if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !FacilityAPI.checkExistingSlots(slotList, slot)) {
                                                slotList.add(slot);
                                            }
                                            //need to consider the slot intervals before starting other slot
                                            startDateTimeOfDay = slot.getSlotEndTime();
                                        }
                                    }
                                }
                            }
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            startDay = cal.getTimeInMillis();

                        }
                    }

                    if (CollectionUtils.isNotEmpty(slotList)) {
                        facilityContext.setSlots(slotList);
                    }
                }
            }


        return false;
    }
}
