package com.facilio.bmsconsole.context;

import com.facilio.activity.AlarmActivityType;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.sensor.SensorEventContext;
import com.facilio.bmsconsole.context.sensor.SensorRollUpEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Getter
@Setter
public class BaseEventContext extends ModuleBaseWithCustomFields {
    private static final long serialVersionUID = 1L;

    private double cost;

    private String description;
    private boolean superCalled = false;

    private String eventMessage;

    public String getMessage() {
        return eventMessage;
    }

    public void setMessage(String message) {
        this.eventMessage = message;
    }

    private ResourceContext resource;

    private AlarmOccurrenceContext alarmOccurrence;

    private BaseAlarmContext baseAlarm;

    private String messageKey;

    public String getMessageKey() {
        return StringUtils.isEmpty(messageKey) ? constructMessageKey() : messageKey;
    }

    public String constructMessageKey() {
        return null;
    }

    private EventState eventState;

    public int getEventState() {
        return eventState == null ? -1 : eventState.getIntVal();
    }

    public void setEventState(int eventState) {
        this.eventState = EventContext.EVENT_STATES[eventState - 1];
    }

    public void setEventState(EventState eventState) {
        this.eventState = eventState;
    }

    public EventState getEventStateEnum() {
        return eventState;
    }

    private EventInternalState internalState;

    public int getInternalState() {
        if (internalState != null) {
            return internalState.getIntVal();
        }
        return -1;
    }

    public void setInternalState(int internalState) {
        this.internalState = EventContext.INETERNAL_STATES[internalState - 1];
    }

    public void setInternalState(EventInternalState internalState) {
        this.internalState = internalState;
    }

    public EventInternalState getInternalStateEnum() {
        return internalState;
    }

    private long createdTime = -1;

    public long getCreatedTime() {
        return (createdTime == -1) ? DateTimeUtil.getCurrenTime() : createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    private String state;

    private String priority;

    private String severityString;

    private AlarmSeverityContext severity;

    private String comment;

    private Map<String, Object> customFields;

    private Type type;

    @JsonInclude
    public final int getEventType() {
        type = getEventTypeEnum();
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

    // this will be used only to get data from database
    public final void setEventType(int eventType) {
        type = Type.valueOf(eventType);
    }

    // this will be used only to get data from database
    public Type getEventTypeEnum() {
        return type;
    }

    private Boolean autoClear;

    public Boolean getAutoClear() {
        return (autoClear == null) ? false : autoClear;
    }

    private String possibleCause;

    private String recommendation;

    private JSONObject additionInfo;

    public JSONObject getAdditionInfo() {
        if (additionInfo == null) {
            this.additionInfo = new JSONObject();
            if (MapUtils.isNotEmpty(getData())) {
                this.additionInfo.putAll(getData());
            }
        }
        return additionInfo;
    }

    public void addAdditionInfo(String key, Object value) {
        if (this.additionInfo == null) {
            this.additionInfo = new JSONObject();
        }
        this.additionInfo.put(key, value);
    }

    public String getAdditionalInfoJsonStr() {
        return (additionInfo != null) ? additionInfo.toJSONString() : null;
    }

    public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
        JSONParser parser = new JSONParser();
        additionInfo = (JSONObject) parser.parse(jsonStr);
    }

    public boolean isClearEvent() throws Exception {
        AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");
        return clearSeverity.equals(getSeverity());
    }

    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (StringUtils.isNotEmpty(getEventMessage())) {
            baseAlarm.setSubject(getEventMessage());
        } else {
            if (isClearEvent()) {
                setEventMessage("Clear Event");
            }
        }
        baseAlarm.setDescription(getDescription());

        if (add) {
            baseAlarm.setResource(getResource());
            baseAlarm.setKey(getMessageKey());
            if (getSiteId() != -1) {
                baseAlarm.setSiteId(getSiteId());
            }
        }
        return baseAlarm;
    }

    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (alarmOccurrence == null && add) {
            alarmOccurrence = new AlarmOccurrenceContext();
        }
        AlarmSeverityContext previousSeverity = alarmOccurrence.getSeverity();
        alarmOccurrence.setSeverity(getSeverity());
        alarmOccurrence.setAutoClear(getAutoClear());
        alarmOccurrence.setLastOccurredTime(getCreatedTime());

        AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");
        if (getSeverity().equals(clearSeverity)) {
            alarmOccurrence.setClearedTime(getCreatedTime());
        }

        JSONObject additionInfo = getAdditionInfo();
        if (additionInfo != null && !additionInfo.isEmpty()) {
            for (Object keySet : additionInfo.keySet()) {
                Object o = additionInfo.get(keySet);
                if (o != null) {
                    alarmOccurrence.addAdditionInfo(keySet.toString(), o);
                }
            }
        }

        if (add) {
            CommonCommandUtil.addEventType(EventType.CREATE, (FacilioContext) context);
            alarmOccurrence.setCreatedTime(getCreatedTime());
            alarmOccurrence.setResource(getResource());
            if (getSiteId() != -1) {
                alarmOccurrence.setSiteId(getSiteId());
            }
            alarmOccurrence.setNoOfEvents(1);
        } else {
            if (!previousSeverity.equals(getSeverity())) {
                if (!getSeverity().equals(clearSeverity)) {
                    JSONObject info = new JSONObject();
                    info.put("field", "Severity");
                    info.put("newValue", getSeverity().getDisplayName());
                    info.put("oldValue", previousSeverity.getDisplayName());
                    if (alarmOccurrence.getAlarm() != null && alarmOccurrence.getAlarm().getId() > 0) {
                        CommonCommandUtil.addAlarmActivityToContext(alarmOccurrence.getAlarm().getId(), -1, AlarmActivityType.SEVERITY_CHANGE, info, (FacilioContext) context, alarmOccurrence.getId());
                    }
                    CommonCommandUtil.addEventType(EventType.UPDATED_ALARM_SEVERITY, (FacilioContext) context);
                    CommonCommandUtil.addEventType(EventType.CREATE_OR_UPDATE_ALARM_SEVERITY,(FacilioContext)context);
                    alarmOccurrence.setAcknowledged(false);
                    alarmOccurrence.setAcknowledgedBy(null);
                    alarmOccurrence.setAcknowledgedTime(-1l);
                } else {
                    CommonCommandUtil.addEventType(EventType.ALARM_CLEARED, (FacilioContext) context);
                }
                alarmOccurrence.setPreviousSeverity(previousSeverity);
            }
            alarmOccurrence.setNoOfEvents(alarmOccurrence.getNoOfEvents() + 1);
        }

        if (StringUtils.isNotEmpty(getPossibleCause())) {
                alarmOccurrence.setPossibleCauses(getPossibleCause());
        }


        if (StringUtils.isNotEmpty(getRecommendation())) {
                alarmOccurrence.setRecommendation(getRecommendation());
        }

        return alarmOccurrence;
    }

    public static BaseEventContext createNewEvent(Type typeEnum, ResourceContext resourceContext, AlarmSeverityContext alarmSeverity, String message, String messageKey, long createdTime) {
        BaseEventContext baseEvent;
        switch (typeEnum) {
            case READING_ALARM:
                baseEvent = new ReadingEventContext();
                break;
            case PRE_ALARM:
                baseEvent = new PreEventContext();
                break;
            case OPERATION_ALARM:
                baseEvent = new OperationAlarmEventContext();
                break;
            case RULE_ROLLUP_ALARM:
                baseEvent = new RuleRollUpEvent();
                break;
            case ASSET_ROLLUP_ALARM:
                baseEvent = new AssetRollUpEvent();
                break;
            case SENSOR_ALARM:
                baseEvent = new SensorEventContext();
                break;
            case SENSOR_ROLLUP_ALARM:
                baseEvent = new SensorRollUpEventContext();
                break;
            case AGENT_ALARM:
                baseEvent = new AgentEventContext();
                break;
            case ML_ANOMALY_ALARM:
                baseEvent = new MLAnomalyEvent();
                break;
            case MULTIVARIATE_ANOMALY_ALARM:
                baseEvent = new MultiVariateAnomalyEvent();
                break;
            default:
                throw new IllegalArgumentException("Invalid type");
        }

        if (createdTime == -1) {
            createdTime = DateTimeUtil.getCurrenTime();
        }

        if (alarmSeverity == null) {
            throw new IllegalArgumentException("Severity cannot be empty");
        }

        baseEvent.setCreatedTime(createdTime);
        baseEvent.setSeverity(alarmSeverity);
        baseEvent.setSeverityString(alarmSeverity.getSeverity());
        baseEvent.setEventMessage(message);
        baseEvent.setMessageKey(messageKey);
        baseEvent.setComment("Automated event");
        baseEvent.setResource(resourceContext);

        return baseEvent;
    }

    public boolean shouldIgnore() {
        superCalled = true;
        return getSeverity() == null;
    }

    public boolean isSuperCalled() {
        return superCalled;
    }

    public BaseEventContext createAdditionClearEvent(AlarmOccurrenceContext alarmOccurrence) {
        return null;
    }

    private Boolean isLiveEvent;

    public boolean isLiveEvent() {
        return (isLiveEvent != null) ? isLiveEvent.booleanValue() : Boolean.FALSE;
    }

    public Boolean getIsLiveEvent() {
        return isLiveEvent;
    }

    public void setIsLiveEvent(Boolean isLiveEvent) {
        this.isLiveEvent = isLiveEvent;
    }

    private EventProcessingStatus eventProcessingStatus;

    public int getEventProcessingStatus() {
        return (eventProcessingStatus != null) ? eventProcessingStatus.getIndex() : -1;
    }

    public EventProcessingStatus getEventProcessingStatusAsEnum() {
        return eventProcessingStatus;
    }

    public void setEventProcessingStatus(int statusint) {
        this.eventProcessingStatus = EventProcessingStatus.getAllOptions().get(statusint);
    }

    public void setEventProcessingStatusAsEnum(EventProcessingStatus status) {
        this.eventProcessingStatus = status;
    }

    public enum EventProcessingStatus implements FacilioIntEnum {

        UNPROCESSED(1),
        ALARM_GENERATED(2),
        ;

        int intVal;

        EventProcessingStatus(int intVal) {
            this.intVal = intVal;
        }

        @Override
        public Integer getIndex() {
            return intVal;
        }

        @Override
        public String getValue() {
            return name();
        }

        private static final Map<Integer, EventProcessingStatus> optionMap = Collections.unmodifiableMap(initTypeMap());

        private static Map<Integer, EventProcessingStatus> initTypeMap() {
            Map<Integer, EventProcessingStatus> typeMap = new HashMap<>();

            for (EventProcessingStatus status : values()) {
                typeMap.put(status.getIndex(), status);
            }
            return typeMap;
        }


        public static Map<Integer, EventProcessingStatus> getAllOptions() {
            return optionMap;
        }
    }
}
