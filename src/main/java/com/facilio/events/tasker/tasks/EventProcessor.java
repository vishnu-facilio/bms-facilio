package com.facilio.events.tasker.tasks;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventProcessor implements IRecordProcessor {

    private List<EventRule> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long orgId;
    private long lastEventTime = System.currentTimeMillis();
    private String shardId;

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    EventProcessor(long orgId){
        this.orgId =orgId;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.getShardId();
        try {
            AccountUtil.setCurrentAccount(orgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Initializing record processor for shard: " + shardId);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        try {
            List<EventRule> ruleList = EventRulesAPI.getEventRules(orgId);
            if(ruleList != null){
                eventRules = ruleList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Record record : processRecordsInput.getRecords()) {
            try {
                String data = decoder.decode(record.getData()).toString();
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(data);
                boolean alarmCreated = process(record.getApproximateArrivalTimestamp().getTime(),  object);
                if(alarmCreated) {
                    processRecordsInput.getCheckpointer().checkpoint(record);
                }
            } catch (InvalidStateException | ShutdownException | CharacterCodingException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        System.out.println("Shutting down record processor for shard: " + shardId);
    }


    private boolean process(long timestamp, JSONObject object) {
        try {
            EventContext event = EventAPI.processPayload(timestamp, object, orgId);
            Map<String, Object> prop = FieldUtil.getAsProperties(event);
            EventAPI.insertObject(prop);
            event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
            if(eventRules.isEmpty()){
                triggerAlarm(prop);
                return true;
            } else {
                for(EventRule rule : eventRules) {

                    Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getBaseCriteriaId());
                    boolean isRuleMatched = criteria.computePredicate().evaluate(prop);

                    if (isRuleMatched) {
                        boolean ignoreEvent = rule.isIgnoreEvent();

                        event.setEventRuleId(rule.getEventRuleId());
                        event.setInternalState(EventContext.EventInternalState.FILTERED);

                        if (ignoreEvent) {
                            event.setEventState(EventContext.EventState.IGNORED);
                        } else {

                            event = EventTransformJob.transform(orgId, event, prop, rule);
                            event.setEventRuleId(rule.getEventRuleId());

                            if (rule.getThresholdCriteriaId() != -1) {
                                Criteria thresholdCriteria = CriteriaAPI.getCriteria(orgId, rule.getThresholdCriteriaId());
                                boolean isThresholdMatched = thresholdCriteria.computePredicate().evaluate(event);
                                if (isThresholdMatched) {
                                    long currentEventTime = event.getCreatedTime();
                                    boolean skipEvent = (currentEventTime - lastEventTime) < rule.getThresholdOverSeconds();
                                    lastEventTime = currentEventTime;
                                    if (skipEvent) {
                                        event.setEventState(EventContext.EventState.IGNORED);
                                    } else {
                                        int thresholdOccurs = rule.getThresholdOccurs();
                                        int numberOfEvents = eventCountMap.getOrDefault(event.getMessageKey(), 0);
                                        int numberOfEventsOccurred = numberOfEvents + 1;
                                        eventCountMap.put(event.getMessageKey(), numberOfEventsOccurred);
                                        if (thresholdOccurs <= (numberOfEventsOccurred)) {
                                            eventCountMap.put(event.getMessageKey(), 0);
                                            triggerAlarm(FieldUtil.getAsProperties(event));
                                            return true;
                                        }
                                    }
                                }
                                event.setInternalState(EventContext.EventInternalState.THRESHOLD_DONE);
                            } else {
                                triggerAlarm(FieldUtil.getAsProperties(event));
                            }
                        }
                        EventAPI.updateEvent(event, orgId);
                        return true;
                    } else {
                        triggerAlarm(prop);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void triggerAlarm(Map<String, Object> prop) throws Exception {
        EventToAlarmJob.alarm(orgId, prop);
    }
}
