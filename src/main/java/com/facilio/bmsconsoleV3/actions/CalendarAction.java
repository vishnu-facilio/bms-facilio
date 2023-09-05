package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarSlotsContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CalendarAction extends V3Action {
    private Long calendarId;
    private Long startTime;
    private Long endTime;
    public String getCalendarSlots() throws Exception{
        List<V3CalendarSlotsContext> calendarSlotsContextList = CalendarApi.getCalendarSlots(calendarId,startTime,endTime);
        if(CollectionUtils.isNotEmpty(calendarSlotsContextList)){
            for(V3CalendarSlotsContext calendarSlotsContext : calendarSlotsContextList){
                calendarSlotsContext.setStart(calendarSlotsContext.getDate(calendarSlotsContext.getCalendarYear(),calendarSlotsContext.getCalendarMonth(),calendarSlotsContext.getCalendarDate(),calendarSlotsContext.getSlotStartTime(),false));
                calendarSlotsContext.setEnd(calendarSlotsContext.getDate(calendarSlotsContext.getCalendarYear(),calendarSlotsContext.getCalendarMonth(),calendarSlotsContext.getCalendarDate(),calendarSlotsContext.getSlotEndTime(),false));
                calendarSlotsContext.setDisplayStartTime(calendarSlotsContext.getTime(calendarSlotsContext.getSlotStartTime(),true));
                calendarSlotsContext.setDisplayEndTime(calendarSlotsContext.getTime(calendarSlotsContext.getSlotEndTime(),true));
                V3EventContext eventContext = V3RecordAPI.getRecord(FacilioConstants.Calendar.EVENT_MODULE_NAME,calendarSlotsContext.getEvent().getId(), V3EventContext.class);
                calendarSlotsContext.setBgColor(eventContext.getEventTypeEnum().getBgColor());
                calendarSlotsContext.setBorderColor(eventContext.getEventTypeEnum().getBorderColor());
                calendarSlotsContext.setTitle(eventContext.getName());
            }
        }
        setData("result",calendarSlotsContextList);
        return SUCCESS;
    }
}
