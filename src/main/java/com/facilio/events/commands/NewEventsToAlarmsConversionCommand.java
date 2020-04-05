package com.facilio.events.commands;

import java.util.*;

import com.facilio.activity.AlarmActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import org.json.simple.JSONObject;

public class NewEventsToAlarmsConversionCommand extends FacilioCommand {

	private Set<String> eventKeys1 = new HashSet<>();
	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = new HashMap<>();
	private Map<String, BaseAlarmContext> alarmMap = new HashMap<>();
	private List<BaseEventContext> baseEvents;
	private AlarmSeverityContext clearSeverity;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			baseEvents = new ArrayList<>((List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST));
			context.put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);

			List<BaseEventContext> events = baseEvents;
			while (true) {
				iterateEventToProcess(context, events);
				Collection<BaseAlarmContext> values = alarmMap.values();
				if (CollectionUtils.isEmpty(values)) {
					break;
				}

				events = new ArrayList<>();
				for (BaseAlarmContext baseAlarm : values) {
					List<BaseEventContext> baseEventContexts = baseAlarm.removeAdditionalEvents();
					if (CollectionUtils.isNotEmpty(baseEventContexts)) {
						events.addAll(baseEventContexts);
					}
				}
				if (CollectionUtils.isEmpty(events)) {
					break;
				} else {
					baseEvents.addAll(events);
				}
			}
			context.put("alarmOccurrenceMap", alarmOccurrenceMap);
			context.put("alarmMap", alarmMap);
		}
		return false;
	}

	private void iterateEventToProcess(Context context, List<BaseEventContext> baseEvents) throws Exception {
		Set<String> eventKeys = new HashSet<>();
		for (BaseEventContext baseEvent : baseEvents) {
			String messageKey = baseEvent.getMessageKey();
			eventKeys.add(messageKey);
		}

		Boolean constructHistoricalAutoClearEvent = (Boolean) context.get(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT);
		constructHistoricalAutoClearEvent = (constructHistoricalAutoClearEvent == null) ? true : constructHistoricalAutoClearEvent;
		
		if(!constructHistoricalAutoClearEvent) //Not the last eventsBatch to be processed for historical
		{ 
			HashMap<String,AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) context.get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
			if(lastOccurrenceOfPreviousBatchMap != null && MapUtils.isNotEmpty(lastOccurrenceOfPreviousBatchMap)) { 
				for (String key : lastOccurrenceOfPreviousBatchMap.keySet()) {
					AlarmOccurrenceContext lastOccurrenceOfPreviousBatch = lastOccurrenceOfPreviousBatchMap.get(key);
					PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
					if (pointedList == null) { // adding previousBatch lastOccurrence as the first element in the list, preceding latestAlarmOccurrence
						pointedList = new PointedList<>();
						alarmOccurrenceMap.put(key, pointedList);
					}
					pointedList.add(lastOccurrenceOfPreviousBatch);
				}
			}
		}
		
		clearSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
		List<AlarmOccurrenceContext> latestAlarmOccurance = NewAlarmAPI.getLatestAlarmOccurance(new ArrayList<>(eventKeys));
		for (AlarmOccurrenceContext alarmOccurrenceContext : latestAlarmOccurance) {
			String key = alarmOccurrenceContext.getAlarm().getKey();
			PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
			if (pointedList == null) {
				// expecting records should come here
				pointedList = new PointedList<>();
				alarmOccurrenceMap.put(key, pointedList);
			}
			pointedList.add(alarmOccurrenceContext);
		}
		
		List<BaseEventContext> additionEventsCreated = new ArrayList<>();
		for (BaseEventContext baseEvent : baseEvents) {
			processEventToAlarm(baseEvent, context, additionEventsCreated);
		}
		baseEvents.addAll(additionEventsCreated);

		if(constructHistoricalAutoClearEvent) {
			for (String key : eventKeys) {
				PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
				if (CollectionUtils.isEmpty(pointedList)) {
					continue;
				}

				List<AlarmOccurrenceContext> list = new ArrayList<>(pointedList);
				for (AlarmOccurrenceContext alarmOccurrence : list) {
					if (!alarmOccurrence.equals(pointedList.getLastRecord()) && !alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
						BaseAlarmContext alarm = alarmOccurrence.getAlarm();
						alarm = NewAlarmAPI.getAlarm(alarm.getId());
						BaseEventContext createdEvent = BaseEventContext.createNewEvent(alarm.getTypeEnum(), alarm.getResource(),
								AlarmAPI.getAlarmSeverity("Clear"), "Automated Clear Event", alarm.getKey(), alarmOccurrence.getLastOccurredTime() + 1000);
						JSONObject info = new JSONObject();
						info.put("field", "Severity");
						info.put("newValue", AlarmAPI.getAlarmSeverity("Clear").getDisplayName());
						if (alarmOccurrence.getPreviousSeverity() != null){
							info.put("oldValue", AlarmAPI.getAlarmSeverity(alarmOccurrence.getPreviousSeverity().getId()).getDisplayName());
						}
						if (alarmOccurrence.getAlarm() != null && alarmOccurrence.getAlarm().getId() > 0) {
							CommonCommandUtil.addAlarmActivityToContext(alarmOccurrence.getAlarm().getId(),-1, AlarmActivityType.CLEAR_ALARM, info, (FacilioContext) context, alarmOccurrence.getId());
						}
						baseEvents.add(createdEvent);
						processEventToAlarm(createdEvent, context, additionEventsCreated);
					}
				}
			}	
		}
		
	}

	private void processEventToAlarm(BaseEventContext baseEvent, Context context, List<BaseEventContext> additionEventsCreated) throws Exception {
		if (baseEvent.getEventStateEnum() != EventState.IGNORED) {
			if(baseEvent.getSeverity().getSeverity().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
				baseEvent.setEventState(EventState.IGNORED);
			}
			else {
				addOrUpdateAlarm(baseEvent, context, additionEventsCreated);
			}
			baseEvent.setInternalState(EventInternalState.COMPLETED);
		}
	}

	private void addOrUpdateAlarm(BaseEventContext baseEvent, Context context, List<BaseEventContext> additionEventsCreated) throws Exception {
		PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(baseEvent.getMessageKey());
		if (pointedList == null) {
			pointedList = new PointedList<>();
			alarmOccurrenceMap.put(baseEvent.getMessageKey(), pointedList);
		}
		AlarmOccurrenceContext alarmOccurrence = pointedList.isEmpty() ? null : pointedList.getCurrentRecord();
		boolean mostRecent = pointedList.isCurrentLast();
		if (alarmOccurrence == null) {
			// Only for newly creating alarm
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent, context);
			pointedList.add(alarmOccurrence);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else if (alarmOccurrence.getSeverity().equals(clearSeverity)) {
			if (baseEvent.getSeverity().equals(clearSeverity)) {
				baseEvent.setEventState(EventState.IGNORED);
				return;
			}
			// create alarm occurrence
			mostRecent = baseEvent.getCreatedTime() > pointedList.getLastRecord().getCreatedTime();
			
			int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
			if (alarmOccurrence.getCreatedTime() < baseEvent.getCreatedTime()) {
				oldObjectIndex ++;
			}
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
			pointedList.getLastRecord().setTimeBetweeenOccurrence((alarmOccurrence.getCreatedTime() - pointedList.getLastRecord().getClearedTime())/1000);
			if (mostRecent) {
				pointedList.add(alarmOccurrence);
				pointedList.moveNext();
			} else {
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else { // if alarm is not cleared, only update in local object.

			// for first historical event
			if (baseEvent.getCreatedTime() < alarmOccurrence.getCreatedTime()) {
				mostRecent = false;
				int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
				alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}

			BaseEventContext additionClearEvent = baseEvent.createAdditionClearEvent(alarmOccurrence);
			if (additionClearEvent != null) {
				additionClearEvent.setSeverity(clearSeverity);
				NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, additionClearEvent, mostRecent, context);
				additionClearEvent.setAlarmOccurrence(alarmOccurrence);
				additionClearEvent.setEventState(EventState.ALARM_UPDATED);
				additionEventsCreated.add(additionClearEvent);

				alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
				pointedList.add(alarmOccurrence);
				pointedList.moveNext();
			}
			else {
				NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent, mostRecent, context);
			}
			baseEvent.setEventState(EventState.ALARM_UPDATED);
		}
		
		alarmMap.put(baseEvent.getMessageKey(), alarmOccurrence.getAlarm());
		baseEvent.setAlarmOccurrence(alarmOccurrence);
		baseEvent.setBaseAlarm(alarmOccurrence.getAlarm());
	}
	public static class PointedList<E> extends ArrayList<E> {
		private static final long serialVersionUID = 1L;
		private int position = 0;
		
		public PointedList() {
		}
		
		public void moveNext() {
			position ++;
			if (position >= size()) {
				position = size() -1;
			}
		}
		
		public void movePrevious() {
			position --;
			if (position < 0) {
				position = 0;
			}
		}
		
		public void setPosition(int position) {
			if (position < 0) {
				position = 0;
			}
			else if (position >= size()) {
				position = size() - 1;
			}
			this.position = position;
		}

//		public boolean add(E e) {
//			return super.add(e);
//		}
		
		public boolean isCurrentLast() {
			return position == (size() - 1);
		}
		
		public E getLastRecord() {
			return get(size() - 1);
		}
		
		public E getCurrentRecord() {
			if (CollectionUtils.isEmpty(this)) {
				return null;
			}
			return get(position);
		}
	}

}
