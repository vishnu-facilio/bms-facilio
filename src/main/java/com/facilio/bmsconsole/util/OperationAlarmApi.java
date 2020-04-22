package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.chain.Context;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.modules.FieldFactory.getSiteIdField;

public class OperationAlarmApi {
    private static final Logger LOGGER = LogManager.getLogger(OperationAlarmApi.class.getName());
    public static  void processOutOfCoverage(long startTime, long endTime) throws Exception {
        processOutOfCoverage(startTime, endTime, null, null);
    }

    public static  void processOutOfCoverage(long startTime, long endTime, List<Long> resourceList, Context context) throws Exception {
        // isHistorical = context.getOrDefault();
        FacilioModule module = ModuleFactory.getFieldsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("fieldId", "FIELDID", FieldType.NUMBER));
        GenericSelectRecordBuilder fieldsBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition("NAME", "name",
                        "runStatus", StringOperators.IS))
                .orCondition(CriteriaAPI.getCondition("NAME", "name",
                        "pumpRunStatus", StringOperators.IS));
        List<Map<String, Object>> fieldIds = fieldsBuilder.get();
        List<Long> fieldIdsList = fieldIds.stream().map(prop -> (long) prop.get("fieldId")).collect(Collectors.toList());

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
        List<FacilioField> selectField = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
        selectField.addAll(FieldFactory.getReadingDataMetaFields());
        selectField.add(getSiteIdField(resourceModule));
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(resourceModule.getTableName())
                .select(selectField)
                .innerJoin(ModuleFactory.getReadingDataMetaModule().getTableName())
                .on(resourceModule.getTableName()+".ID = "+ readingDataMeta.getTableName()+".RESOURCE_ID")
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", "false", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", String.valueOf("2"), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("BUSINESS_HOURS_ID","operatingHour",  "0", NumberOperators.GREATER_THAN))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), fieldIdsList, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS));

        if (resourceList != null) {
        	System.out.println(resourceList);
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceList, NumberOperators.EQUALS));
        }
//        if (assetCategory != null) {
//            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), Collections.singletonList(assetCategory), NumberOperators.EQUALS));
//        }
        List<Map<String, Object>> resourceWithOperatingHour = selectBuilder.get();
       

        List<Long> operatingHourId = resourceWithOperatingHour.stream().map(prop -> (long) prop.get("operatingHour")).collect(Collectors.toList());
        LOGGER.info("operatingHourId :: " + operatingHourId );
        List<BusinessHoursContext> bus = BusinessHoursAPI.getBusinessHours(operatingHourId);
        LOGGER.info("BusinessHoursContext :: " + bus );
        Map<Long,List<Map<String,Object>>> fieldIdVsResource= new HashMap<Long,List<Map<String,Object>>>();

        for(Map<String,Object> resource :resourceWithOperatingHour) {
            Long fieldId =(Long) resource.remove("fieldId");
            List<Map<String,Object>> resourceData= fieldIdVsResource.get(fieldId);
            if(resourceData == null) {
                resourceData= new ArrayList<Map<String,Object>>();
                fieldIdVsResource.put(fieldId, resourceData);
            }
            resourceData.add(resource);
        }
        LOGGER.info("resourceWithOperatingHour :: " + fieldIdVsResource.size() );

        for (long fieldId : fieldIdsList) {
            if (fieldIdVsResource.containsKey(fieldId)) {
                List<Map<String,Object>> resourceId = fieldIdVsResource.get(fieldId);
                List<Long> assetList = resourceId.stream().map(prop -> (long) prop.get("resourceId")).collect(Collectors.toList());
                FacilioField field = modBean.getField(fieldId);
                Map<Long, List<ReadingContext>> readingMap = fetchReadings(field, assetList,startTime, endTime);
                for (Long parentID : readingMap.keySet()) {
                    setPreviousEventMeta(parentID, context, startTime);
                    executeOutOfSchedule(bus, readingMap.get(parentID), resourceId, field , context);
                }

            }
        }
        addEventToDB(context);
//        if (operationAlarmEvents != null && operationAlarmEvents.size() > 0 )
//        {

       // }
    }


    private static void raiseAlarm(String msg, ReadingContext readingContext, OperationAlarmContext.CoverageType type, Context context, FacilioField readingField) throws Exception
    {
        // checkAndClearEvent(readingContext, type == OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE ? OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE : OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE , context, readingField);
        OperationAlarmEventContext event = createOperationEvent(msg, readingContext, type, readingField);
        addEvent(event, context);
    }


    private static Map<Long, List<ReadingContext>> fetchReadings(FacilioField field, List<Long> resourceId, long startTime, long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> fields = modBean.getAllFields(field.getModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField parentField = fieldMap.get("parentId");
        FacilioField ttimeField = fieldMap.get("ttime");
        List<FacilioField> selectfields = new ArrayList<>();
        selectfields.add(parentField);
        selectfields.add(ttimeField);
        selectfields.add(field);
        FacilioModule readingModule = modBean.getModule(field.getModule().getName());

        SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                .module(readingModule)
                .select(selectfields)
                .beanClass(ReadingContext.class)
                .andCondition(CriteriaAPI.getCondition(parentField, resourceId, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(field, "-1", NumberOperators.NOT_EQUALS))
                .orderBy("TTIME")
                ;
        List<ReadingContext> readingList = selectBuilder.get();
        readingList.stream().collect(
                Collectors.groupingBy(ReadingContext::getParentId, HashMap::new, Collectors.toCollection(ArrayList::new))
        );
        Map<Long, List<ReadingContext>> resourceReadingList = readingList.stream().collect(
                Collectors.groupingBy(ReadingContext::getParentId, HashMap::new, Collectors.toCollection(ArrayList::new))
        );
        return resourceReadingList;
    }

    private static void executeOutOfSchedule(List<BusinessHoursContext> resourceBusinessHours, List<ReadingContext> readings, List<Map<String,Object>> resourceList, FacilioField readingField,Context context) throws Exception {

        if (readings != null && !readings.isEmpty()) {
            for (ReadingContext reading: readings) {
                long resourceId = reading.getParentId();
                Map<String,Object> resourcemap = resourceList.stream().filter(i -> i.get("resourceId").equals(resourceId)).collect(Collectors.toList()).get(0);
                Long operatingId = (Long) resourcemap.get("operatingHour");
                BusinessHoursContext businessHours = resourceBusinessHours.stream().filter(i -> i.getId() == operatingId).collect(Collectors.toList()).get(0);
                evaluateOutOfSchedule(businessHours, reading, readingField, context, resourcemap);
            }

        }

    }

    private static void evaluateOutOfSchedule(BusinessHoursContext businessHoursContext, ReadingContext reading, FacilioField readingField, Context context,Map<String,Object> resource) throws Exception {
        if (reading == null || readingField == null) {
            LOGGER.info("Reading => " + reading);
            LOGGER.info("Reading Fields => " + readingField);
            return;
        }
        if (reading.getData() == null) {
            LOGGER.info("Reading => " + readingField.getName());
            return;
        }
        Boolean value = (Boolean) reading.getData().get(readingField.getName());
        BusinessHoursContext.BusinessHourType type = BusinessHoursContext.BusinessHourType.valueOf(businessHoursContext.getBusinessHourTypeId());
        OperationAlarmEventContext event;
        reading.setSiteId((long) resource.get("siteId"));
        switch (type) {
            case DAYS_24_7:
                if (!value) {
                    raiseAlarm(resource.get("name").toString() , reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField);
                    break;
                }
                checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField, resource);
                break;
            case DAYS_24_5:
                List<BusinessHourContext> hours = businessHoursContext.getSingleDaybusinessHoursList();
                List<Integer> hoursList = new ArrayList<>();
                for(BusinessHourContext hour:hours)
                {
                    hoursList.add(hour.getDayOfWeek());
                }
                if(!value) {  //  when result is false and checking whether it should be true
                    if(hoursList.contains(reading.getDay())) {
                        raiseAlarm( resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField);
                        break;
                    }
                    checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField, resource);
                }
                else
                {
                    if(!hoursList.contains(reading.getDay())) {
                        raiseAlarm(resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField);
                        break;
                    }
                    checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField, resource);
                }
                break;
            case CUSTOM:

                List<BusinessHourContext> hoursNew = businessHoursContext.getSingleDaybusinessHoursList();
                Map<Integer,BusinessHourContext> dayWithTime = new HashMap<Integer, BusinessHourContext>();
                for(BusinessHourContext hour: hoursNew)
                {
                    dayWithTime.put(hour.getDayOfWeek(), hour );
                }
                ZonedDateTime date =  DateTimeUtil.getDateTime(reading.getTtime());
                BusinessHourContext businessHour = dayWithTime.get(reading.getDay());
                LocalTime startTime = null;
                LocalTime endTime = null;
                if (businessHour != null) {
                    startTime = businessHour.getStartTimeAsLocalTime();
                    endTime = businessHour.getEndTimeAsLocalTime();
                }
                if(!value)
                {
                    if(businessHour == null) {
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField, resource);
                        break;
                    }
                    else if (businessHour != null ){
//
                        if (date.getHour() >= startTime.getHour() && date.getHour() <= endTime.getHour()) {
                            raiseAlarm(resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField);
                            break;
                        }
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context, readingField, resource);
                    }
                }
                else
                {
                    if (businessHour == null ) {
                        raiseAlarm(resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField);
                        break;
                    } else if (businessHour != null) {
                        if (date.getHour() < startTime.getHour() && date.getHour() >= endTime.getHour()) {
                            raiseAlarm(resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField);
                            break;
                        }
                        else if (date.getHour() >= endTime.getHour() || date.getHour() < startTime.getHour()) {
                            raiseAlarm(resource.get("name").toString(), reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField);
                            break;
                        }
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField, resource);
                    }
                    checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context, readingField, resource);
                }
                break;
        }

    }

    private static OperationAlarmEventContext createOperationEvent(String message, ReadingContext readingContext, OperationAlarmContext.CoverageType type, FacilioField field) {
        return createOperationEvent(message, readingContext, type, FacilioConstants.Alarm.CRITICAL_SEVERITY,field );
    }
    private static OperationAlarmEventContext createOperationEvent(String message, ReadingContext readingContext, OperationAlarmContext.CoverageType type,String severity, FacilioField readingField)
    {
        OperationAlarmEventContext event=new OperationAlarmEventContext();
        event.setMessage(message + " is not operating as per the operation schedule");
        event.setSeverityString(severity);
        // event.setDescription("description");
        event.setCreatedTime(readingContext.getTtime());
        event.setSiteId(readingContext.getSiteId());
        event.setReadingFieldId(readingField.getFieldId());
        ResourceContext context = new ResourceContext();
        context.setId(readingContext.getParentId());
        event.setResource(context);
        event.setCoverageType(type);
        return event;

    }

    private static OperationAlarmEventContext constructClearOperationalEvent(AlarmOccurrenceContext occurrenceContext) throws Exception {
        OperationAlarmEventContext event = new OperationAlarmEventContext();
        event.setResource(occurrenceContext.getResource());
        event.setEventMessage("Cleared");
        event.setComment("Alarm Cleared ");
        event.setCreatedTime(occurrenceContext.getCreatedTime());
        event.setAutoClear(true);
        event.setSiteId(occurrenceContext.getSiteId());
        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
        return event;
    }
//    private static void checkAndClearEvent(ReadingContext reading, OperationAlarmContext.CoverageType coverageType, Context context, FacilioField readingField, Map<String,Object> resource) throws Exception {
//
//        BaseEventContext previousExceedEvent = (BaseEventContext) context.get(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT);
//        BaseEventContext previousShortEvent = (BaseEventContext) context.get(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT);
//            if (previousExceedEvent.getSeverityString() == null && previousShortEvent.getSeverityString() == null) {
//                return;
//            }
//            if (coverageType == OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE) {
//                if (previousExceedEvent.getSeverityString() != null && !previousExceedEvent.getSeverityString().equals("Clear")) {
//                    addEvent(createOperationEvent(resource.get("name") + " asset is operating out of scheduled hours\n ", reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
//                }
//                if (previousShortEvent.getSeverityString() != null && !previousShortEvent.getSeverityString().equals("Clear")) {
//                    addEvent(createOperationEvent(resource.get("name") + " asset is OFF during scheduled operating hours ", reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
//                }
//                return;
//            }
//
//        if (coverageType == OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE) {
//            if (previousShortEvent.getSeverityString() != null && !previousShortEvent.getSeverityString().equals("Clear")) {
//                addEvent(createOperationEvent(resource.get("name") + " asset is OFF during scheduled operating hours ", reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
//            }
//            if (previousExceedEvent.getSeverityString() != null && !previousExceedEvent.getSeverityString().equals("Clear")) {
//                addEvent(createOperationEvent(resource.get("name") + " asset is operating out of scheduled hours\n ", reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
//            }
//            return;
//        }
//    }
    private static void checkAndClearEvent(ReadingContext reading, OperationAlarmContext.CoverageType coverageType, Context context, FacilioField readingField, Map<String,Object> resource) throws Exception {
        BaseEventContext previousEvent = (BaseEventContext) context.get(FacilioConstants.ContextNames.PREVIOUS_EVENT);
        if (previousEvent.getSeverityString() == null) {
            return;
        }
        if (coverageType == OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE) {
            if (previousEvent.getSeverityString() != null && !previousEvent.getSeverityString().equals("Clear")) {
                addEvent(createOperationEvent(resource.get("name").toString(), reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
            }
            return;
        }

        if (coverageType == OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE) {
            if (previousEvent.getSeverityString() != null && !previousEvent.getSeverityString().equals("Clear")) {
                addEvent(createOperationEvent(resource.get("name").toString(), reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY, readingField), context);
            }
            return;
        }
    }
    private static void addEvent(OperationAlarmEventContext event, Context context) throws Exception {
        OperationAlarmEventContext baseEvent = (OperationAlarmEventContext) event;
        List<BaseEventContext> operationAlarmEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        if (event.getCoverageType() == OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE.getIndex()) {
            baseEvent.setCoverageType(OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE);
            context.put(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT, baseEvent);
        } else  {
            baseEvent.setCoverageType(OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE);
            context.put(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT, baseEvent);
        }
        if (operationAlarmEvents == null) {
            operationAlarmEvents = new ArrayList<>();
        }
        context.put(FacilioConstants.ContextNames.PREVIOUS_EVENT, baseEvent);
        // LOGGER.info("CoverageType" + event.getCoverageType());
        operationAlarmEvents.add(baseEvent);
        context.put(EventConstants.EventContextNames.EVENT_LIST, operationAlarmEvents);
        // operationAlarmEvents.add(baseEvent);
    }

    private static void addEventToDB(Context context) throws Exception {
        List<BaseEventContext> eventList =  (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        if (eventList != null) {
            Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
            if (isHistorical) {
                insertEventsWithoutAlarmOccurrenceProcessed(context);
            } else {
                FacilioChain chain = TransactionChainFactory.getV2AddEventChain(true);
                context.put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, false);
                context.put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
                chain.execute(context);
            }
        }

    }
    private static OperationAlarmEventContext setPreviousEventMeta(Long parentId, Context context, long startTime) throws Exception {
        OperationAlarmEventContext previousEvent = null;
        Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
        BaseAlarmContext opBaseAlarm = NewAlarmAPI.getBaseAlarmByMessageKey( parentId + "_" + BaseAlarmContext.Type.OPERATION_ALARM);
//        BaseAlarmContext exceedbaseAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_"  + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
//        AlarmOccurrenceContext exceedlastestOccurrence ;
//        AlarmOccurrenceContext shortOflastestOccurrence ;
        AlarmOccurrenceContext opAlarmOccurrence ;
        BaseEventContext opEvent = new BaseEventContext();
//        BaseEventContext shortOfPreviousEvent = new BaseEventContext();
        if (opBaseAlarm != null) {
            if (!isHistorical) {
                opAlarmOccurrence = NewAlarmAPI.getLatestAlarmOccurance(opBaseAlarm.getId());

            } else {
                opAlarmOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(opBaseAlarm, startTime);
            }
            opEvent.setSeverity(opAlarmOccurrence.getSeverity());

        }
//        BaseAlarmContext shortOfAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_" + OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
//        if (shortOfAlarm != null) {
//            if (!isHistorical) {
//                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurance(shortOfAlarm.getId());
//
//            } else {
//                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(shortOfAlarm, startTime);
//            }
//            shortOfPreviousEvent.setSeverity(shortOflastestOccurrence.getSeverity());
//        }
//        context.put(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT, shortOfPreviousEvent);
        context.put(FacilioConstants.ContextNames.PREVIOUS_EVENT, opEvent);
        return previousEvent;
    }

//    private static OperationAlarmEventContext setPreviousEventMeta(Long parentId, Context context, long startTime) throws Exception {
//        OperationAlarmEventContext previousEvent = null;
//        Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
//        BaseAlarmContext exceedbaseAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_" + OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
//        AlarmOccurrenceContext exceedlastestOccurrence ;
//        AlarmOccurrenceContext shortOflastestOccurrence ;
//        BaseEventContext exceedPreviousEvent = new BaseEventContext();
//        BaseEventContext shortOfPreviousEvent = new BaseEventContext();
//        if (exceedbaseAlarm != null) {
//            if (!isHistorical) {
//                exceedlastestOccurrence = NewAlarmAPI.getLatestAlarmOccurance(exceedbaseAlarm.getId());
//
//            } else {
//                exceedlastestOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(exceedbaseAlarm, startTime);
//            }
//            exceedPreviousEvent.setSeverity(exceedlastestOccurrence.getSeverity());
//
//        }
//        BaseAlarmContext shortOfAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_" + OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
//        if (shortOfAlarm != null) {
//            if (!isHistorical) {
//                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurance(shortOfAlarm.getId());
//
//            } else {
//                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(shortOfAlarm, startTime);
//            }
//            shortOfPreviousEvent.setSeverity(shortOflastestOccurrence.getSeverity());
//        }
//        context.put(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT, shortOfPreviousEvent);
//        context.put(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT, exceedPreviousEvent);
//        return previousEvent;
//    }
    private static void insertEventsWithoutAlarmOccurrenceProcessed(Context context) throws Exception
    {
        List<BaseEventContext> events = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        List<OperationAlarmEventContext> opEvents = new ArrayList<OperationAlarmEventContext>();
        for(BaseEventContext event:events)
        {
            if (event instanceof  OperationAlarmEventContext) {
                OperationAlarmEventContext operationEvent = (OperationAlarmEventContext) event;
                operationEvent.setCoverageType(operationEvent.getCoverageType());
                operationEvent.setSeverity(AlarmAPI.getAlarmSeverity(operationEvent.getSeverityString()));
                operationEvent.setMessageKey(operationEvent.constructMessageKey());
                operationEvent.setAlarmOccurrence(null);
                operationEvent.setBaseAlarm(null);
                opEvents.add(operationEvent);
            }
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (!opEvents.isEmpty()) {
            String moduleName = NewEventAPI.getEventModuleName(BaseAlarmContext.Type.OPERATION_ALARM);
            InsertRecordBuilder<OperationAlarmEventContext> builder = new InsertRecordBuilder<OperationAlarmEventContext>()
                    .moduleName(moduleName)
                    .fields(modBean.getAllFields(moduleName));
            builder.addRecords(opEvents);
            builder.save();
        }
    }

    public static void updateOperationAlarmHistoricalLogger(OperationAlarmHistoricalLogsContext operationAlarmHistoricalLogsContext) throws Exception {

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
                .fields(FieldFactory.getOperationAlarmHistoricalLogFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", ""+operationAlarmHistoricalLogsContext.getId(), NumberOperators.EQUALS));

        Map<String, Object> props = FieldUtil.getAsProperties(operationAlarmHistoricalLogsContext);
        updateBuilder.update(props);
    }

    public static OperationAlarmHistoricalLogsContext getOperationAlarmHistoricalLoggerById (long loggerRuleId) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getOperationAlarmHistoricalLogFields())
                .table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerRuleId, NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            OperationAlarmHistoricalLogsContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), OperationAlarmHistoricalLogsContext.class);
            return workflowRuleHistoricalLogger;
        }
        return null;
    }

//    public static List<OperationAlarmHistoricalLogsContext> getOperationAlarmHistoricalLoggerByParentId (long loggerRuleId) throws Exception {
//
//        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//                .select(FieldFactory.getOperationAlarmHistoricalLogFields())
//                .table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
//                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", ""+loggerRuleId, NumberOperators.EQUALS));
//
//        List<Map<String, Object>> props = selectBuilder.get();
//        if (props != null && !props.isEmpty()) {
//            OperationAlarmHistoricalLogsContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), OperationAlarmHistoricalLogsContext.class);
//            return workflowRuleHistoricalLogger;
//        }
//        return null;
//    }
}
