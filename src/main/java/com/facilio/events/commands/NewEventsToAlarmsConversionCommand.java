package com.facilio.events.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

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
		updateEvent(baseEvent);
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
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY))) {
			// create alarm occurrence
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else {
			NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent);
			baseEvent.setEventState(EventState.ALARM_UPDATED);
		}
		
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

}
