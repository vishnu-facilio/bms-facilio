package com.facilio.events.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class NewEventsToAlarmsConversionCommand implements Command {

	private Map<String, List<BaseEventContext>> eventsMap = new HashMap<>();
	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = new HashMap<>();
	private Map<String, BaseAlarmContext> alarmMap = new HashMap<>();
	private List<BaseEventContext> baseEvents;

	@Override
	public boolean execute(Context context) throws Exception {
		baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			for (BaseEventContext baseEvent : baseEvents) {
				String messageKey = baseEvent.getMessageKey();
				List<BaseEventContext> list = eventsMap.get(messageKey);
				if (list == null) {
					list = new ArrayList<>();
					eventsMap.put(messageKey, list);
				}
				list.add(baseEvent);
			}
			
			List<AlarmOccurrenceContext> latestAlarmOccurance = NewAlarmAPI.getLatestAlarmOccurance(new ArrayList<>(eventsMap.keySet()));
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
			
			for (Map.Entry<String, List<BaseEventContext>> entry : eventsMap.entrySet()) {
				List<BaseEventContext> baseEvents = entry.getValue();
				for (BaseEventContext baseEvent : baseEvents) {
					processEventToAlarm(baseEvent);
				}
				PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(entry.getKey());
				
				List<AlarmOccurrenceContext> list = new ArrayList<>(pointedList);
				for (AlarmOccurrenceContext alarmOccurrence : list) {
					if (!alarmOccurrence.equals(pointedList.getLastRecord()) && !alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
						BaseAlarmContext alarm = alarmOccurrence.getAlarm();
						BaseEventContext createdEvent = BaseEventContext.createNewEvent(alarm.getTypeEnum(), alarm.getResource(), AlarmAPI.getAlarmSeverity("Clear"), "Automated Clear Event", alarm.getKey(), alarmOccurrence.getCreatedTime());
						baseEvents.add(createdEvent);
						this.baseEvents.add(createdEvent);
						processEventToAlarm(createdEvent);
					}
				}
			}
			
			saveRecords();
			context.put("alarmOccurrenceMap", alarmOccurrenceMap);
			context.put("alarmMap", alarmMap);
		}
		return false;
	}

	private void saveRecords() throws Exception {
		
	}

	private void processEventToAlarm(BaseEventContext baseEvent) throws Exception {
		if (baseEvent.getEventStateEnum() != EventState.IGNORED) {
			if(baseEvent.getSeverityString().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
				baseEvent.setEventState(EventState.IGNORED);
			}
			else {
				addOrUpdateAlarm(baseEvent);
			}
			baseEvent.setInternalState(EventInternalState.COMPLETED);
		}
	}

	private void addOrUpdateAlarm(BaseEventContext baseEvent) throws Exception {
		PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(baseEvent.getMessageKey());
		if (pointedList == null) {
			pointedList = new PointedList<>();
			alarmOccurrenceMap.put(baseEvent.getMessageKey(), pointedList);
		}
		AlarmOccurrenceContext alarmOccurrence = pointedList.isEmpty() ? null : pointedList.getCurrentRecord();
		
		boolean mostRecent = pointedList.isCurrentLast();
		if (alarmOccurrence == null) {
			// Only for newly creating alarm
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent);
			pointedList.add(alarmOccurrence);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY))) {
			// create alarm occurrence
			mostRecent = baseEvent.getCreatedTime() > pointedList.getLastRecord().getCreatedTime();
			
			int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
			if (alarmOccurrence.getCreatedTime() < baseEvent.getCreatedTime()) {
				oldObjectIndex ++;
			}
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent);
			if (mostRecent) {
				pointedList.add(alarmOccurrence);
				pointedList.moveNext();
			} else {
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else {
			// if alarm is not cleared, only update in local object.
			if (baseEvent.getCreatedTime() < alarmOccurrence.getCreatedTime()) {
				int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
				alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent);
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}
			
			NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent, mostRecent);
			baseEvent.setEventState(EventState.ALARM_UPDATED);
		}
		
		alarmMap.put(baseEvent.getMessageKey(), alarmOccurrence.getAlarm());
		baseEvent.setAlarmOccurrence(alarmOccurrence);
	}

	private void updateEvent(BaseEventContext baseEvent) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(NewEventAPI.getEventModuleName(baseEvent.getAlarmType()));
		UpdateRecordBuilder<BaseEventContext> builder = new UpdateRecordBuilder<BaseEventContext>()
				.moduleName(module.getName())
				.fields(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(baseEvent.getId(), module))
				;
		builder.update(baseEvent);
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
