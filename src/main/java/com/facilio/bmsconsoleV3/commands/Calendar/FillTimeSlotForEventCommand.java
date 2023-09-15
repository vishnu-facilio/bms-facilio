package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventTimeSlotContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FillTimeSlotForEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String,Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3EventContext> v3CalendarEventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(v3CalendarEventContextList)){
            return false;
        }
        for(V3EventContext v3CalendarEventContext : v3CalendarEventContextList){
            List<V3EventTimeSlotContext> eventTimeSlotContextList = CalendarApi.getTimeSlotOfEvent(v3CalendarEventContext.getId());
            if(CollectionUtils.isEmpty(eventTimeSlotContextList)){
                continue;
            }
            List<String> timeSlotStringList = new ArrayList<>();
            for(V3EventTimeSlotContext eventTimeSlotContext : eventTimeSlotContextList){
                timeSlotStringList.add(CalendarApi.convertMinuteToHourAndMins(eventTimeSlotContext.getStartMin())+" - "+CalendarApi.convertMinuteToHourAndMins(eventTimeSlotContext.getEndMin()));
            }
            v3CalendarEventContext.setTimeSlotString(timeSlotStringList);
            v3CalendarEventContext.setTimeSlotList(eventTimeSlotContextList);
        }
        return false;
    }
}
