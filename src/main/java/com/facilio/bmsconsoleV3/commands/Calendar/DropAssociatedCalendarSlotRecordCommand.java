package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DropAssociatedCalendarSlotRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3EventContext> calendarEventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(calendarEventContextList)){
            return false;
        }
        List<Long> calendarIdList = new ArrayList<>();
        for(V3EventContext v3CalendarEventContext : calendarEventContextList){
            List<V3CalendarContext> calendarList = CalendarApi.getCalendarIdsAssociatedWithNonEditedEvent(v3CalendarEventContext.getId());
            if(calendarList == null){
                continue;
            }
            calendarIdList = calendarList.stream().map(V3CalendarContext::getId).collect(Collectors.toList());
            for(Long calendarId : calendarIdList){
                CalendarApi.dropCalendarSlotsOfCalendar(calendarId);
            }
        }
        if(CollectionUtils.isNotEmpty(calendarIdList)){
            context.put(FacilioConstants.Calendar.SAVE_CALENDAR_ID_LIST,calendarIdList);
        }
        return false;
    }
}
