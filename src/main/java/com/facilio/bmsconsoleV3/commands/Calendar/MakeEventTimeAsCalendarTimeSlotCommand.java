package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MakeEventTimeAsCalendarTimeSlotCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() == 0){
            return false;
        }
        List<V3CalendarContext> calendarContextList = (List<V3CalendarContext>) recordMap.get(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(CollectionUtils.isEmpty(calendarContextList)){
            return false;
        }
        for(V3CalendarContext v3CalendarContext : calendarContextList){
            V3CalendarContext calendarContext = new V3CalendarContext();
            calendarContext.setId(v3CalendarContext.getId());
            List<V3CalendarEventMappingContext> calendarEventMappingContextList = v3CalendarContext.getCalendarEventMappingContextList();
            if(CollectionUtils.isEmpty(calendarEventMappingContextList)){
                continue;
            }
            for(V3CalendarEventMappingContext v3CalendarEventMappingContext : calendarEventMappingContextList){
                if(CollectionUtils.isEmpty(v3CalendarEventMappingContext.getCalendarTimeSlotContextList())){
                    List<V3EventTimeSlotContext> eventTimeSlotContextList = CalendarApi.getTimeSlotOfEvent(v3CalendarEventMappingContext.getEvent().getId());
                    if(CollectionUtils.isEmpty(eventTimeSlotContextList)){
                        throw new IllegalArgumentException("No Time Slot found for Event - "+v3CalendarEventMappingContext.getEvent().getId());
                    }
                    else{
                        List<V3CalendarTimeSlotContext> calendarTimeSlotContextList = new ArrayList<>();
                        for(V3EventTimeSlotContext eventTimeSlotContext : eventTimeSlotContextList){
                            calendarTimeSlotContextList.add(CalendarApi.castV3EventTimeSlotContextToV3CalendarTimeSlotContext(eventTimeSlotContext,calendarContext,v3CalendarEventMappingContext.getEvent()));
                        }
                        v3CalendarEventMappingContext.setCalendarTimeSlotContextList(calendarTimeSlotContextList);
                    }
                }
            }
        }
        return false;
    }
}
