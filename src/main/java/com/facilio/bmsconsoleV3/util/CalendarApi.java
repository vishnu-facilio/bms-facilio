package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.calendar.*;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.enums.EventTypeEnum;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.fms.message.Message;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.sqlite.core.DB;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CalendarApi {
    public static void addEventWithCalendar(V3CalendarContext calendar, V3EventContext eventContext, boolean isEdited) throws Exception{
        if(calendar == null){
            throw new IllegalArgumentException("Calendar Id cannot be Null");
        }
        if(eventContext == null){
            throw new IllegalArgumentException("Event  Can't be Null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        List<ModuleBaseWithCustomFields> records = new ArrayList<>();
        V3CalendarEventMappingContext calendarEventMappingContext = new V3CalendarEventMappingContext();
        calendarEventMappingContext.setCalendar(calendar);
        calendarEventMappingContext.setEvent(eventContext);
        calendarEventMappingContext.setIsEventEdited(isEdited);
        records.add(calendarEventMappingContext);
        V3Util.createRecord(module,records);
        return;
    }
    public static List<V3EventContext> getMappedEventList(Long calendarId) throws Exception{
        List<V3CalendarEventMappingContext> calendarEventMappingContextList = getEventCalendarMapping(calendarId);
        if(CollectionUtils.isEmpty(calendarEventMappingContextList)){
            return null;
        }
        List<V3EventContext> calendarEventContextList = calendarEventMappingContextList.stream().map(V3CalendarEventMappingContext::getEvent).collect(Collectors.toList());
        return V3RecordAPI.getRecordsList(FacilioConstants.Calendar.EVENT_MODULE_NAME,calendarEventContextList.stream().map(V3EventContext::getId).collect(Collectors.toList()));
    }
    public static List<V3CalendarEventMappingContext> getEventCalendarMapping(Long calendarId) throws Exception{
        if(calendarId == null){
            throw new IllegalArgumentException("Calendar Id cannot be Null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarEventMappingContext> selectRecordBuilder = new SelectRecordsBuilder<V3CalendarEventMappingContext>()
                .module(module)
                .select(fields)
                .beanClass(V3CalendarEventMappingContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId), NumberOperators.EQUALS));
        return selectRecordBuilder.get();
    }
    public static void deleteRemovedEventsOfCalendar(List<Long> eventIds, Long calendarId) throws Exception{
        if(calendarId == null){
            throw new IllegalArgumentException("Calendar Id cannot be Null");
        }
        if(CollectionUtils.isEmpty(eventIds)){
            throw new IllegalArgumentException("Event Id List Can't be Null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            DeleteRecordBuilder<V3CalendarEventMappingContext> deleteRecordBuilder = new DeleteRecordBuilder<V3CalendarEventMappingContext>()
                    .module(module)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"), StringUtils.join(eventIds, ","),NumberOperators.EQUALS));
            deleteRecordBuilder.markAsDelete();
            deleteCalendarTimeSlot(calendarId,eventIds);
    }
    public static void deleteCalendarTimeSlot(Long calendarId, List<Long> eventIds) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<V3CalendarEventMappingContext> deleteRecordBuilder = new DeleteRecordBuilder<V3CalendarEventMappingContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),StringUtils.join(eventIds, ","),NumberOperators.EQUALS));
        deleteRecordBuilder.markAsDelete();
    }
    public static void populateCalendarView(Long calendarId,Long startDate, Long endDate) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("startDate",startDate);
        message.put("endDate",endDate);
        message.put(FacilioConstants.Calendar.CALENDAR_ID, calendarId);

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("calendar/" + calendarId)
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    public static List<V3CalendarTimeSlotContext> getTimeSlotOfEventAssociatedWithCalendar(Long calendarId, Long eventId) throws Exception{
        if(calendarId == null || eventId == null){
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarTimeSlotContext> selectRecordsBuilder = new SelectRecordsBuilder<V3CalendarTimeSlotContext>()
                .module(module)
                .select(fields)
                .beanClass(V3CalendarTimeSlotContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId),NumberOperators.EQUALS));
        return selectRecordsBuilder.get();

    }
    public static List<V3CalendarContext> getCalendarIdsAssociatedWithNonEditedEvent(Long eventId) throws Exception{
        if(eventId == null){
            throw new IllegalArgumentException("Event Id cannot be Null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarEventMappingContext> selectRecordBuilder = new SelectRecordsBuilder<V3CalendarEventMappingContext>()
                .module(module)
                .select(fields)
                .beanClass(V3CalendarEventMappingContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("isEventEdited"),String.valueOf(false), BooleanOperators.IS));
       List<V3CalendarEventMappingContext> calendarEventMappingContextList = selectRecordBuilder.get();
        if(CollectionUtils.isEmpty(calendarEventMappingContextList)){
            return null;
        }

        return calendarEventMappingContextList.stream().map(V3CalendarEventMappingContext::getCalendar).collect(Collectors.toList());
    }
    public static int dropCalendarSlotsOfCalendar(Long calendarId) throws Exception{
        if(calendarId == null){
            throw new IllegalArgumentException("Calendar Id cannot be Null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_SLOTS_MODULE_NAME);
        Long currentTime = System.currentTimeMillis();
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), DBConf.getInstance().getCurrentZoneId());
        ZonedDateTime startDate = now.plusDays(1);
        ZonedDateTime endDate = now.plusDays(100);

        List<V3CalendarSlotsContext> calendarSlotsContextList = getCalendarSlots(calendarId,startDate.toEpochSecond()*1000,endDate.toEpochSecond()*1000);
        if(CollectionUtils.isEmpty(calendarSlotsContextList)){
            return 0;
        }
        List<Long> calendarSlotIds = calendarSlotsContextList.stream().map(V3CalendarSlotsContext::getId).collect(Collectors.toList());
        DeleteRecordBuilder<V3CalendarSlotsContext> builder = new DeleteRecordBuilder<V3CalendarSlotsContext>()
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(calendarSlotIds,module));
       return builder.markAsDelete();
    }
    public static V3EventContext getFilteredEventFromEventList(List<V3EventContext> calendarEventContextList){
        List<V3EventContext> sortedEventsBasedOnType = calendarEventContextList.stream().
                sorted(Comparator.comparing(V3EventContext::getEventType)).collect(Collectors.toList());
        int min = sortedEventsBasedOnType.get(0).getEventType();
        List<V3EventContext> listOfEventsToBeFilteredByEventSequence = new ArrayList<>();
        for(V3EventContext calendarEventContext : sortedEventsBasedOnType){
            if(min != calendarEventContext.getEventType()){
                break;
            }
            listOfEventsToBeFilteredByEventSequence.add(calendarEventContext);
        }
        if(listOfEventsToBeFilteredByEventSequence.size() == 1){
            return listOfEventsToBeFilteredByEventSequence.get(0);
        }
        List<V3EventContext> sortedEventsBasedOnSequence = listOfEventsToBeFilteredByEventSequence.stream().
                sorted(Comparator.comparing(V3EventContext::getEventSequence)).collect(Collectors.toList());
        return sortedEventsBasedOnSequence.get(sortedEventsBasedOnSequence.size()-1);
    }
    public static V3CalendarEventMappingContext  getHighPriorityEvent(LocalDate localDate, V3CalendarContext calendarContext) throws Exception{
        if(calendarContext == null){
            throw new IllegalArgumentException("Calendar can't be null");
        }
        List<V3CalendarEventMappingContext> eventCalendarMappingForADay = getAvailableEventsForADay(localDate,calendarContext);
        if(CollectionUtils.isEmpty(eventCalendarMappingForADay)){
            return null;
        }
        List<V3EventContext> eventContextList = eventCalendarMappingForADay.stream().map(V3CalendarEventMappingContext::getEvent).collect(Collectors.toList());
        List<Long> eventId = eventContextList.stream().map(V3EventContext::getId).collect(Collectors.toList());
        Map<Long,V3CalendarEventMappingContext> eventIdVsCalendarEventMapping = IntStream.range(0, eventId.size()).boxed().collect(Collectors.toMap(eventId::get, eventCalendarMappingForADay::get));
        V3EventContext filteredEvent = CalendarApi.getFilteredEventFromEventList(eventContextList);
        return eventIdVsCalendarEventMapping.get(filteredEvent.getId());
    }
    public static List<V3CalendarEventMappingContext> getAvailableEventsForADay(LocalDate localDate, V3CalendarContext calendarContext) throws Exception{
        List<V3EventContext> v3CalendarEventContextList = new ArrayList<>();
        if(calendarContext == null){
            throw new IllegalArgumentException("calendar can't be Null");
        }
        List<V3CalendarEventMappingContext> calendarEventMappingContextList = calendarContext.getCalendarEventMappingContextList();
        List<V3EventContext> eventList = calendarEventMappingContextList.stream().map(V3CalendarEventMappingContext::getEvent).collect(Collectors.toList());
        List<Long> eventIdList = eventList.stream().map(V3EventContext::getId).collect(Collectors.toList());;
        Map<Long,V3CalendarEventMappingContext> eventIsVsEventMappingMap = IntStream.range(0,eventIdList.size()).boxed().collect(Collectors.toMap(eventIdList::get,calendarEventMappingContextList::get));
        List<V3CalendarEventMappingContext> eventMappingForADay = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(eventList)) {
            for (V3EventContext eventContext : eventList) {
                boolean check = eventContext.getEventTypeEnum().isMatch(eventContext,localDate);
                if (check) {
                    eventMappingForADay.add(eventIsVsEventMappingMap.get(eventContext.getId()));
                }
            }
        }
        return eventMappingForADay;
    }
    public static FacilioContext createCalendarSlots(V3CalendarContext calendarContext, V3CalendarEventMappingContext calendarEventMappingContext, LocalDate localDate) throws Exception{
        if(calendarContext == null){
            throw new IllegalArgumentException("Calendar can't be null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule calendarSlotsModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_SLOTS_MODULE_NAME);
        List<ModuleBaseWithCustomFields> records = new ArrayList<>();
        V3EventContext calendarEventContext = calendarEventMappingContext.getEvent();
        List<V3CalendarTimeSlotContext> timeSlotContexts = calendarEventMappingContext.getCalendarTimeSlotContextList();
        for(V3CalendarTimeSlotContext timeSlotContext : timeSlotContexts) {
            V3CalendarSlotsContext calendarSlot = new V3CalendarSlotsContext();
            calendarSlot.setCalendar(calendarContext);
            calendarSlot.setEvent(calendarEventContext);
            calendarSlot.setCalendarYear(localDate.getYear());
            calendarSlot.setCalendarMonth(localDate.getMonthValue());
            calendarSlot.setCalendarDate(localDate.getDayOfMonth());
            calendarSlot.setCalendarWeekNumber(localDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH));
            calendarSlot.setCalendarWeekDay(localDate.getDayOfWeek().getValue());
            calendarSlot.setSlotStartTime(timeSlotContext.getStartMin());
            calendarSlot.setSlotEndTime(timeSlotContext.getEndMin());
            records.add(calendarSlot);
        }
        if(CollectionUtils.isEmpty(records)){
            return null;
        }
        FacilioContext facilioContext = V3Util.createRecord(calendarSlotsModule,records);
        return facilioContext;
    }
    public static void eventSelectorMethod(Long calendarId,ZonedDateTime startDate, ZonedDateTime endDate) throws Exception{
        // events will not be created for the endDate
        V3CalendarContext calendarContext = V3RecordAPI.getRecord(FacilioConstants.Calendar.CALENDAR_MODULE_NAME,calendarId);
        List<V3CalendarEventMappingContext> calendarEventMappingContextList = CalendarApi.getEventCalendarMapping(calendarContext.getId());
        if(CollectionUtils.isEmpty(calendarEventMappingContextList)){
            return;
        }
        for(V3CalendarEventMappingContext v3CalendarEventMappingContext : calendarEventMappingContextList){
            v3CalendarEventMappingContext.setEvent(V3RecordAPI.getRecord(FacilioConstants.Calendar.EVENT_MODULE_NAME,v3CalendarEventMappingContext.getEvent().getId()));
            v3CalendarEventMappingContext.setCalendarTimeSlotContextList(CalendarApi.getTimeSlotOfEventAssociatedWithCalendar(calendarContext.getId(),v3CalendarEventMappingContext.getEvent().getId()));
        }
        calendarContext.setCalendarEventMappingContextList(calendarEventMappingContextList);
        ZonedDateTime zonedDate = startDate;
        //TODO - validity check for event
        while(zonedDate.isBefore(endDate))
        {
            LocalDate localDate = LocalDate.of(zonedDate.getYear(),zonedDate.getMonthValue(), zonedDate.getDayOfMonth());
            V3CalendarEventMappingContext calendarEventMappingContext = getHighPriorityEvent(localDate,calendarContext);
            if (calendarEventMappingContext == null){
                zonedDate = zonedDate.plusDays(1);
                continue;
            }
            createCalendarSlots(calendarContext,calendarEventMappingContext,localDate);
            zonedDate = zonedDate.plusDays(1);
        }
        return;
    }
    public static int deleteTimeSlotOfCalendarEvent(Long calendarId, Long eventId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<V3CalendarTimeSlotContext> builder = new DeleteRecordBuilder<V3CalendarTimeSlotContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId),NumberOperators.EQUALS));

        return builder.markAsDelete();
    }
    public static int deleteCalendarTimeSlot(Long calendarId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<V3CalendarTimeSlotContext> builder = new DeleteRecordBuilder<V3CalendarTimeSlotContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS));
       return builder.markAsDelete();
    }
    public static int deleteTimeSlotOfAnEvent(Long eventId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule eventTimeSlot = modBean.getModule(FacilioConstants.Calendar.EVENT_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(eventTimeSlot.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<V3CalendarTimeSlotContext> builder = new DeleteRecordBuilder<V3CalendarTimeSlotContext>()
                .module(eventTimeSlot)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId),NumberOperators.EQUALS));
       return builder.markAsDelete();
    }
    public static V3CalendarEventMappingContext getCalendarEventMapping(Long calendarId, Long eventId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarEventMappingContext> builder = new SelectRecordsBuilder<V3CalendarEventMappingContext>()
                .beanClass(V3CalendarEventMappingContext.class)
                .module(module)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId),NumberOperators.EQUALS));
        return builder.fetchFirst();
    }
    public static int updateCalendarMapping(V3CalendarEventMappingContext calendarEventMappingContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        UpdateRecordBuilder<V3CalendarEventMappingContext> builder = new UpdateRecordBuilder<V3CalendarEventMappingContext>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(calendarEventMappingContext.getId(),module));
       return builder.update(calendarEventMappingContext);
    }
    public static List<V3EventTimeSlotContext> getTimeSlotOfEvent(Long eventId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule eventTimeSlot = modBean.getModule(FacilioConstants.Calendar.EVENT_TIME_SLOT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(eventTimeSlot.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3EventTimeSlotContext> builder = new SelectRecordsBuilder<V3EventTimeSlotContext>()
                .beanClass(V3EventTimeSlotContext.class)
                .module(eventTimeSlot)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("event"),String.valueOf(eventId),NumberOperators.EQUALS));
        List<V3EventTimeSlotContext> eventTimeSlotContextList = builder.get();
        return eventTimeSlotContextList;
    }
    public static V3CalendarTimeSlotContext castV3EventTimeSlotContextToV3CalendarTimeSlotContext(V3EventTimeSlotContext eventTimeSlotContext,V3CalendarContext calendarContext,V3EventContext eventContext){
        V3CalendarTimeSlotContext v3CalendarTimeSlotContext = new V3CalendarTimeSlotContext();
        v3CalendarTimeSlotContext.setCalendar(calendarContext);
        v3CalendarTimeSlotContext.setEvent(eventContext);
        v3CalendarTimeSlotContext.setStartMin(eventTimeSlotContext.getStartMin());
        v3CalendarTimeSlotContext.setEndMin(eventTimeSlotContext.getEndMin());
        return v3CalendarTimeSlotContext;
    }
    public static V3EventTimeSlotContext castV3CalendarTimeSlotContextToV3EventTimeSlotContext(V3CalendarTimeSlotContext calendarTimeSlotContext, V3EventContext eventContext){
        V3EventTimeSlotContext eventTimeSlotContext = new V3EventTimeSlotContext();
        eventTimeSlotContext.setEvent(eventContext);
        eventTimeSlotContext.setStartMin(calendarTimeSlotContext.getStartMin());
        eventTimeSlotContext.setEndMin(calendarTimeSlotContext.getEndMin());
        return eventTimeSlotContext;
    }
    public static List<V3CalendarTimeSlotContext> createCalendarTimeSlotObjectList(List<V3CalendarTimeSlotContext> calendarTimeSlotContextList,V3CalendarContext calendarContext,V3EventContext eventContext){
        if(CollectionUtils.isEmpty(calendarTimeSlotContextList)){
            return null;
        }
        for(V3CalendarTimeSlotContext calendarTimeSlotContext : calendarTimeSlotContextList){
            calendarTimeSlotContext.setEvent(eventContext);
            calendarTimeSlotContext.setCalendar(calendarContext);
        }
        return calendarTimeSlotContextList;
    }
    public static boolean checkForDifference(Long eventId,List<V3CalendarTimeSlotContext> calendarTimeSlotContextList) throws Exception{
        List<V3EventTimeSlotContext> eventTimeSlotContextList = CalendarApi.getTimeSlotOfEvent(eventId);
        if(CollectionUtils.isEmpty(eventTimeSlotContextList) && CollectionUtils.isNotEmpty(calendarTimeSlotContextList)){
            return true;
        }
        if(CollectionUtils.isEmpty(calendarTimeSlotContextList) && CollectionUtils.isNotEmpty(eventTimeSlotContextList)){
            return true;
        }
        if(eventTimeSlotContextList.size() != calendarTimeSlotContextList.size()){
            return true;
        }
        int index = 0;
        while(index < eventTimeSlotContextList.size()){
            if(eventTimeSlotContextList.get(index).getStartMin() != calendarTimeSlotContextList.get(index).getStartMin()){
                return true;
            }
            if(eventTimeSlotContextList.get(index).getEndMin() != calendarTimeSlotContextList.get(index).getEndMin()){
                return true;
            }
            index += 1;
        }
        return false;
    }
    public static V3EventContext getEventName(Long eventId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule eventModule = modBean.getModule(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(eventModule.getName());

        SelectRecordsBuilder<V3EventContext> builder = new SelectRecordsBuilder<V3EventContext>()
                .module(eventModule)
                .select(fields)
                .beanClass(V3EventContext.class)
                .andCondition(CriteriaAPI.getIdCondition(eventId,eventModule));

       return builder.fetchFirst();
    }
    public static List<V3CalendarSlotsContext> getCalendarSlots(Long calendarId,Long startTime, Long endTime) throws Exception{
        ZonedDateTime startDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), DBConf.getInstance().getCurrentZoneId());
        ZonedDateTime endDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime),DBConf.getInstance().getCurrentZoneId());

        if(startDateTime.getYear() == endDateTime.getYear() && startDateTime.getMonthValue() == endDateTime.getMonthValue() && startDateTime.getDayOfMonth() >= endDateTime.getDayOfMonth()){
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule calendarSlots = modBean.getModule(FacilioConstants.Calendar.CALENDAR_SLOTS_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(calendarSlots.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarSlotsContext> builder = new SelectRecordsBuilder<V3CalendarSlotsContext>()
                .module(calendarSlots)
                .select(fields)
                .beanClass(V3CalendarSlotsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS));

        int startDate = startDateTime.getDayOfMonth();
        int startMonth = startDateTime.getMonthValue();
        int startYear = startDateTime.getYear();
        int endDate = endDateTime.getDayOfMonth();
        int endMonth = endDateTime.getMonthValue();
        int endYear = endDateTime.getYear();
        Criteria criteria = new Criteria();
        if(startYear == endYear && startMonth == endMonth && startDate < endDate){
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(startDate),NumberOperators.GREATER_THAN_EQUAL));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(endDate),NumberOperators.LESS_THAN_EQUAL));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarMonth"),String.valueOf(startMonth),NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarYear"),String.valueOf(startYear),NumberOperators.EQUALS));
        }
        else if(startYear == endYear && endMonth > startMonth){
            for(int month = startMonth; month <= endMonth; month += 1){
                if(month == startMonth){
                    criteria = getStartMonthCriteria(startDateTime,fieldMap);
                }
                if(month == endMonth){
                    criteria.orCriteria(getEndMonthCriteria(endDateTime,fieldMap));

                }
                if(month != startMonth && month != endMonth){
                    ZonedDateTime zonedDateTime = ZonedDateTime.of(startYear,month,1,0,0,0,0, DBConf.getInstance().getCurrentZoneId());
                    criteria.orCriteria(getMonthInBetweenRangeCriteria(zonedDateTime,fieldMap));
                }
            }
        }
        else if(endYear > startYear){
            for(int year = startYear; year <= endYear; year += 1){
                int monthStart = 1;
                if(year == startYear){
                    monthStart = startMonth;
                }
                int monthEnd = 12;
                if(year == endYear){
                    monthEnd = endMonth;
                }
                for(;monthStart <= monthEnd && monthStart <= 12; monthStart += 1){
                    if(monthStart == startMonth && year == startYear){
                         criteria = getStartMonthCriteria(startDateTime,fieldMap);
                    }
                    else if(monthStart == endMonth && year == endYear){
                         criteria.orCriteria(getEndMonthCriteria(endDateTime,fieldMap));
                    }
                    else{
                        ZonedDateTime zonedDateTime = ZonedDateTime.of(year,monthStart,1,0,0,0,0,DBConf.getInstance().getCurrentZoneId());
                        criteria.orCriteria(getMonthInBetweenRangeCriteria(zonedDateTime,fieldMap));
                    }
                }
            }
        }
        builder.andCriteria(criteria);
        return builder.get();
    }
    public static Criteria getStartMonthCriteria(ZonedDateTime date,Map<String, FacilioField> fieldMap){
        LocalDate localDate = date.toLocalDate();
        LocalDate monthEndlocalDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        int monthEnd = monthEndlocalDate.getDayOfMonth();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(localDate.getDayOfMonth()),NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(monthEnd),NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarMonth"), String.valueOf(localDate.getMonthValue()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarYear"),String.valueOf(localDate.getYear()),NumberOperators.EQUALS));
        return criteria;
    }
    public static Criteria getEndMonthCriteria(ZonedDateTime dateTime,Map<String, FacilioField> fieldMap){
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(1),NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(dateTime.getDayOfMonth()),NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarMonth"), String.valueOf(dateTime.getMonthValue()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarYear"),String.valueOf(dateTime.getYear()),NumberOperators.EQUALS));
        return criteria;
    }
    public static Criteria getMonthInBetweenRangeCriteria(ZonedDateTime zonedDateTime,Map<String,FacilioField> fieldMap){
        LocalDate localDate = zonedDateTime.toLocalDate();
        LocalDate monthEndlocalDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        int monthEnd = monthEndlocalDate.getDayOfMonth();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(1),NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarDate"),String.valueOf(monthEnd),NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarMonth"), String.valueOf(localDate.getMonthValue()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("calendarYear"),String.valueOf(localDate.getYear()),NumberOperators.EQUALS));
        return criteria;
    }
    public static List<V3CalendarContext> getAvailableCalendar() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        SelectRecordsBuilder<V3CalendarContext> builder = new SelectRecordsBuilder<V3CalendarContext>()
                .module(module)
                .select(fields)
                .beanClass(V3CalendarContext.class);
        return builder.get();
    }
    public static ZonedDateTime getGeneratedUptoOfCalendar(Long calendarId)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_SLOTS_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3CalendarSlotsContext> builder = new SelectRecordsBuilder<V3CalendarSlotsContext>()
                .module(module)
                .select(fields)
                .beanClass(V3CalendarSlotsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS))
                .orderBy("ID DESC")
                .limit(1);

        V3CalendarSlotsContext calendarSlotsContext = builder.fetchFirst();
        if(calendarSlotsContext == null){
            return null;
        }
        return ZonedDateTime.of(calendarSlotsContext.getCalendarYear(),calendarSlotsContext.getCalendarMonth(),calendarSlotsContext.getCalendarDate(),0,0,0,0,DBConf.getInstance().getCurrentZoneId());

    }
    public static String convertMinuteToHourAndMins(Integer slotTime){
        String hr = java.lang.String.valueOf(slotTime/60);
        String min = java.lang.String.valueOf(slotTime%60);
        if(hr.length() == 1){
            hr = "0"+hr;
        }
        if(min.length() == 1){
            min = "0"+min;
        }
        java.lang.String suffix = "am";
        if(Integer.parseInt(hr) > 11){
            if(Integer.parseInt(hr) == 12){
                hr = "12";
            }
            else {
                hr = java.lang.String.valueOf(Integer.parseInt(hr) - 12);
            }
            if(hr.length() == 1){
                hr = "0"+hr;
            }

            suffix = "pm";
        }
        if(Integer.parseInt(hr) == 0){
            hr = "12";
        }
        String time = hr+":"+min+""+suffix;
        return time;
    }
    public static List<V3ControlActionTemplateContext> getAssociatedControlActionTemplateList(Long calendarId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3ControlActionTemplateContext> controlActionTemplateContextSelectRecordsBuilder = new SelectRecordsBuilder<V3ControlActionTemplateContext>()
                .module(module)
                .select(fields)
                .beanClass(V3ControlActionTemplateContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("calendar"),String.valueOf(calendarId),NumberOperators.EQUALS));
        return controlActionTemplateContextSelectRecordsBuilder.get();
    }

}

