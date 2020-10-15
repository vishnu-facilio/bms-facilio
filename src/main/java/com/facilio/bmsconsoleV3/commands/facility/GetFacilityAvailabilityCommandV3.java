package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class GetFacilityAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> facilityIds = (List<Long>)Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        Long startDateTime = null;
        Long endDateTime = null;
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

                    Map<Long, SlotContext> slotMap = new HashMap<>();
                    Map<Long, FacilitySpecialAvailabilityContext> unavailabilityMAp = new HashMap<>();

                    if (CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {
                        for (FacilitySpecialAvailabilityContext splAvailability : facilityContext.getFacilitySpecialAvailabilities()) {
                            Long startTime = splAvailability.getStartDate();
                            while (startTime <= splAvailability.getEndDate()) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(startTime);
                                int date = c.get(Calendar.DATE);
                                int month = c.get(Calendar.MONTH);
                                int year = c.get(Calendar.YEAR);

                                int startTimeInSecondsOfDay = splAvailability.getStartTimeAsLocalTime().toSecondOfDay();
                                c.set(year, month, date, -1, -1, startTimeInSecondsOfDay);
                                long startDateTimeOfDay = c.getTimeInMillis();

                                int endTimeInSecondsOfDay = splAvailability.getEndTimeAsLocalTime().toSecondOfDay();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(year, month, date, -1, -1, endTimeInSecondsOfDay);
                                long endDateTimeOfDay = endTime.getTimeInMillis();

                                while (startDateTimeOfDay <= endDateTimeOfDay && startDateTimeOfDay <= endDateTime) {
                                    SlotContext slot = new SlotContext();
                                    slot.setSlotCost(splAvailability.getCost());
                                    slot.setSlotStartTime(startDateTimeOfDay);
                                    slot.setSlotEndTime(startDateTimeOfDay + facilityContext.getSlotDuration());
                                    if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !slotMap.containsKey(slot.getSlotStartTime())) {
                                        slotMap.put(slot.getSlotStartTime(), slot);
                                    }
                                    //need to consider the slot intervals before starting other slot
                                    startDateTimeOfDay = slot.getSlotEndTime();
                                }
                                c.add(Calendar.DATE, 1);
                                startTime = c.getTimeInMillis();
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(facilityContext.getWeekDayAvailabilities())) {
                        Map<Integer, List<WeekDayAvailability>> weekDayMap = new HashMap<>();
                        for (WeekDayAvailability weekDay : facilityContext.getWeekDayAvailabilities()) {
                            if (!weekDayMap.containsKey(weekDay.getDayOfWeek())) {
                                weekDayMap.put(weekDay.getDayOfWeek(), Collections.singletonList(weekDay));
                            } else {
                                List<WeekDayAvailability> weekDays = weekDayMap.get(weekDay.getDayOfWeek());
                                weekDays.add(weekDay);
                            }
                        }
                        Long startDay = startDateTime;
                        while (startDay <= endDateTime) {

                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(startDay);
                            int date = c.get(Calendar.DATE);
                            int month = c.get(Calendar.MONTH);
                            int year = c.get(Calendar.YEAR);
                            int day = c.get(Calendar.DAY_OF_WEEK);

                            if (weekDayMap.containsKey(day)) {
                                List<WeekDayAvailability> weekDaysForDay = weekDayMap.get(day);
                                if (CollectionUtils.isNotEmpty(weekDaysForDay)) {
                                    for (WeekDayAvailability wk : weekDaysForDay) {
                                        int startTimeInSecondsOfDay = wk.getStartTimeAsLocalTime().toSecondOfDay();
                                        c.set(year, month, date, -1, -1, startTimeInSecondsOfDay);
                                        long startDateTimeOfDay = c.getTimeInMillis();

                                        int endTimeInSecondsOfDay = wk.getEndTimeAsLocalTime().toSecondOfDay();
                                        Calendar endTime = Calendar.getInstance();
                                        endTime.set(year, month, date, -1, -1, endTimeInSecondsOfDay);
                                        long endDateTimeOfDay = endTime.getTimeInMillis();

                                        while (startDateTimeOfDay <= endDateTimeOfDay && startDateTimeOfDay <= endDateTime) {
                                            SlotContext slot = new SlotContext();
                                            slot.setSlotCost(wk.getCost());
                                            slot.setSlotStartTime(startDateTimeOfDay);
                                            slot.setSlotEndTime(startDateTimeOfDay + facilityContext.getSlotDuration());
                                            if (!FacilityAPI.checkForUnavailability(slot.getSlotStartTime(), slot.getSlotEndTime(), facilityContext.getFacilitySpecialAvailabilities()) && !slotMap.containsKey(slot.getSlotStartTime())) {
                                                slotMap.put(slot.getSlotStartTime(), slot);
                                            }
                                            //need to consider the slot intervals before starting other slot
                                            startDateTimeOfDay = slot.getSlotEndTime();
                                        }
                                    }
                                }
                            }
                            c.add(Calendar.DAY_OF_MONTH, 1);
                            startDay = c.getTimeInMillis();

                        }
                    }

                    if (MapUtils.isNotEmpty(slotMap)) {
                        facilityContext.setSlots(new ArrayList<SlotContext>(slotMap.values()));
                    }
                }
            }


        return false;
    }
}
