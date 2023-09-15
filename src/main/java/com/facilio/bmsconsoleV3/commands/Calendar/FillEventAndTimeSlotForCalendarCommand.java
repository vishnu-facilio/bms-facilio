package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FillEventAndTimeSlotForCalendarCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3CalendarContext> calendarContextList = (List<V3CalendarContext>) recordMap.get(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(CollectionUtils.isEmpty(calendarContextList)){
            return false;
        }
        for(V3CalendarContext calendarContext : calendarContextList) {
            List<V3CalendarEventMappingContext> calendarEventMappingContextList = CalendarApi.getEventCalendarMapping(calendarContext.getId());
            if (CollectionUtils.isEmpty(calendarEventMappingContextList)) {
                continue;
            }
            for(V3CalendarEventMappingContext calendarEventMappingContext : calendarEventMappingContextList){
                calendarEventMappingContext.setEvent(V3RecordAPI.getRecord(FacilioConstants.Calendar.EVENT_MODULE_NAME,calendarEventMappingContext.getEvent().getId(), V3EventContext.class));
                List<V3CalendarTimeSlotContext> calendarTimeSlotContextList = CalendarApi.getTimeSlotOfEventAssociatedWithCalendar(calendarContext.getId(),calendarEventMappingContext.getEvent().getId());
                if(CollectionUtils.isNotEmpty(calendarTimeSlotContextList)){
                    calendarEventMappingContext.setCalendarTimeSlotContextList(calendarTimeSlotContextList);
                    List<String> calendarEventTimeSlotString = new ArrayList<>();
                    for(V3CalendarTimeSlotContext calendarTimeSlotContext : calendarTimeSlotContextList){
                        calendarEventTimeSlotString.add(CalendarApi.convertMinuteToHourAndMins(calendarTimeSlotContext.getStartMin())+" - "+CalendarApi.convertMinuteToHourAndMins(calendarTimeSlotContext.getEndMin()));
                    }
                    calendarEventMappingContext.setCalendarEventTimeSlotString(calendarEventTimeSlotString);
                }
            }
            calendarContext.setCalendarEventMappingContextList(calendarEventMappingContextList);
        }

        return false;
    }
}
