package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CreateCalendarTimeSlotCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeSlotModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3EventContext> v3CalendarEventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(v3CalendarEventContextList)){
            return false;
        }
        List<ModuleBaseWithCustomFields> records = new ArrayList<>();
        for(V3EventContext v3CalendarEventContext : v3CalendarEventContextList){
            V3EventContext eventContext = new V3EventContext();
            eventContext.setId(v3CalendarEventContext.getId());
            List<V3CalendarContext> calendarList= CalendarApi.getCalendarIdsAssociatedWithNonEditedEvent(v3CalendarEventContext.getId());
            if(CollectionUtils.isEmpty(calendarList)){
                continue;
            }
            List<Long> calendarIdList = calendarList.stream().map(V3CalendarContext::getId).collect(Collectors.toList());
            for(Long calendarId : calendarIdList){
                V3CalendarEventMappingContext v3CalendarEventMappingContext = CalendarApi.getCalendarEventMapping(calendarId,v3CalendarEventContext.getId());
                if(v3CalendarEventMappingContext.getIsEventEdited()){
                    continue;
                }
                else{
                    CalendarApi.deleteTimeSlotOfCalendarEvent(calendarId,v3CalendarEventContext.getId());
                    List<V3EventTimeSlotContext> eventTimeSlotContextList = v3CalendarEventContext.getTimeSlotList();
                    if(CollectionUtils.isEmpty(eventTimeSlotContextList)){
                        continue;
                    }
                    V3CalendarContext calendarContext = new V3CalendarContext();
                    calendarContext.setId(calendarId);
                    for(V3EventTimeSlotContext eventTimeSlotContext : eventTimeSlotContextList){
                        V3CalendarTimeSlotContext calendarTimeSlotContext = CalendarApi.castV3EventTimeSlotContextToV3CalendarTimeSlotContext(eventTimeSlotContext,calendarContext,eventContext);
                        records.add(calendarTimeSlotContext);
                    }
                }
            }

        }
        if(CollectionUtils.isEmpty(records)){
            return false;
        }
        V3Util.createRecord(timeSlotModule,records);
        return false;
    }
}
