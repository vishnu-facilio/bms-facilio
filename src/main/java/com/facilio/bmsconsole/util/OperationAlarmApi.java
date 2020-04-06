package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.chain.Context;

import java.sql.Time;
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
        LOGGER.info("resourceWithOperatingHour :: " + fieldIdVsResource.size() + fieldIdVsResource );

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


    private static void raiseAlarm(String msg, ReadingContext readingContext, OperationAlarmContext.CoverageType type, Context context) throws Exception
    {
        OperationAlarmEventContext event = createOperationEvent(msg, readingContext, type);
        addEvent(event, context);
    }


    private static Map<Long, List<ReadingContext>> fetchReadings(FacilioField field, List<Long> resourceId, long startTime, long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> fields = modBean.getAllFields(field.getModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField parentField = fieldMap.get("parentId");
        FacilioField ttimeField = fieldMap.get("ttime");
        FacilioModule readingModule = modBean.getModule(field.getModule().getName());

        SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                .module(readingModule)
                .select(fields)
                .beanClass(ReadingContext.class)
                .andCondition(CriteriaAPI.getCondition(parentField, resourceId, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
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
        Boolean value = (Boolean) reading.getData().get(readingField.getName());
        BusinessHoursContext.BusinessHourType type = BusinessHoursContext.BusinessHourType.valueOf(businessHoursContext.getBusinessHourTypeId());
        OperationAlarmEventContext event;
        reading.setSiteId((long) resource.get("siteId"));
        switch (type) {
            case DAYS_24_7:
                if (!value) {
                    raiseAlarm("Expected to run 24/ 7 ", reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
                    break;
                }
                checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
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
                        raiseAlarm("Expected to run 24/5 short", reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
                        break;
                    }
                    checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
                }
                else
                {
                    if(!hoursList.contains(reading.getDay())) {
                        raiseAlarm("Expected to run 24/5 exceed", reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                        break;
                    }
                    checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                }
                break;
            case CUSTOM:
                List<BusinessHourContext> hoursNew = businessHoursContext.getSingleDaybusinessHoursList();
                Map<Integer,BusinessHourContext> dayWithTime = new HashMap<Integer, BusinessHourContext>();
                for(BusinessHourContext hour: hoursNew)
                {
                    dayWithTime.put(hour.getDayOfWeek(), hour );
                }
                Date date= new java.util.Date(reading.getTtime());
            	int readingMinutes=date.getMinutes();
                if(!value)
                {
                    if(dayWithTime.containsKey(reading.getDay()))
                    {
                        BusinessHourContext businessHour = dayWithTime.get(reading.getDay());
						if ((businessHour.getStartTimeAsLocalTime().getHour() < reading.getHour() && businessHour.getStartTimeAsLocalTime().getMinute() < readingMinutes) && (businessHour.getEndTimeAsLocalTime().getHour() > reading.getHour() && businessHour.getEndTimeAsLocalTime().getMinute() > readingMinutes)) {
                            raiseAlarm("Custom short", reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
                            break;
                        }
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE, context);
                    }
                }
                else
                {
                    if(dayWithTime.containsKey(reading.getDay()))
                    {
                        BusinessHourContext businessHour = dayWithTime.get(reading.getDay());
                        if (businessHour != null) {
                            if ((businessHour.getStartTimeAsLocalTime().getHour() > reading.getHour() && businessHour.getStartTimeAsLocalTime().getMinute() > readingMinutes) || (businessHour.getEndTimeAsLocalTime().getHour() < reading.getHour() && businessHour.getEndTimeAsLocalTime().getMinute() < readingMinutes)) {
                                raiseAlarm("Custom exceed", reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                                break;
                            }
                        } else {
                            raiseAlarm("Custom exceed", reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                            break;
                        }
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                    }
                    else
                    {
                    	if(dayWithTime.get(reading.getDay()) == null) {
	                    	raiseAlarm("Custom exceed", reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
	                        break;
                    	}
                        checkAndClearEvent(reading, OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE, context);
                    }

                }
                break;
        }

    }

    private static OperationAlarmEventContext createOperationEvent(String message, ReadingContext readingContext, OperationAlarmContext.CoverageType type) {
        return createOperationEvent(message, readingContext, type, FacilioConstants.Alarm.CRITICAL_SEVERITY);
    }
    private static OperationAlarmEventContext createOperationEvent(String message, ReadingContext readingContext, OperationAlarmContext.CoverageType type,String severity)
    {
        OperationAlarmEventContext event=new OperationAlarmEventContext();
        event.setMessage(message);
        event.setSeverityString(severity);
        event.setDescription("description");
        event.setCreatedTime(readingContext.getTtime());
        event.setSiteId(readingContext.getSiteId());
        ResourceContext context = new ResourceContext();
        context.setId(readingContext.getParentId());
        event.setResource(context);;
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
    private static void checkAndClearEvent(ReadingContext reading, OperationAlarmContext.CoverageType coverageType, Context context) throws Exception {
        BaseEventContext previousExceedEvent = (BaseEventContext) context.get(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT);
        BaseEventContext previousShortEvent = (BaseEventContext) context.get(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT);
            if (previousExceedEvent.getSeverityString() == null && previousShortEvent.getSeverityString() == null) {
                return;
            }
             if (previousExceedEvent.getSeverityString() != null && !previousExceedEvent.getSeverityString().equals("Clear")) {
                 addEvent(createOperationEvent("Clear ", reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY), context);
                 return;
             }

             if (previousShortEvent.getSeverityString() != null
                     && !previousShortEvent.getSeverityString().equals("Clear")) {
                 addEvent(createOperationEvent("Clear ", reading, coverageType, FacilioConstants.Alarm.CLEAR_SEVERITY), context);
                 return;
             }
    }

    private static void addEvent(OperationAlarmEventContext event, Context context) throws Exception {
        BaseEventContext baseEvent = (BaseEventContext) event;
        List<BaseEventContext> operationAlarmEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        if (event.getCoverageType() == OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE.getIndex()) {
            context.put(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT, baseEvent);
        } else  {
            context.put(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT, baseEvent);
        }
        if (operationAlarmEvents == null) {
            operationAlarmEvents = new ArrayList<>();
        }
        operationAlarmEvents.add(baseEvent);
        context.put(EventConstants.EventContextNames.EVENT_LIST, operationAlarmEvents);
        // operationAlarmEvents.add(baseEvent);
    }

    private static void addEventToDB(Context context) throws Exception {
        List<BaseEventContext> eventList =  (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        if (eventList != null) {
            // context.put(EventConstants.EventContextNames.EVENT_LIST, operationAlarmEvents);
           //  Boolean isHistorical = context.get(FacilioConstants.ContextNames.IS_HISTORICAL, false);
            FacilioChain chain = TransactionChainFactory.getV2AddEventChain(true);
            chain.execute(context);
        }

    }
    private static OperationAlarmEventContext setPreviousEventMeta(Long parentId, Context context, long startTime) throws Exception {
        OperationAlarmEventContext previousEvent = null;
        Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
        BaseAlarmContext exceedbaseAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_" + OperationAlarmContext.CoverageType.EXCEEDED_SCHEDULE + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
        AlarmOccurrenceContext exceedlastestOccurrence ;
        AlarmOccurrenceContext shortOflastestOccurrence ;
        BaseEventContext exceedPreviousEvent = new BaseEventContext();
        BaseEventContext shortOfPreviousEvent = new BaseEventContext();
        if (exceedbaseAlarm != null) {
            if (!isHistorical) {
                exceedlastestOccurrence = NewAlarmAPI.getLatestAlarmOccurance(exceedbaseAlarm.getId());

            } else {
                exceedlastestOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(exceedbaseAlarm, startTime);
            }
            exceedPreviousEvent.setSeverity(exceedlastestOccurrence.getSeverity());

        }
        BaseAlarmContext shortOfAlarm = NewAlarmAPI.getBaseAlarmByMessageKey(parentId + "_" + OperationAlarmContext.CoverageType.SHORT_OF_SCHEDULE + "_" +  BaseAlarmContext.Type.OPERATION_ALARM);
        if (shortOfAlarm != null) {
            if (!isHistorical) {
                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurance(shortOfAlarm.getId());

            } else {
                shortOflastestOccurrence = NewAlarmAPI.getLatestAlarmOccurrenceWithInTime(shortOfAlarm, startTime);
            }
            shortOfPreviousEvent.setSeverity(shortOflastestOccurrence.getSeverity());
        }
        context.put(FacilioConstants.ContextNames.PREVIOUS_SHORT_OF_EVENT, shortOfPreviousEvent);
        context.put(FacilioConstants.ContextNames.PREVIOUS_EXCEED_EVENT, exceedPreviousEvent);
        return previousEvent;
    }

}
