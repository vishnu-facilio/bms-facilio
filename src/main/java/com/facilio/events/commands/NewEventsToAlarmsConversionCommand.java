package com.facilio.events.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;

public class NewEventsToAlarmsConversionCommand extends FacilioCommand {

	private Set<String> eventKeys = new HashSet<>();
	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = new HashMap<>();
	private Map<String, BaseAlarmContext> alarmMap = new HashMap<>();
	private List<BaseEventContext> baseEvents;
	private AlarmSeverityContext clearSeverity;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			baseEvents = new ArrayList<>((List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST));
			context.put(context.get(EventConstants.EventContextNames.EVENT_LIST), baseEvents);
			for (BaseEventContext baseEvent : baseEvents) {
				String messageKey = baseEvent.getMessageKey();
				eventKeys.add(messageKey);
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

			for (String key : eventKeys) {
				PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
				if (CollectionUtils.isEmpty(pointedList)) {
					continue;
				}

				List<AlarmOccurrenceContext> list = new ArrayList<>(pointedList);
				for (AlarmOccurrenceContext alarmOccurrence : list) {
					if (!alarmOccurrence.equals(pointedList.getLastRecord()) && !alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
						BaseAlarmContext alarm = alarmOccurrence.getAlarm();
						BaseEventContext createdEvent = BaseEventContext.createNewEvent(alarm.getTypeEnum(), alarm.getResource(),
								AlarmAPI.getAlarmSeverity("Clear"), "Automated Clear Event", alarm.getKey(), alarmOccurrence.getLastOccurredTime() + 1000);
						baseEvents.add(createdEvent);
						processEventToAlarm(createdEvent, context, additionEventsCreated);
					}
				}
			}

			context.put("alarmOccurrenceMap", alarmOccurrenceMap);
			context.put("alarmMap", alarmMap);
		}
		return false;
	}

	private void saveRecords() throws Exception {
		
	}

	private void processEventToAlarm(BaseEventContext baseEvent, Context context, List<BaseEventContext> additionEventsCreated) throws Exception {
		if (baseEvent.getEventStateEnum() != EventState.IGNORED) {
			if(baseEvent.getSeverityString().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
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
			AlarmOccurrenceContext lastAlarmOccurrence = pointedList.getLastRecord();
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
			updateTimeBetweenOccurrence(lastAlarmOccurrence,alarmOccurrence.getCreatedTime());
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
	}
	public static void updateTimeBetweenOccurrence(AlarmOccurrenceContext lastAlarm,long currentAlarmCreatedTime) throws Exception{
		lastAlarm.setTimeBetweeenOccurrence((currentAlarmCreatedTime - lastAlarm.getClearedTime())/1000);
		NewAlarmAPI.updateAlarmOccurrence(lastAlarm);
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
