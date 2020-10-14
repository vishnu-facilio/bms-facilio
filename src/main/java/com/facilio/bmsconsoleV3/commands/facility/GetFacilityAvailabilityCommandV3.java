package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class GetFacilityAvailabilityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long facilityId = (Long)context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_ID);
        Long startDateTime = (Long)context.get(FacilioConstants.ContextNames.FacilityBooking.START_DATE_TIME);
        Long endDateTime = (Long)context.get(FacilioConstants.ContextNames.FacilityBooking.END_DATE_TIME);

        if(facilityId == null || facilityId <= 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Facility Id cannot be null");
        }
        FacilityContext facilityContext = (FacilityContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY, facilityId, FacilityContext.class);
        FacilityAPI.setFacilitySpecialAvailability(facilityContext);
        FacilityAPI.setFacilityWeekDayAvailability(facilityContext);
        Map<Long, SlotContext> slotMap = new HashMap<>();
        Map<Long, FacilitySpecialAvailabilityContext> unavailabilityMAp = new HashMap<>();

        if(CollectionUtils.isNotEmpty(facilityContext.getFacilitySpecialAvailabilities())) {
            for (FacilitySpecialAvailabilityContext splAvailability : facilityContext.getFacilitySpecialAvailabilities()) {
                Long startTime = splAvailability.getStartDateTime();
                if(splAvailability.getSpecialTypeEnum() == FacilitySpecialAvailabilityContext.SpecialType.SPECIAL_UNAVAILABILITY){
                    unavailabilityMAp.put(splAvailability.getStartDateTime(), splAvailability);
                    continue;
                }
                while (startTime <= splAvailability.getEndDateTime()) {
                    SlotContext slot = new SlotContext();
                    slot.setSlotCost(splAvailability.getCost());
                    slot.setSlotStartTime(startTime);
                    slot.setSlotEndTime(startTime + facilityContext.getSlotDuration());
                    slotMap.put(slot.getSlotStartTime(), slot);
                    //need to consider the slot intervals before starting other slot
                    startTime = slot.getSlotEndTime();
                }
            }
        }
        if(CollectionUtils.isNotEmpty(facilityContext.getWeekDayAvailabilities())) {
            Map<Integer, List<WeekDayAvailability>> weekDayMap = new HashMap<>();
            for(WeekDayAvailability weekDay : facilityContext.getWeekDayAvailabilities()){
                if(!weekDayMap.containsKey(weekDay.getDayOfWeek())){
                    weekDayMap.put(weekDay.getDayOfWeek(), Collections.singletonList(weekDay));
                }
                else {
                    List<WeekDayAvailability> weekDays= weekDayMap.get(weekDay.getDayOfWeek());
                    weekDays.add(weekDay);
                }
            }
            Long startDay = startDateTime;
            while(startDay <= endDateTime) {

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(startDay);
                int day = c.get(Calendar.DAY_OF_WEEK);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                if(weekDayMap.containsKey(day)){
                    List<WeekDayAvailability> weekDaysForDay = weekDayMap.get(day);
                    if(CollectionUtils.isNotEmpty(weekDaysForDay)){
                        for(WeekDayAvailability wk : weekDaysForDay){
                            int startTimeInSecondsOfDay = wk.getStartTimeAsLocalTime().toSecondOfDay();
                            c.set(year, month, day, -1,-1,startTimeInSecondsOfDay);
                            long startDateTimeOfDay = c.getTimeInMillis();

                            int endTimeInSecondsOfDay = wk.getEndTimeAsLocalTime().toSecondOfDay();
                            Calendar endTime = Calendar.getInstance();
                            endTime.set(year, month, day, -1,-1,endTimeInSecondsOfDay);
                            long endDateTimeOfDay = endTime.getTimeInMillis();

                            while (startDateTimeOfDay <= endDateTimeOfDay) {
                                SlotContext slot = new SlotContext();
                                slot.setSlotCost(wk.getCost());
                                slot.setSlotStartTime(startDateTimeOfDay);
                                slot.setSlotEndTime(startDateTimeOfDay + facilityContext.getSlotDuration());
                                if(!unavailabilityMAp.containsKey(slot.getSlotStartTime()) && !slotMap.containsKey(slot.getSlotStartTime())) {
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

        return false;
    }
}
