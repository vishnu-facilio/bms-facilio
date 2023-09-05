package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;

import java.util.HashMap;
import java.util.List;

public class DropCalendarTimeSlotCommand extends FacilioCommand {
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
        for(V3CalendarContext calendarContext : calendarContextList){
             CalendarApi.dropCalendarSlotsOfCalendar(calendarContext.getId());
        }
        return false;
    }
}
