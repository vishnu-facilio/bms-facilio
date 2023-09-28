package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarEventMappingContext;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarTimeSlotContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FillTimeSlotForCalendarEventMappingContext extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String,Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3CalendarEventMappingContext> calendarEventMappingContextList = (List<V3CalendarEventMappingContext>) recordMap.get(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        if(CollectionUtils.isEmpty(calendarEventMappingContextList)){
            return false;
        }
        for(V3CalendarEventMappingContext calendarEventMappingContext : calendarEventMappingContextList){
            calendarEventMappingContext.setEvent(V3RecordAPI.getRecord(FacilioConstants.Calendar.EVENT_MODULE_NAME,calendarEventMappingContext.getEvent().getId(), V3EventContext.class));
            List<V3CalendarTimeSlotContext> calendarTimeSlotContextList = CalendarApi.getTimeSlotOfEventAssociatedWithCalendar(calendarEventMappingContext.getCalendar().getId(),calendarEventMappingContext.getEvent().getId());
            if(CollectionUtils.isNotEmpty(calendarTimeSlotContextList)){
                calendarEventMappingContext.setCalendarTimeSlotContextList(calendarTimeSlotContextList);
                List<String> calendarEventTimeSlotString = new ArrayList<>();
                for(V3CalendarTimeSlotContext calendarTimeSlotContext : calendarTimeSlotContextList){
                    calendarEventTimeSlotString.add(CalendarApi.convertMinuteToHourAndMins(calendarTimeSlotContext.getStartMin())+" - "+CalendarApi.convertMinuteToHourAndMins(calendarTimeSlotContext.getEndMin()));
                }
                calendarEventMappingContext.setCalendarEventTimeSlotString(calendarEventTimeSlotString);
            }
        }
        return false;
    }
}
