package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarSlotCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<Long> calendarIdList = new ArrayList<>();
        List<V3CalendarContext> v3CalendarContextList = (List<V3CalendarContext>) recordMap.get(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(CollectionUtils.isNotEmpty(v3CalendarContextList)){
            calendarIdList.addAll(v3CalendarContextList.stream().map(V3CalendarContext::getId).collect(Collectors.toList()));
        }
        if(context.containsKey(FacilioConstants.Calendar.SAVE_CALENDAR_ID_LIST)){
            calendarIdList.addAll((List<Long>) context.get(FacilioConstants.Calendar.SAVE_CALENDAR_ID_LIST));
        }
        if(CollectionUtils.isEmpty(calendarIdList)){
            return false;
        }
        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime startDate = currentTime.plusDays(1);
        ZonedDateTime endDate = currentTime.plusDays(90);
        for(Long calendarId : calendarIdList){
            CalendarApi.populateCalendarView(calendarId,startDate.toEpochSecond()*1000,endDate.toEpochSecond()*1000);
        }

        return false;
    }
}
