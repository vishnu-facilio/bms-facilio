package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AlarmActivityType;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.wms.constants.WmsEventType;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveAlarmAndEventsCommand extends FacilioCommand implements PostTransactionCommand {

    private static final Logger LOGGER = LogManager.getLogger(SaveAlarmAndEventsCommand.class.getName());

    private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap;
    List<EventType> eventTypes;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        eventTypes = CommonCommandUtil.getEventTypes(context);

        List<BaseEventContext> eventList = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
        Map<String, BaseAlarmContext> alarmMap = (Map<String, BaseAlarmContext>) context.get("alarmMap");
        alarmOccurrenceMap = (Map<String, PointedList<AlarmOccurrenceContext>>) context.get("alarmOccurrenceMap");


        Map<Long, List<UpdateChangeSet>> changes = new HashMap<>();

        Map<String, BaseEventContext> keyVsEvents = eventList.stream()
                .filter(e -> e.getBaseAlarm() != null)
                .collect(Collectors.toMap(e -> e.getBaseAlarm().getKey(), Function.identity(), (prevEvent, event) -> {
                    if (prevEvent.getCreatedTime() > event.getCreatedTime()) {
                        return prevEvent;
                    }
                    return event;
                }));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (MapUtils.isNotEmpty(alarmMap)) {
            for (BaseAlarmContext baseAlarm : alarmMap.values()) {
                BaseEventContext alarmEvent = keyVsEvents.get(baseAlarm.getKey());
                if (alarmEvent != null) {
                    baseAlarm.setData(alarmEvent.getCustomFields());
                }
                if (baseAlarm.getId() > 0) {
                    UpdateRecordBuilder<BaseAlarmContext> builder = new UpdateRecordBuilder<BaseAlarmContext>()
							.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
							.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))))
							.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
                    if (baseAlarm instanceof AgentAlarmContext) {
                        builder.updateSupplement(getAgentAlarmToControllerMultiLookupField());
                    }
                    builder.withChangeSet(V3RecordAPI.getRecordsList(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()),Collections.singletonList(baseAlarm.getId())));
                    builder.update(baseAlarm);
                    Map<Long, List<UpdateChangeSet>> recordChanges = builder.getChangeSet();
                    changes.put(baseAlarm.getId(), recordChanges.get(baseAlarm.getId()));

                } else {
                    InsertRecordBuilder<BaseAlarmContext> builder = new InsertRecordBuilder<BaseAlarmContext>()
							.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
							.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
                    if (baseAlarm instanceof AgentAlarmContext) {
                        builder.insertSupplement(getAgentAlarmToControllerMultiLookupField());
                    }
                    LOGGER.debug("Alarm Value: " + FieldUtil.getAsProperties(baseAlarm));
                    // LOGGER.log(Level.INFO, "Alarm Value: " + FieldUtil.getAsProperties(baseAlarm));
                    if (!FacilioProperties.isProduction()) {
                        LOGGER.info("Alarm Value: " + FieldUtil.getAsProperties(baseAlarm));
                    }
                    builder.insert(baseAlarm);
                }
            }
        }

        if (MapUtils.isNotEmpty(alarmOccurrenceMap)) {

            Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT); //For Historical Reading rule
            isHistorical = (isHistorical == null) ? false : isHistorical;
			if(isHistorical)
			{
                HashMap<String, AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = new HashMap<String, AlarmOccurrenceContext>();
                for (String key : alarmOccurrenceMap.keySet()) {
                    PointedList<AlarmOccurrenceContext> alarmOccurrenceList = alarmOccurrenceMap.get(key);
                    if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty()) {
                        if (alarmOccurrenceList.size() > 1 && alarmOccurrenceList.get(alarmOccurrenceList.size() - 1).getId() < 0) { //if the lastElement is the latestAlarmoccurrence as it is not inserted yet, and so lastElement is the batch's lastOne
                            AlarmOccurrenceContext lastOccurrenceOfPreviousBatch = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
                            lastOccurrenceOfPreviousBatchMap.put(key, lastOccurrenceOfPreviousBatch);
						}
						else if(alarmOccurrenceList.size() > 1 && alarmOccurrenceList.get(alarmOccurrenceList.size() - 1).getId() > 0){ //lastElement is not the latestAlarmoccurence and so the previous element is the lastOne in the Batch
                            AlarmOccurrenceContext lastOccurrenceOfPreviousBatch = alarmOccurrenceList.get(alarmOccurrenceList.size() - 2);
                            lastOccurrenceOfPreviousBatchMap.put(key, lastOccurrenceOfPreviousBatch);
                        }
                    }
                }
                context.put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
            }


            Map<AlarmOccurrenceContext.Type, List<AlarmOccurrenceContext>> occurrenceMap = new HashMap<>();
            for (PointedList<AlarmOccurrenceContext> occurrenceList : alarmOccurrenceMap.values()) {
                for (AlarmOccurrenceContext occurrence : occurrenceList) {
                    List<AlarmOccurrenceContext> alarmOccurrenceContexts = occurrenceMap.get(occurrence.getTypeEnum());
                    if (alarmOccurrenceContexts == null) {
                        alarmOccurrenceContexts = new ArrayList<>();
                        occurrenceMap.put(occurrence.getTypeEnum(), alarmOccurrenceContexts);
                    }
                    alarmOccurrenceContexts.add(occurrence);
                }
            }

            for (AlarmOccurrenceContext.Type type : occurrenceMap.keySet()) {
                List<FacilioField> allFields = modBean.getAllFields(NewAlarmAPI.getOccurrenceModuleName(type));
                InsertRecordBuilder<AlarmOccurrenceContext> builder = new InsertRecordBuilder<AlarmOccurrenceContext>()
                        .moduleName(NewAlarmAPI.getOccurrenceModuleName(type))
                        .fields(allFields);
                List<AlarmOccurrenceContext> records = new ArrayList<>();
                FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(type));
                for (AlarmOccurrenceContext alarmOccurrenceContext : occurrenceMap.get(type)) {
                    JSONObject addOccurrenceInfo = new JSONObject();
                    JSONObject info = new JSONObject();
                    info.put("createdTime", alarmOccurrenceContext.getCreatedTime());
                    info.put("subject", alarmOccurrenceContext.getSubject());
                    info.put("severity", alarmOccurrenceContext.getSeverity());
                    addOccurrenceInfo.put("addOccurrence", info);

                    alarmOccurrenceContext.setData(eventList.get(0).getCustomFields());

                    if (alarmOccurrenceContext.getId() > 0) {
                        UpdateRecordBuilder<AlarmOccurrenceContext> updateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
                                .moduleName(NewAlarmAPI.getOccurrenceModuleName(type))
                                .andCondition(CriteriaAPI.getIdCondition(alarmOccurrenceContext.getId(), module))
                                .fields(allFields);
                        updateBuilder.update(alarmOccurrenceContext);
                        if (alarmOccurrenceContext.getAlarm() != null && alarmOccurrenceContext.getAlarm().getId() > 0) {
                            CommonCommandUtil.addAlarmActivityToContext(alarmOccurrenceContext.getAlarm().getId(), -1, AlarmActivityType.ALARM_OCCURRENCE_UPDATED, addOccurrenceInfo, (FacilioContext) context, alarmOccurrenceContext.getId());
                        }
                    } else {
                        if (alarmOccurrenceContext.getAlarm() != null && alarmOccurrenceContext.getAlarm().getId() > 0) {
                            CommonCommandUtil.addAlarmActivityToContext(alarmOccurrenceContext.getAlarm().getId(), -1, AlarmActivityType.ALARM_OCCURRENCE_CREATED, addOccurrenceInfo, (FacilioContext) context, alarmOccurrenceContext.getId());
                        }
                        records.add(alarmOccurrenceContext);
                    }
                }
                builder.addRecords(records);
                builder.save();

                int readingAlarmOccurrenceCount = 0;   //For HistoricalReadingRule
                if (type != null && type == AlarmOccurrenceContext.Type.READING && records != null && !records.isEmpty() && records.get(0) instanceof ReadingAlarmOccurrenceContext) {
                    readingAlarmOccurrenceCount = records.size();
                    context.put(FacilioConstants.ContextNames.ALARM_COUNT, readingAlarmOccurrenceCount);
                }
            }
        }

        if (MapUtils.isNotEmpty(alarmMap)) {
            for (BaseAlarmContext baseAlarm : alarmMap.values()) {
                List<FacilioField> allBaseAlarmField = modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()));
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allBaseAlarmField);
                GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getBaseAlarmModule().getTableName())
                        .fields(Collections.singletonList(fieldMap.get("lastOccurrenceId")))
                        .andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), ModuleFactory.getBaseAlarmModule()));
                Map<String, Object> map = new HashMap<>();
                map.put("lastOccurrenceId", baseAlarm.getLastOccurrenceId());
                updateBuilder.update(map);
            }
        }

        Map<Type, List<BaseEventContext>> eventsMap = new HashMap<>();
        if (eventList != null) {
            for (BaseEventContext baseEvent : eventList) {
                List<BaseEventContext> list = eventsMap.get(baseEvent.getEventTypeEnum());
                if (list == null) {
                    list = new ArrayList<>();
                    eventsMap.put(baseEvent.getEventTypeEnum(), list);
                }
                list.add(baseEvent);
            }
        }

        if (MapUtils.isNotEmpty(eventsMap)) {
            for (Type type : eventsMap.keySet()) {
                String moduleName = NewEventAPI.getEventModuleName(type);
                InsertRecordBuilder<BaseEventContext> insertBuilder = new InsertRecordBuilder<BaseEventContext>()
                        .moduleName(moduleName)
                        .fields(modBean.getAllFields(moduleName));
                InsertRecordBuilder<BaseEventContext> agentEventInsertBuilder = new InsertRecordBuilder<BaseEventContext>()
                        .moduleName(moduleName)
                        .fields(modBean.getAllFields(moduleName))
                        .insertSupplement(getAgentEventToControllerMultiLookupField());
                List<BaseEventContext> eventRecords = new ArrayList<>();
                List<BaseEventContext> agentEventRecords = new ArrayList<>();
                List<BaseEventContext> moduleEventList = eventsMap.get(type);
                for (BaseEventContext event : moduleEventList) {
                    event.setData(event.getCustomFields());
                    if (event.getId() > 0) {
                        UpdateRecordBuilder<BaseEventContext> builder = new UpdateRecordBuilder<BaseEventContext>()
                                .moduleName(moduleName)
                                .andCondition(CriteriaAPI.getIdCondition(event.getId(), modBean.getModule(moduleName)))
                                .fields(modBean.getAllFields(moduleName));
                        if (event instanceof AgentEventContext) {
                            builder.updateSupplement(getAgentEventToControllerMultiLookupField());
                        }
                        builder.update(event);
                    } else {
                        if (event instanceof AgentEventContext) {
                            agentEventRecords.add(event);
                        } else {
                            eventRecords.add(event);
                        }
                    }
                }
                agentEventInsertBuilder.addRecords(agentEventRecords);
                agentEventInsertBuilder.save();
                insertBuilder.addRecords(eventRecords);
                insertBuilder.save();
            }
        }

        Collection<BaseAlarmContext> alarmList = alarmMap.values();
        if (CollectionUtils.isNotEmpty(alarmList)) {
            Map<String, List<BaseAlarmContext>> alarmModuleMap = new HashMap<>();
            for (BaseAlarmContext alarm : alarmList) {
                String alarmModuleName = NewAlarmAPI.getAlarmModuleName(alarm.getTypeEnum());
                List<BaseAlarmContext> baseAlarmContexts = alarmModuleMap.get(alarmModuleName);
                if (baseAlarmContexts == null) {
                    baseAlarmContexts = new ArrayList<>();
                    alarmModuleMap.put(alarmModuleName, baseAlarmContexts);
                    context.put(FacilioConstants.ContextNames.MODULE_NAME,alarmModuleName);
                    context.put(FacilioConstants.ContextNames.ALARM_ID,alarm.getId());
                }
                baseAlarmContexts.add(alarm);
            }
            context.put(FacilioConstants.ContextNames.RECORD_MAP, alarmModuleMap);
            context.put (FacilioConstants.ContextNames.CHANGE_SET_MAP,changes);
            constructEventType(context);
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (MapUtils.isNotEmpty(alarmOccurrenceMap)) {
            Set<Long> alarmOccurrenceIds = new HashSet<>();
            for (String key : alarmOccurrenceMap.keySet()) {
                for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceMap.get(key)) {
                    alarmOccurrenceIds.add(alarmOccurrence.getId());
                }
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule alarmOccurrenceModule = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
            Map<String, FacilioField> occurrenceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(alarmOccurrenceModule.getName()));
            FacilioField noOfEventsField = occurrenceFieldMap.get("noOfEvents");

            FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
            Map<String, FacilioField> eventFieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
            List<FacilioField> fields = new ArrayList<>();
            FacilioField alarmOccurrenceField = eventFieldMap.get("alarmOccurrence");
            fields.add(alarmOccurrenceField);

//			FacilioField countField = new FacilioField();
//			countField.setName("count");
//			countField.setColumnName("COUNT(*)");
//			countField.setDataType(FieldType.NUMBER);
//			fields.add(countField);

            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .module(eventModule)
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField())
                    .select(fields)
                    .groupBy(alarmOccurrenceField.getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(alarmOccurrenceField, StringUtils.join(alarmOccurrenceIds, ","), NumberOperators.EQUALS));

            List<Map<String, Object>> list = builder.getAsProps();
            Map<String, Object> updateMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                long id = ((Number) ((Map) map.get("alarmOccurrence")).get("id")).longValue();
                int numberOfEvents = ((Number) map.get("id")).intValue();

                updateMap.put("noOfEvents", numberOfEvents);

                UpdateRecordBuilder<AlarmOccurrenceContext> updateRecordBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
                        .module(alarmOccurrenceModule)
                        .fields(Collections.singletonList(noOfEventsField))
                        .andCondition(CriteriaAPI.getIdCondition(id, alarmOccurrenceModule));

                updateRecordBuilder.updateViaMap(updateMap);
            }

            Set<Long> alarmIds = new HashSet<>();
            List<BaseAlarmContext> alarms = new ArrayList<BaseAlarmContext>();
            for (String key : alarmOccurrenceMap.keySet()) {
                for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceMap.get(key)) {
                    if (!alarmIds.contains(alarmOccurrence.getAlarm().getId())) {
                        alarmIds.add(alarmOccurrence.getAlarm().getId());
                        alarms.add(alarmOccurrence.getAlarm());
                    }
                }
            }

            FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
            FacilioField noOfOccurrenceField = modBean.getField("noOfOccurrences", alarmModule.getName());

            fields = new ArrayList<>();
            FacilioField alarmField = occurrenceFieldMap.get("alarm");
            fields.add(alarmField);
//			fields.add(countField);
            builder = new SelectRecordsBuilder()
                    .module(alarmOccurrenceModule)
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField())
                    .select(fields)
                    .groupBy(alarmField.getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(alarmField, StringUtils.join(alarmIds, ","), NumberOperators.EQUALS));
            list = builder.getAsProps();
            updateMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                long id = ((Number) ((Map) map.get("alarm")).get("id")).longValue();
                int numberOfOccurrences = ((Number) map.get("id")).intValue();

                updateMap.put("noOfOccurrences", numberOfOccurrences);

                UpdateRecordBuilder<BaseAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
                        .module(alarmModule)
                        .fields(Collections.singletonList(noOfOccurrenceField))
                        .andCondition(CriteriaAPI.getIdCondition(id, alarmModule));

                updateRecordBuilder.updateViaMap(updateMap);
            }
            try {
                if (eventTypes != null && (eventTypes.contains(EventType.CREATE) || eventTypes.contains(EventType.UPDATED_ALARM_SEVERITY)) && alarms.size() == 1 &&
                        (AccountUtil.getCurrentOrg().getOrgId() != 88 || alarms.get(0).getSeverity().getId() == AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CRITICAL_SEVERITY).getId())) {
                    // temp check for reading alarm...needs to support bms alarms also
                    if (alarms.get(0).getTypeEnum() == Type.READING_ALARM) {
                        WmsEvent event = new WmsEvent();
                        event.setNamespace("newAlarm");
                        event.setEventType(WmsEventType.Module.RECORD_UPDATE);
                        JSONObject record = new JSONObject();
                        record.put("id", alarms.get(0).getId());
                        event.setAction("refetch");
                        event.addData("record", record);
                        event.addData("sound", true);
                        event.setSessionType(LiveSessionType.APP);

                        List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
                        List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
                        WmsApi.sendEvent(recipients, event);
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Exception occurred while pushing Web notification during alarm updation ", e);
            }
        }
        return false;
    }

    public static MultiLookupField getAgentAlarmToControllerMultiLookupField() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> map = FieldFactory.getAsMap(moduleBean.getAllFields("agentAlarm"));

        if (map.containsKey("controllersList")) {
            return (MultiLookupField) map.get("controllersList");
        }
        LOGGER.info("controllersList field missing");
        return null;
    }

    public static MultiLookupField getAgentEventToControllerMultiLookupField() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> map = FieldFactory.getAsMap(moduleBean.getAllFields("agentAlarmEvent"));

        if (map.containsKey("controllersList")) {
            return (MultiLookupField) map.get("controllersList");
        }
        LOGGER.info("controllersList field missing");
        return null;
    }
    public void constructEventType(Context context){
        Map<Long,Object> changeSet= (Map<Long,Object>) context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
        List<Object> eventTypes= (List<Object>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        if(CollectionUtils.isEmpty(eventTypes) && (MapUtils.isNotEmpty(changeSet))){
            CommonCommandUtil.addEventType(EventType.EDIT,(FacilioContext) context);
        }
    }

}