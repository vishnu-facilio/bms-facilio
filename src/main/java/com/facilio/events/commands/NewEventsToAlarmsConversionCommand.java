package com.facilio.events.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;

public class NewEventsToAlarmsConversionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<BaseEventContext> baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			for (BaseEventContext baseEvent : baseEvents) {
				processEventToAlarm(baseEvent);
			}
		}
		return false;
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
		AlarmOccurrenceContext alarmOccurrence;
		if (baseEvent.getAlarmOccurrence() != null) {
			alarmOccurrence = baseEvent.getAlarmOccurrence();
		}
		else {
			alarmOccurrence = NewAlarmAPI.getLatestAlarmOccurance(baseEvent.getMessageKey(), baseEvent.getAlarmType());
		}
		
		if (alarmOccurrence == null) {
			// there is no active alarm for this event
			//create alarm
//			NewAlarmAPI.createAlarm(alarm);
//			BaseAlarmContext alarm = baseEvent.addOrUpdateAlarm(true);
//			NewAlarmAPI.createAlarm(alarm);
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent);
		}
		else if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY))) {
			// create alarm occurrence
			
		}
		else {
			NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent);
		}
		
		baseEvent.setAlarmOccurrence(alarmOccurrence);
		updateEvent(baseEvent);
	}

	private void updateEvent(BaseEventContext baseEvent) {
		// TODO Auto-generated method stub
		
	}

}
