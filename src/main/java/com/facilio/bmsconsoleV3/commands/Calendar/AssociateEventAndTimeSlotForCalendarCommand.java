package com.facilio.bmsconsoleV3.commands.Calendar;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CalendarActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AssociateEventAndTimeSlotForCalendarCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
    List<V3CalendarContext> calendarContextList = (List<V3CalendarContext>) recordMap.get(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
    FacilioModule calendarTimeSlotModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
    if(CollectionUtils.isEmpty(calendarContextList)){
        return false;
    }
    List<ModuleBaseWithCustomFields> records = new ArrayList<>();
    for(V3CalendarContext v3CalendarContext : calendarContextList){
        V3CalendarContext calendarContext = new V3CalendarContext();
        calendarContext.setId(v3CalendarContext.getId());
        List<V3CalendarEventMappingContext> calendarEventMappingContextList = v3CalendarContext.getCalendarEventMappingContextList();
        List<V3EventContext> existingCalendarEventList = CalendarApi.getMappedEventList(v3CalendarContext.getId());
        List<V3EventContext> incomingEventList = new ArrayList<>();
        List<Long> incomingEventIdList = new ArrayList<>();
        Map<Long,V3CalendarEventMappingContext> eventIdVsEventMappingMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(calendarEventMappingContextList)){
            incomingEventList = calendarEventMappingContextList.stream().map(V3CalendarEventMappingContext::getEvent).collect(Collectors.toList());
            incomingEventIdList = incomingEventList.stream().map(V3EventContext::getId).collect(Collectors.toList());
            eventIdVsEventMappingMap = IntStream.range(0, incomingEventIdList.size()).boxed().collect(Collectors.toMap(incomingEventIdList::get, calendarEventMappingContextList::get));
        }
        if(CollectionUtils.isEmpty(calendarEventMappingContextList) && CollectionUtils.isEmpty(existingCalendarEventList)){
            //Skip if calendar has no existing event and no incoming event
            continue;
        }
        else if(CollectionUtils.isEmpty(calendarEventMappingContextList) && CollectionUtils.isNotEmpty(existingCalendarEventList)){
            // Deleting all Existing Events Mapping and TimeSlot if the incoming list is Empty And Existing list is not empty
            CalendarApi.deleteRemovedEventsOfCalendar(existingCalendarEventList.stream().map(V3EventContext::getId).collect(Collectors.toList()),v3CalendarContext.getId());
        }
        else if(CollectionUtils.isEmpty(existingCalendarEventList) && CollectionUtils.isNotEmpty(calendarEventMappingContextList)){
            // Add Events To Calendar If The Calendar Has No Existing Event
            for(V3EventContext eventContext : incomingEventList){
                CalendarApi.addEventWithCalendar(calendarContext,eventContext,false);
                if(CollectionUtils.isNotEmpty(eventIdVsEventMappingMap.get(eventContext.getId()).getCalendarTimeSlotContextList())) {
                    records.addAll(CalendarApi.createCalendarTimeSlotObjectList(eventIdVsEventMappingMap.get(eventContext.getId()).getCalendarTimeSlotContextList(), calendarContext, eventContext));
                }
            }
        }
        else{
            List<Long> existingEventIdList = existingCalendarEventList.stream().map(V3EventContext::getId).collect(Collectors.toList());

            List<Long> eventIdsToBeAdded = new ArrayList<>();
            eventIdsToBeAdded.addAll(incomingEventIdList);
            eventIdsToBeAdded.removeAll(existingEventIdList);

            List<Long> eventIdsToBeDeleted = new ArrayList<>();
            eventIdsToBeDeleted.addAll(existingEventIdList);
            eventIdsToBeDeleted.removeAll(incomingEventIdList);

            List<Long> eventIdsToBeUpdated = new ArrayList<>();
            eventIdsToBeUpdated.addAll(existingEventIdList);
            //eventIdsToBeUpdated.addAll(eventIdsToBeAdded);
            eventIdsToBeUpdated.removeAll(eventIdsToBeDeleted);

            if(CollectionUtils.isNotEmpty(eventIdsToBeAdded)){
                for(Long eventId : eventIdsToBeAdded) {
                    V3EventContext eventContext = new V3EventContext();
                    eventContext.setId(eventId);
                    CalendarApi.addEventWithCalendar(calendarContext, eventContext, false);
                    records.addAll(CalendarApi.createCalendarTimeSlotObjectList(eventIdVsEventMappingMap.get(eventId).getCalendarTimeSlotContextList(), calendarContext, eventContext));
                }
                JSONObject info = new JSONObject();
                List<V3EventContext> eventContextList = V3RecordAPI.getRecordsList(FacilioConstants.Calendar.EVENT_MODULE_NAME,eventIdsToBeAdded);
                List<String> eventNameList = eventContextList.stream().map(V3EventContext::getName).collect(Collectors.toList());
                info.put(FacilioConstants.ContextNames.VALUES,eventNameList);
                CommonCommandUtil.addActivityToContext(v3CalendarContext.getId(), -1L,CalendarActivityType.ASSOCIATE_EVENT,info,(FacilioContext) context);
            }
            if(CollectionUtils.isNotEmpty(eventIdsToBeDeleted)){
                CalendarApi.deleteRemovedEventsOfCalendar(eventIdsToBeDeleted,v3CalendarContext.getId());
                JSONObject info = new JSONObject();
                List<V3EventContext> eventContextList = V3RecordAPI.getRecordsList(FacilioConstants.Calendar.EVENT_MODULE_NAME,eventIdsToBeDeleted);
                List<String> eventNameList = eventContextList.stream().map(V3EventContext::getName).collect(Collectors.toList());
                info.put(FacilioConstants.ContextNames.VALUES,eventNameList);
                CommonCommandUtil.addActivityToContext(v3CalendarContext.getId(), -1L,CalendarActivityType.REMOVED_EVENT,info,(FacilioContext) context);
            }
            if(CollectionUtils.isNotEmpty(eventIdsToBeUpdated)){
                for(Long eventId : eventIdsToBeUpdated){
                    V3EventContext eventContext = new V3EventContext();
                    eventContext.setId(eventId);
                    V3CalendarEventMappingContext calendarEventMappingContext = eventIdVsEventMappingMap.get(eventId);
                    V3CalendarEventMappingContext v3CalendarEventMappingContextToBeUpdated = CalendarApi.getCalendarEventMapping(calendarContext.getId(),eventId);
                    boolean check = CalendarApi.checkForDifference(eventId,calendarEventMappingContext.getCalendarTimeSlotContextList());
                    v3CalendarEventMappingContextToBeUpdated.setIsEventEdited(check);
                    CalendarApi.updateCalendarMapping(v3CalendarEventMappingContextToBeUpdated);
                    if(CollectionUtils.isNotEmpty(calendarEventMappingContext.getCalendarTimeSlotContextList())) {
                        records.addAll(CalendarApi.createCalendarTimeSlotObjectList(calendarEventMappingContext.getCalendarTimeSlotContextList(), calendarContext, eventContext));
                    }
                }
                CalendarApi.deleteCalendarTimeSlot(v3CalendarContext.getId(),eventIdsToBeUpdated);
            }

        }
        if(CollectionUtils.isNotEmpty(records)){
            V3Util.createRecord(calendarTimeSlotModule,records);
        }



    }
        return false;
    }
}
