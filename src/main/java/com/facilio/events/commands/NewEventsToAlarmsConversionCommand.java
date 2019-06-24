package com.facilio.events.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

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

public class NewEventsToAlarmsConversionCommand implements Command, PostTransactionCommand {

	private Map<String, List<BaseEventContext>> eventsMap = new HashMap<>();
	private Map<String, Stack<AlarmOccurrenceContext>> alarmOccurrenceMap = new HashMap<>();
	private Map<String, BaseAlarmContext> alarmMap = new HashMap<>();
//	private Map<String, MutablePair<BaseEventContext, AlarmOccurrenceContext>> occurrenceMap = new HashMap<>();
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
			
			for (Map.Entry<String, List<BaseEventContext>> entry : eventsMap.entrySet()) {
				for (BaseEventContext baseEvent : entry.getValue()) {
					processEventToAlarm(baseEvent);
				}
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			
			if (MapUtils.isNotEmpty(alarmMap)) {
				for (BaseAlarmContext baseAlarm : alarmMap.values()) {
					if (baseAlarm.getId() > 0) {
						UpdateRecordBuilder<BaseAlarmContext> builder = new UpdateRecordBuilder<BaseAlarmContext>()
								.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
								.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))))
								.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
						builder.update(baseAlarm);
					}
					else {
						InsertRecordBuilder<BaseAlarmContext> builder = new InsertRecordBuilder<BaseAlarmContext>()
								.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
								.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
						builder.insert(baseAlarm);
					}
				}
			}
			
			if (MapUtils.isNotEmpty(alarmOccurrenceMap)) {
				List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
				InsertRecordBuilder<AlarmOccurrenceContext> builder = new InsertRecordBuilder<AlarmOccurrenceContext>()
						.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
						.fields(allFields);
				
				List<AlarmOccurrenceContext> records = new ArrayList<>();
				for (Map.Entry<String, Stack<AlarmOccurrenceContext>> entry : alarmOccurrenceMap.entrySet()) {
					for (AlarmOccurrenceContext alarmOccurrenceContext : entry.getValue()) {
						if (alarmOccurrenceContext.getId() > 0) {
							UpdateRecordBuilder<AlarmOccurrenceContext> updateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
									.moduleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE)
									.andCondition(CriteriaAPI.getIdCondition(alarmOccurrenceContext.getId(), modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE)))
									.fields(allFields);
							updateBuilder.update(alarmOccurrenceContext);
						} else {
							records.add(alarmOccurrenceContext);
						}
					}
				}
				builder.addRecords(records);
				builder.save();
			}
			
			if (MapUtils.isNotEmpty(eventsMap)) {
				for (List<BaseEventContext> baseEvents : eventsMap.values()) {
					for (BaseEventContext baseEvent : baseEvents) {
						updateEvent(baseEvent);
					}
				}
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
//		updateEvent(baseEvent);
	}

	private void addOrUpdateAlarm(BaseEventContext baseEvent) throws Exception {
		Stack<AlarmOccurrenceContext> stack = alarmOccurrenceMap.get(baseEvent.getMessageKey());
		if (stack == null) {
			stack = new Stack<>();
			alarmOccurrenceMap.put(baseEvent.getMessageKey(), stack);
		}
		AlarmOccurrenceContext  alarmOccurrence = stack.isEmpty() ? null : stack.peek();
		
		if (alarmOccurrence == null) {
			// if no similar event occurred, take from db
			if (baseEvent.getAlarmOccurrence() != null) {
				alarmOccurrence = baseEvent.getAlarmOccurrence();
			}
			else {
				alarmOccurrence = NewAlarmAPI.getLatestAlarmOccurance(baseEvent.getMessageKey(), baseEvent.getAlarmType());
			}
		}

		if (alarmOccurrence == null) {
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent);
			stack.add(alarmOccurrence);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else if (alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY))) {
			// create alarm occurrence
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent);
			stack.add(alarmOccurrence);
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else {
			// if alarm is not cleared, only update in local object.
			NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent);
			baseEvent.setEventState(EventState.ALARM_UPDATED);
			
			// add if its not found in stack
			if (!stack.contains(alarmOccurrence)) {
				stack.add(alarmOccurrence);
			}
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
	
	@Override
	public boolean postExecute() throws Exception {
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			List<Long> alarmOccurrenceIds = new ArrayList<>();
			for (BaseEventContext baseEvent : baseEvents) {
				AlarmOccurrenceContext alarmOccurrence = baseEvent.getAlarmOccurrence();
				if (!alarmOccurrenceIds.contains(alarmOccurrence)) {
					alarmOccurrenceIds.add(alarmOccurrence.getId());
				}
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule alarmOccurrenceModule = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			FacilioField noOfEventsField = modBean.getField("noOfEvents", alarmOccurrenceModule.getName());
			
			FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
			Map<String, FacilioField> eventFieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
			List<FacilioField> fields = new ArrayList<>();
			FacilioField alarmOccurrenceField = eventFieldMap.get("alarmOccurrence");
			fields.add(alarmOccurrenceField);
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(eventModule.getTableName())
					.select(fields)
					.groupBy(alarmOccurrenceField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(alarmOccurrenceField, StringUtils.join(alarmOccurrenceIds, ","), NumberOperators.EQUALS));
			
			List<Map<String, Object>> list = builder.get();
			Map<String, Object> updateMap = new HashMap<>();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("alarmOccurrence")).longValue();
				int numberOfEvents = ((Number) map.get("count")).intValue();

				updateMap.put("noOfEvents", numberOfEvents);

				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(alarmOccurrenceModule)
						.fields(Collections.singletonList(noOfEventsField))
						.andCondition(CriteriaAPI.getIdCondition(id, alarmOccurrenceModule));

				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
		return false;
	}

}
