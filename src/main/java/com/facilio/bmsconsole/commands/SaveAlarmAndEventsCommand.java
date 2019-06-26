package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class SaveAlarmAndEventsCommand implements Command, PostTransactionCommand {

	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap;

	@Override
	public boolean execute(Context context) throws Exception {
//		Map<Type, List<BaseEventContext>> eventsMap = (Map<Type, List<BaseEventContext>>) context.get("eventMap");
		List<BaseEventContext> eventList = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		Map<String, BaseAlarmContext> alarmMap = (Map<String, BaseAlarmContext>) context.get("alarmMap");
		alarmOccurrenceMap = (Map<String, PointedList<AlarmOccurrenceContext>>) context.get("alarmOccurrenceMap");
		
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
			for (Map.Entry<String, PointedList<AlarmOccurrenceContext>> entry : alarmOccurrenceMap.entrySet()) {
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
		
		Map<Type, List<BaseEventContext>>  eventsMap = new HashMap<>();
		for (BaseEventContext baseEvent : eventList) {
			List<BaseEventContext> list = eventsMap.get(baseEvent.getAlarmType());
			if (list == null) {
				list = new ArrayList<>();
				eventsMap.put(baseEvent.getAlarmType(), list);
			}
			list.add(baseEvent);
		}
		if (MapUtils.isNotEmpty(eventsMap)) {
			for (Type type : eventsMap.keySet()) {
				String moduleName = NewEventAPI.getEventModuleName(type);
				InsertRecordBuilder<BaseEventContext> builder = new InsertRecordBuilder<BaseEventContext>()
						.moduleName(moduleName)
						.fields(modBean.getAllFields(moduleName));
				builder.addRecords(eventsMap.get(type));
				builder.save();
			}
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
