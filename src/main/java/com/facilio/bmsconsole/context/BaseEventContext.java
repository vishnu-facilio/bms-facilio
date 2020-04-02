package com.facilio.bmsconsole.context;

import com.facilio.activity.AlarmActivityType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

public class BaseEventContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private double cost;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}


	private String description;
	private boolean superCalled = false;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String eventMessage;
	public String getEventMessage() {
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	public String getMessage() {
		return eventMessage;
	}
	public void setMessage(String message) {
		this.eventMessage = message;
	}

	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private AlarmOccurrenceContext alarmOccurrence;
	public AlarmOccurrenceContext getAlarmOccurrence() {
		return alarmOccurrence;
	}
	public void setAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence) {
		this.alarmOccurrence = alarmOccurrence;
	}

	private BaseAlarmContext baseAlarm;
	public BaseAlarmContext getBaseAlarm() {
		return baseAlarm;
	}
	public void setBaseAlarm(BaseAlarmContext baseAlarm) {
		this.baseAlarm = baseAlarm;
	}

	private String messageKey;
	public String getMessageKey() {
		if (StringUtils.isEmpty(messageKey)) {
			messageKey = constructMessageKey();
		}
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	};
	public String constructMessageKey() {
		return null;
	}
	
	private EventState eventState;
	public int getEventState() {
		if(eventState != null) {
			return eventState.getIntVal();
		}
		return -1;
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
		if(internalState != null) {
			return internalState.getIntVal();
		}
		return -1;
	}
	public void setInternalState(int internalState) {
		this.internalState = EventContext.INETERNAL_STATES[internalState-1];
	}
	public void setInternalState(EventInternalState internalState) {
		this.internalState = internalState;
	}
	public EventInternalState getInternalStateEnum() {
		return internalState;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		if (createdTime == -1) {
			return DateTimeUtil.getCurrenTime();
		}
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	private String priority;
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	private String severityString;
	public String getSeverityString() {
		return severityString;
	}
	public void setSeverityString(String severityString) {
		this.severityString = severityString;
	}
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private String comment;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

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
		if (autoClear == null) {
			return false;
		}
		return autoClear;
	}
	public void setAutoClear(Boolean autoClear) {
		this.autoClear = autoClear;
	}
	
	private String possibleCause;
	public String getPossibleCause() {
		return possibleCause;
	}
	public void setPossibleCause(String possibleCause) {
		this.possibleCause = possibleCause;
	}
	
	private String recommendation;
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

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
		if(this.additionInfo == null) {
			this.additionInfo =  new JSONObject();
		}
		this.additionInfo.put(key,value);
	}

	public String getAdditionalInfoJsonStr() {
		if(additionInfo != null) {
			return additionInfo.toJSONString();
		}
		return null;
	}
	public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
		JSONParser parser = new JSONParser();
		additionInfo = (JSONObject) parser.parse(jsonStr);
	}
	
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (StringUtils.isNotEmpty(getEventMessage())) {
			baseAlarm.setSubject(getEventMessage());
		}
		else {
			AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");
			if (getSeverity().equals(clearSeverity)) {
				setEventMessage("Clear Event");
			}
		}
		baseAlarm.setDescription(getDescription());

		if (add) {
			baseAlarm.setResource(getResource());
			baseAlarm.setKey(getMessageKey());
			if(getSiteId() != -1) {
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
			if(getSiteId() != -1) {
				alarmOccurrence.setSiteId(getSiteId());
			}
			alarmOccurrence.setNoOfEvents(1);
		} 
		else {
			if (!previousSeverity.equals(getSeverity())) {
				if (!getSeverity().equals(clearSeverity)) {
					JSONObject info = new JSONObject();
					info.put("field", "Severity");
					info.put("newValue", getSeverity().getDisplayName());
					info.put("oldValue", previousSeverity.getDisplayName());
					if (alarmOccurrence.getAlarm()!=null && alarmOccurrence.getAlarm().getId() >0) {
						CommonCommandUtil.addAlarmActivityToContext(alarmOccurrence.getAlarm().getId(), -1, AlarmActivityType.SEVERITY_CHANGE, info, (FacilioContext) context, alarmOccurrence.getId());
					}
					CommonCommandUtil.addEventType(EventType.UPDATED_ALARM_SEVERITY, (FacilioContext) context);
					alarmOccurrence.setAcknowledged(false);
					alarmOccurrence.setAcknowledgedBy(null);
					alarmOccurrence.setAcknowledgedTime(-1l);
				}
				else {
					CommonCommandUtil.addEventType(EventType.ALARM_CLEARED, (FacilioContext) context);
				}
				alarmOccurrence.setPreviousSeverity(previousSeverity);
			}
			alarmOccurrence.setNoOfEvents(alarmOccurrence.getNoOfEvents() + 1);
		}
		
		if (StringUtils.isNotEmpty(getPossibleCause())) {
			if (StringUtils.isNotEmpty(getPossibleCause())) {
				alarmOccurrence.setPossibleCauses(getPossibleCause());
			}
		}


		if (StringUtils.isNotEmpty(getRecommendation())) {
			if (StringUtils.isNotEmpty(getRecommendation())) {
				alarmOccurrence.setRecommendation(getRecommendation());
			}
		}

		return alarmOccurrence;
	}
	
	public static BaseEventContext createNewEvent(Type typeEnum, ResourceContext resourceContext, AlarmSeverityContext alarmSeverity, String message, String messageKey, long createdTime) {
		BaseEventContext baseEvent = null;
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
		if (getSeverity() == null) {
			return true;
		}
		return false;
	}

	public boolean isSuperCalled() {
		return superCalled;
	}

    public BaseEventContext createAdditionClearEvent(AlarmOccurrenceContext alarmOccurrence) {
        return null;
    }
}
